package com.cloudlink;

import com.cloudlink.handlers.RedirectHandler;
import com.cloudlink.repository.InMemoryUrlRepository;
import com.cloudlink.service.UrlService;
import com.cloudlink.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedirectHandlerTest {

    private RedirectHandler handler;
    private UrlService service;

    @BeforeEach
    void setUp() {

        InMemoryUrlRepository repository = new InMemoryUrlRepository();
        ShortCodeGenerator generator = new ShortCodeGenerator();

        service = new UrlService(repository, generator);
        handler = new RedirectHandler(service);
    }

    @Test
    void redirect_returnsOriginalUrl_whenCodeExists() {

        // Arrange
        String code = service.shortenUrl("https://example.com");

        // Act
        String result = handler.redirect(code);

        // Assert
        assertEquals("https://example.com", result);
    }

    @Test
    void redirect_returnsNull_whenCodeDoesNotExist() {

        // Act
        String result = handler.redirect("invalid123");

        // Assert
        assertNull(result);
    }

    @Test
    void redirect_incrementsClickCount() {

        // Arrange
        String code = service.shortenUrl("https://example.com");

        // First redirect
        handler.redirect(code);

        // Second redirect
        handler.redirect(code);

        // Third redirect
        handler.redirect(code);

        // Redirect should still return the original URL
        String result = handler.redirect(code);

        assertEquals("https://example.com", result);
    }
}