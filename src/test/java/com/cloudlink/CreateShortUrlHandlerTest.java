package com.cloudlink;

import com.cloudlink.handlers.CreateShortUrlHandler;
import com.cloudlink.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateShortUrlHandlerTest {

    private UrlService service;
    private CreateShortUrlHandler handler;

    @BeforeEach
    void setUp() {
        service = mock(UrlService.class);
        handler = new CreateShortUrlHandler(service);
    }

    @Test
    void createShortUrl_returnsGeneratedCode() {
        when(service.shortenUrl("https://example.com"))
                .thenReturn("abc12345");

        String result = handler.createShortUrl("https://example.com");

        assertEquals("abc12345", result);
    }

    @Test
    void createShortUrl_callsServiceOnce() {
        when(service.shortenUrl(anyString()))
                .thenReturn("abc12345");

        handler.createShortUrl("https://example.com");

        verify(service, times(1))
                .shortenUrl("https://example.com");
    }
}