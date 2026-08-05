package com.cloudlink;

import com.cloudlink.handlers.CreateShortUrlHandler;
import com.cloudlink.repository.InMemoryUrlRepository;
import com.cloudlink.service.UrlService;
import com.cloudlink.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateShortUrlHandlerTest {

    private CreateShortUrlHandler handler;

    @BeforeEach
    void setUp() {
        InMemoryUrlRepository repository = new InMemoryUrlRepository();
        ShortCodeGenerator generator = new ShortCodeGenerator();
        UrlService service = new UrlService(repository, generator);

        handler = new CreateShortUrlHandler(service);
    }

    @Test
    void createShortUrl_returnsShortCode() {
        String code = handler.createShortUrl("https://example.com");

        assertNotNull(code);
        assertEquals(8, code.length());
    }

    @Test
    void createShortUrl_returnsDifferentCodes() {
        String first = handler.createShortUrl("https://google.com");
        String second = handler.createShortUrl("https://github.com");

        assertNotEquals(first, second);
    }
}