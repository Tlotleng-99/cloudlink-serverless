package com.cloudlink;

import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.InMemoryUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUrlRepositoryTest {

    private InMemoryUrlRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUrlRepository();
    }

    @Test
    void save_storesMapping() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        repository.save(mapping);

        Optional<UrlMapping> result = repository.findByCode("abc12345");

        assertTrue(result.isPresent());
    }

    @Test
    void findByCode_returnsCorrectMapping() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        repository.save(mapping);

        UrlMapping result = repository.findByCode("abc12345").get();

        assertEquals("https://example.com", result.getOriginalUrl());
    }

    @Test
    void findByCode_returnsEmptyWhenCodeDoesNotExist() {
        Optional<UrlMapping> result = repository.findByCode("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void update_replacesExistingMapping() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        repository.save(mapping);

        mapping.incrementClickCount();

        repository.update(mapping);

        UrlMapping updated = repository.findByCode("abc12345").get();

        assertEquals(1L, updated.getClickCount());
    }

    @Test
    void update_keepsOriginalUrl() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        repository.save(mapping);

        repository.update(mapping);

        UrlMapping result = repository.findByCode("abc12345").get();

        assertEquals("https://example.com", result.getOriginalUrl());
    }

    @Test
    void save_multipleMappings() {
        repository.save(
                UrlMapping.createNew("abc12345", "https://google.com", null));

        repository.save(
                UrlMapping.createNew("xyz98765", "https://github.com", null));

        assertTrue(repository.findByCode("abc12345").isPresent());
        assertTrue(repository.findByCode("xyz98765").isPresent());
    }
}