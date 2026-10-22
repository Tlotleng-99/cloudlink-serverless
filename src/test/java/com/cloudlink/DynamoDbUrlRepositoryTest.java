package com.cloudlink;

import com.cloudlink.exception.CodeAlreadyExistsException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.DynamoDbUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests: the DynamoDB client is mocked, so these run without AWS. */
class DynamoDbUrlRepositoryTest {

    private static final String TABLE = "test-table";

    private DynamoDbClient client;
    private DynamoDbUrlRepository repository;

    @BeforeEach
    void setUp() {
        client = mock(DynamoDbClient.class);
        repository = new DynamoDbUrlRepository(client, TABLE);
    }

    private static AttributeValue s(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static AttributeValue n(String value) {
        return AttributeValue.builder().n(value).build();
    }

    private static Map<String, AttributeValue> storedItem(String expiresAtEpoch) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortCode", s("abc12345"));
        item.put("originalUrl", s("https://example.com"));
        item.put("clickCount", n("5"));
        item.put("createdAt", s("2026-01-01T10:00:00Z"));
        if (expiresAtEpoch != null) {
            item.put("expiresAt", n(expiresAtEpoch));
        }
        return item;
    }



    @Test
    void save_writesAllAttributes_andRefusesToOverwrite() {
        UrlMapping mapping = UrlMapping.createNew("abc12345", "https://example.com", null);

        repository.save(mapping);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        PutItemRequest request = captor.getValue();

        assertEquals(TABLE, request.tableName());
        assertEquals("attribute_not_exists(shortCode)", request.conditionExpression());
        assertEquals("abc12345", request.item().get("shortCode").s());
        assertEquals("https://example.com", request.item().get("originalUrl").s());
        assertEquals("0", request.item().get("clickCount").n());
        assertEquals(64, request.item().get("urlHash").s().length()); // SHA-256 as hex
        assertFalse(request.item().containsKey("expiresAt"));
    }

    @Test
    void save_storesExpiryAsEpochSeconds() {
        Instant expiresAt = Instant.parse("2030-01-01T00:00:00Z");

        repository.save(UrlMapping.createNew("abc12345", "https://example.com", expiresAt));

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());

        assertEquals(Long.toString(expiresAt.getEpochSecond()), captor.getValue().item().get("expiresAt").n());
    }

    @Test
    void save_throwsCodeAlreadyExists_whenConditionFails() {
        when(client.putItem(any(PutItemRequest.class)))
                .thenThrow(ConditionalCheckFailedException.builder().message("exists").build());

        assertThrows(CodeAlreadyExistsException.class, () ->
                repository.save(UrlMapping.createNew("abc12345", "https://example.com", null)));
    }


    @Test
    void findByCode_rebuildsStoredMapping() {
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(storedItem("1893456000")).build());

        UrlMapping mapping = repository.findByCode("abc12345").orElseThrow();

        assertEquals("abc12345", mapping.getShortCode());
        assertEquals("https://example.com", mapping.getOriginalUrl());
        assertEquals(5L, mapping.getClickCount());
        assertEquals(Instant.parse("2026-01-01T10:00:00Z"), mapping.getCreatedAt());
        assertEquals(Instant.ofEpochSecond(1893456000L), mapping.getExpiresAt());
    }

    @Test
    void findByCode_usesConsistentRead() {
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(storedItem(null)).build());

        repository.findByCode("abc12345");

        ArgumentCaptor<GetItemRequest> captor = ArgumentCaptor.forClass(GetItemRequest.class);
        verify(client).getItem(captor.capture());

        assertTrue(captor.getValue().consistentRead());
        assertEquals("abc12345", captor.getValue().key().get("shortCode").s());
    }

    @Test
    void findByCode_hasNoExpiry_whenAttributeMissing() {
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(storedItem(null)).build());

        assertNull(repository.findByCode("abc12345").orElseThrow().getExpiresAt());
    }

    @Test
    void findByCode_returnsEmpty_whenItemDoesNotExist() {
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().build());

        assertTrue(repository.findByCode("missing").isEmpty());
    }



    @Test
    void update_throwsNotFound_whenItemDoesNotExist() {
        when(client.putItem(any(PutItemRequest.class)))
                .thenThrow(ConditionalCheckFailedException.builder().message("missing").build());

        assertThrows(UrlNotFoundException.class, () ->
                repository.update(UrlMapping.createNew("abc12345", "https://example.com", null)));
    }

    @Test
    void incrementClickCount_usesAtomicAdd() {
        repository.incrementClickCount("abc12345");

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest request = captor.getValue();

        assertEquals(TABLE, request.tableName());
        assertEquals("ADD clickCount :one", request.updateExpression());
        assertEquals("attribute_exists(shortCode)", request.conditionExpression());
        assertEquals("1", request.expressionAttributeValues().get(":one").n());
        assertEquals("abc12345", request.key().get("shortCode").s());
    }

    @Test
    void incrementClickCount_throwsNotFound_whenItemDoesNotExist() {
        when(client.updateItem(any(UpdateItemRequest.class)))
                .thenThrow(ConditionalCheckFailedException.builder().message("missing").build());

        assertThrows(UrlNotFoundException.class, () -> repository.incrementClickCount("missing"));
    }

    @Test
    void delete_removesItemByCode() {
        repository.delete("abc12345");

        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(client).deleteItem(captor.capture());

        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals("abc12345", captor.getValue().key().get("shortCode").s());
    }


    @Test
    void findByOriginalUrl_queriesTheUrlHashIndex() {
        when(client.query(any(QueryRequest.class)))
                .thenReturn(QueryResponse.builder().items(storedItem(null)).build());

        Optional<UrlMapping> result = repository.findByOriginalUrl("https://example.com");

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(client).query(captor.capture());

        assertEquals("urlHash-index", captor.getValue().indexName());
        assertEquals("urlHash = :hash", captor.getValue().keyConditionExpression());
        assertEquals(64, captor.getValue().expressionAttributeValues().get(":hash").s().length());
        assertEquals("abc12345", result.orElseThrow().getShortCode());
    }

    @Test
    void findByOriginalUrl_ignoresExpiredMappings() {
        long oneMinuteAgo = Instant.now().minusSeconds(60).getEpochSecond();
        when(client.query(any(QueryRequest.class)))
                .thenReturn(QueryResponse.builder().items(storedItem(Long.toString(oneMinuteAgo))).build());

        assertTrue(repository.findByOriginalUrl("https://example.com").isEmpty());
    }

    @Test
    void findByOriginalUrl_ignoresHashCollisions() {
        // the index returned an item whose URL is different from the one we asked for
        when(client.query(any(QueryRequest.class)))
                .thenReturn(QueryResponse.builder().items(storedItem(null)).build());

        assertTrue(repository.findByOriginalUrl("https://some-other-site.com").isEmpty());
    }
}