package com.cloudlink;

import com.cloudlink.handlers.StatsHandler;
import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.InMemoryUrlRepository;
import com.cloudlink.service.UrlService;
import com.cloudlink.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatsHandlerTest {

    private StatsHandler handler;
    private UrlService service;

    @BeforeEach
    void setUp() {

        InMemoryUrlRepository repository = new InMemoryUrlRepository();
        ShortCodeGenerator generator = new ShortCodeGenerator();

        service = new UrlService(repository, generator);
        handler = new StatsHandler(service);
    }

    @Test
    void getStats_returnsUrlMapping_whenCodeExists() {

        // Arrange
        String code = service.shortenUrl("https://example.com");

        // Act
        UrlMapping mapping = handler.getStats(code);

        // Assert
        assertNotNull(mapping);
        assertEquals(code, mapping.getShortCode());
        assertEquals("https://example.com", mapping.getOriginalUrl());
        assertEquals(0, mapping.getClickCount());
    }

    @Test
    void getStats_returnsNull_whenCodeDoesNotExist() {

        // Act
        UrlMapping mapping = handler.getStats("invalid123");

        // Assert
        assertNull(mapping);
    }
}