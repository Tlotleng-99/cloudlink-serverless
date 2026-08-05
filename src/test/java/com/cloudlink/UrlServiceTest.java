package com.cloudlink;

import com.cloudlink.exception.InvalidUrlException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.UrlMappingRepository;
import com.cloudlink.service.UrlService;
import com.cloudlink.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    private UrlMappingRepository repository;
    private ShortCodeGenerator generator;
    private UrlService service;

    @BeforeEach
    void setUp() {
        repository = mock(UrlMappingRepository.class);
        generator = new ShortCodeGenerator();   // Use the real generator
        service = new UrlService(repository, generator);
    }

    @Test
    void shortenUrl_returnsGeneratedCode() {
        String code = service.shortenUrl("https://example.com");

        assertNotNull(code);
        assertEquals(8, code.length());
    }

    @Test
    void shortenUrl_savesMapping() {
        service.shortenUrl("https://example.com");

        verify(repository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void redirect_returnsOriginalUrl_whenCodeExists() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        when(repository.findByCode("abc12345"))
                .thenReturn(Optional.of(mapping));

        String result = service.redirect("abc12345");

        assertEquals("https://example.com", result);
    }

    @Test
    void redirect_throwsException_whenCodeDoesNotExist() {
        when(repository.findByCode("missing"))
                .thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () ->{
        service.redirect("missing");

        });
    }

    @Test
    void redirect_incrementsClickCount() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        when(repository.findByCode("abc12345"))
                .thenReturn(Optional.of(mapping));

        service.redirect("abc12345");

        assertEquals(1L, mapping.getClickCount());
    }

    @Test
    void redirect_updatesRepository() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        when(repository.findByCode("abc12345"))
                .thenReturn(Optional.of(mapping));

        service.redirect("abc12345");

        verify(repository).update(mapping);
    }
    @Test
    void shortenUrl_throwsException_whenUrlIsInvalid() {

        assertThrows(InvalidUrlException.class, () -> {
            service.shortenUrl("not-a-url");
        });
    }

    @Test
    void redirect_throwsException_whenUrlHasExpired() {

        UrlMapping mapping = UrlMapping.createNew(
                "abc123",
                "https://example.com",
                Instant.now().minusSeconds(60) // Expired 1 minute ago
        );

        when(repository.findByCode("abc123"))
                .thenReturn(Optional.of(mapping));

        assertThrows(UrlNotFoundException.class, () -> {
            service.redirect("abc123");
        });
    }

    @Test
    void shortenUrl_withExpiration_savesExpirationDate() {

        Instant expiresAt = Instant.now().plusSeconds(3600);

        service.shortenUrl("https://example.com", expiresAt);

        ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);

        verify(repository).save(captor.capture());

        UrlMapping saved = captor.getValue();

        assertEquals(expiresAt, saved.getExpiresAt());
    }
}