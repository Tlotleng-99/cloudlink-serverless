package com.cloudlink;


import com.cloudlink.model.UrlMapping;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


import static org.junit.jupiter.api.Assertions.*;


public class UrlMappingTest {
    @Test
    void createNew_setsClickCountToZeroAndCreatedAtToNow() {
        // Arrange + Act
        UrlMapping mapping = UrlMapping.createNew("aZ3xQ1", "https://example.com", null);

        // Assert
        assertEquals("aZ3xQ1", mapping.getShortCode());
        assertEquals("https://example.com", mapping.getOriginalUrl());
        assertEquals(0L, mapping.getClickCount());
        assertNotNull(mapping.getCreatedAt());
        assertNull(mapping.getExpiresAt());
    }

    @Test
    void incrementClickCount_increasesCountByOne() {
        // Arrange
        UrlMapping mapping = UrlMapping.createNew("aZ3xQ1", "https://example.com", null);

        // Act
        mapping.incrementClickCount();
        mapping.incrementClickCount();
        mapping.incrementClickCount();

        // Assert
        assertEquals(3L, mapping.getClickCount());
    }

    @Test
    void isExpired_returnsFalse_whenExpiresAtIsNull() {
        // Arrange
        UrlMapping mapping = UrlMapping.createNew("aZ3xQ1", "https://example.com", null);

        // Act + Assert
        assertFalse(mapping.isExpired());
    }

    @Test
    void isExpired_returnsFalse_whenExpiresAtIsInTheFuture() {
        // Arrange
        Instant oneHourFromNow = Instant.now().plus(1, ChronoUnit.HOURS);
        UrlMapping mapping = UrlMapping.createNew("aZ3xQ1", "https://example.com", oneHourFromNow);

        // Act + Assert
        assertFalse(mapping.isExpired());
    }

    @Test
    void isExpired_returnsTrue_whenExpiresAtIsInThePast() {
        // Arrange
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        UrlMapping mapping = UrlMapping.createNew("aZ3xQ1", "https://example.com", oneHourAgo);

        // Act + Assert
        assertTrue(mapping.isExpired());
    }

    @Test
    void equals_returnsTrue_forMappingsWithSameShortCode() {
        // Arrange
        UrlMapping mapping1 = UrlMapping.createNew("aZ3xQ1", "https://example.com", null);
        UrlMapping mapping2 = UrlMapping.createNew("aZ3xQ1", "https://a-totally-different-url.com", null);

        // Act + Assert
        assertEquals(mapping1, mapping2);
    }

    @Test
    void equals_returnsFalse_forMappingsWithDifferentShortCode() {
        // Arrange
        UrlMapping mapping1 = UrlMapping.createNew("aZ3xQ1", "https://example.com", null);
        UrlMapping mapping2 = UrlMapping.createNew("bY7wR2", "https://example.com", null);

        // Act + Assert
        assertNotEquals(mapping1, mapping2);
    }

}
