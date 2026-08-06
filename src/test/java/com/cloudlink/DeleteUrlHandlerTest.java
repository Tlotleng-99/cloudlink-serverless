package com.cloudlink;

import com.cloudlink.handlers.DeleteUrlHandler;
import com.cloudlink.service.UrlService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DeleteUrlHandlerTest {

    @Test
    void delete_callsService() {

        UrlService service = mock(UrlService.class);

        DeleteUrlHandler handler = new DeleteUrlHandler(service);

        handler.delete("abc123");

        verify(service).deleteUrl("abc123");
    }
}
