package com.cloudlink;

import com.cloudlink.exception.CodeAlreadyExistsException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.DynamoDbUrlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Talks to a REAL DynamoDB table, so it only runs when CLOUDLINK_TABLE is set:
 *
 * <pre>
 *   export CLOUDLINK_TABLE=cloudlink-url-mappings
 *   export AWS_REGION=af-south-1        # or the region you deployed the table to
 *   mvn test -Dtest=DynamoDbUrlRepositoryIntegrationTest
 * </pre>
 *
 * Credentials come from the normal AWS chain (aws configure, env vars, ...). Each test uses
 * random codes and URLs and deletes them afterwards.
 */
@EnabledIfEnvironmentVariable(named = "CLOUDLINK_TABLE", matches = ".+")
class DynamoDbUrlRepositoryIntegrationTest {

    private DynamoDbClient client;
    private DynamoDbUrlRepository repository;
    private String code;
    private String url;

    @BeforeEach
    void setUp() {
        client = DynamoDbClient.create();
        repository = new DynamoDbUrlRepository(client, System.getenv("CLOUDLINK_TABLE"));
        code = UUID.randomUUID().toString().substring(0, 8);
        url = "https://example.com/" + UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        repository.delete(code);
        client.close();
    }

    @Test
    void saveThenFind_roundTrips() {
        repository.save(UrlMapping.createNew(code, url, null));

        UrlMapping found = repository.findByCode(code).orElseThrow();

        assertEquals(url, found.getOriginalUrl());
        assertEquals(0L, found.getClickCount());
        assertNull(found.getExpiresAt());
    }

    @Test
    void save_refusesToOverwriteExistingCode() {
        repository.save(UrlMapping.createNew(code, url, null));

        assertThrows(CodeAlreadyExistsException.class, () ->
                repository.save(UrlMapping.createNew(code, "https://other.example.com", null)));

        assertEquals(url, repository.findByCode(code).orElseThrow().getOriginalUrl());
    }

    @Test
    void incrementClickCount_countsEveryClick() {
        repository.save(UrlMapping.createNew(code, url, null));

        repository.incrementClickCount(code);
        repository.incrementClickCount(code);
        repository.incrementClickCount(code);

        assertEquals(3L, repository.findByCode(code).orElseThrow().getClickCount());
    }

    @Test
    void incrementClickCount_throwsForUnknownCode() {
        assertThrows(UrlNotFoundException.class, () -> repository.incrementClickCount(code));
    }

    @Test
    void findByOriginalUrl_findsTheMappingThroughTheIndex() throws InterruptedException {
        repository.save(UrlMapping.createNew(code, url, null));

        // global secondary indexes are eventually consistent, so allow a few seconds
        Optional<UrlMapping> found = Optional.empty();
        for (int attempt = 0; attempt < 10 && found.isEmpty(); attempt++) {
            found = repository.findByOriginalUrl(url);
            if (found.isEmpty()) {
                Thread.sleep(500);
            }
        }

        assertEquals(code, found.orElseThrow().getShortCode());
    }

    @Test
    void delete_removesTheMapping() {
        repository.save(UrlMapping.createNew(code, url, null));

        repository.delete(code);

        assertTrue(repository.findByCode(code).isEmpty());
    }
}