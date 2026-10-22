package com.cloudlink.repository;

import com.cloudlink.exception.CodeAlreadyExistsException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

/**
 * DynamoDB implementation of {@link UrlMappingRepository}.
 *
 * <p>Table layout (see infra/dynamodb-table.yaml):
 * <ul>
 *   <li>partition key: {@code shortCode} (S)</li>
 *   <li>{@code originalUrl} (S), {@code clickCount} (N), {@code createdAt} (S, ISO-8601)</li>
 *   <li>{@code expiresAt} (N, epoch seconds) - optional; also the table's TTL attribute</li>
 *   <li>{@code urlHash} (S, SHA-256 of originalUrl) - partition key of the {@value #URL_HASH_INDEX} index,
 *       used to find an existing short code for a URL without scanning the table</li>
 * </ul>
 */
public class DynamoDbUrlRepository implements UrlMappingRepository {

    public static final String URL_HASH_INDEX = "urlHash-index";

    static final String SHORT_CODE = "shortCode";
    static final String ORIGINAL_URL = "originalUrl";
    static final String URL_HASH = "urlHash";
    static final String CLICK_COUNT = "clickCount";
    static final String CREATED_AT = "createdAt";
    static final String EXPIRES_AT = "expiresAt";

    private final DynamoDbClient client;
    private final String tableName;

    public DynamoDbUrlRepository(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public void save(UrlMapping mapping) {
        try {
            client.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(toItem(mapping))
                    // never overwrite an existing short code
                    .conditionExpression("attribute_not_exists(" + SHORT_CODE + ")")
                    .build());
        } catch (ConditionalCheckFailedException e) {
            throw new CodeAlreadyExistsException(mapping.getShortCode());
        }
    }

    @Override
    public Optional<UrlMapping> findByCode(String code) {
        GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(key(code))
                .consistentRead(true) // a link must work immediately after it is created
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(fromItem(response.item()));
    }

    @Override
    public void update(UrlMapping mapping) {
        try {
            client.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(toItem(mapping))
                    .conditionExpression("attribute_exists(" + SHORT_CODE + ")")
                    .build());
        } catch (ConditionalCheckFailedException e) {
            throw new UrlNotFoundException("Short URL does not exist");
        }
    }

    @Override
    public void incrementClickCount(String code) {
        try {
            // ADD is evaluated by DynamoDB itself, so simultaneous clicks are never lost
            client.updateItem(UpdateItemRequest.builder()
                    .tableName(tableName)
                    .key(key(code))
                    .updateExpression("ADD " + CLICK_COUNT + " :one")
                    .conditionExpression("attribute_exists(" + SHORT_CODE + ")")
                    .expressionAttributeValues(Map.of(":one", number(1)))
                    .build());
        } catch (ConditionalCheckFailedException e) {
            throw new UrlNotFoundException("Short URL does not exist");
        }
    }

    @Override
    public void delete(String code) {
        client.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key(code))
                .build());
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        QueryResponse response = client.query(QueryRequest.builder()
                .tableName(tableName)
                .indexName(URL_HASH_INDEX)
                .keyConditionExpression(URL_HASH + " = :hash")
                .expressionAttributeValues(Map.of(":hash", string(sha256(originalUrl))))
                .build());

        return response.items().stream()
                .map(this::fromItem)
                .filter(mapping -> mapping.getOriginalUrl().equals(originalUrl)) // guards against hash collisions
                .filter(mapping -> !mapping.isExpired())
                .findFirst();
    }

    // ---------------------------------------------------------------- mapping helpers

    private Map<String, AttributeValue> toItem(UrlMapping mapping) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(SHORT_CODE, string(mapping.getShortCode()));
        item.put(ORIGINAL_URL, string(mapping.getOriginalUrl()));
        item.put(URL_HASH, string(sha256(mapping.getOriginalUrl())));
        item.put(CLICK_COUNT, number(mapping.getClickCount()));
        item.put(CREATED_AT, string(mapping.getCreatedAt().toString()));
        if (mapping.getExpiresAt() != null) {
            item.put(EXPIRES_AT, number(mapping.getExpiresAt().getEpochSecond()));
        }
        return item;
    }

    private UrlMapping fromItem(Map<String, AttributeValue> item) {
        long clickCount = item.containsKey(CLICK_COUNT) ? Long.parseLong(item.get(CLICK_COUNT).n()) : 0L;
        Instant expiresAt = item.containsKey(EXPIRES_AT)
                ? Instant.ofEpochSecond(Long.parseLong(item.get(EXPIRES_AT).n()))
                : null;

        return UrlMapping.restore(
                item.get(SHORT_CODE).s(),
                item.get(ORIGINAL_URL).s(),
                clickCount,
                Instant.parse(item.get(CREATED_AT).s()),
                expiresAt);
    }

    private static Map<String, AttributeValue> key(String code) {
        return Map.of(SHORT_CODE, string(code));
    }

    private static AttributeValue string(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static AttributeValue number(long value) {
        return AttributeValue.builder().n(Long.toString(value)).build();
    }

    static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e); // every JVM must support it
        }
    }
}