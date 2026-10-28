package com.cloudlink;

import com.cloudlink.exception.InvalidUrlException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.exception.CodeAlreadyExistsException;
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
    void redirect_incrementsClickCountInRepository() {
        UrlMapping mapping =
                UrlMapping.createNew("abc12345", "https://example.com", null);

        when(repository.findByCode("abc12345"))
                .thenReturn(Optional.of(mapping));

        service.redirect("abc12345");

        verify(repository).incrementClickCount("abc12345");
    }

    @Test
    void redirect_doesNotCountClick_whenUrlHasExpired() {
        UrlMapping expired = UrlMapping.createNew(
                "abc12345", "https://example.com", Instant.now().minusSeconds(60));

        when(repository.findByCode("abc12345"))
                .thenReturn(Optional.of(expired));

        assertThrows(UrlNotFoundException.class, () -> service.redirect("abc12345"));

        verify(repository, never()).incrementClickCount(any());
    }

    @Test
    void shortenUrl_retriesWithNewCode_whenCodeAlreadyExists() {
        doThrow(new CodeAlreadyExistsException("taken"))
                .doNothing()
                .when(repository).save(any(UrlMapping.class));

        String code = service.shortenUrl("https://example.com");

        assertNotNull(code);
        verify(repository, times(2)).save(any(UrlMapping.class));
    }

    @Test
    void shortenUrl_givesUp_whenEveryCodeIsTaken() {
        doThrow(new CodeAlreadyExistsException("taken"))
                .when(repository).save(any(UrlMapping.class));

        assertThrows(IllegalStateException.class, () -> service.shortenUrl("https://example.com"));
    }

    @Test
    void shortenUrl_returnsExistingCode_whenUrlWasAlreadyShortened() {
        UrlMapping existing = UrlMapping.createNew("abc12345", "https://example.com", null);
        when(repository.findByOriginalUrl("https://example.com"))
                .thenReturn(Optional.of(existing));

        String code = service.shortenUrl("https://example.com");

        assertEquals("abc12345", code);
        verify(repository, never()).save(any(UrlMapping.class));
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
    @Test
    void deleteUrl_removesExistingUrl() {

        UrlMapping mapping = UrlMapping.createNew(
                "abc123",
                "https://example.com",
                null
        );

        when(repository.findByCode("abc123"))
                .thenReturn(Optional.of(mapping));

        service.deleteUrl("abc123");

        verify(repository).delete("abc123");
    }
    @Test
    void deleteUrl_throwsException_whenUrlDoesNotExist() {

        when(repository.findByCode("missing"))
                .thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> {
            service.deleteUrl("missing");
        });
    }
}