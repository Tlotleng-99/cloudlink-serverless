package com.cloudlink.functions;

import com.cloudlink.dto.CreateShortUrlRequest;
import com.cloudlink.dto.CreateShortUrlResponse;
import com.cloudlink.handlers.CreateShortUrlHandler;

public class CreateShortUrlFunction {
    private final CreateShortUrlHandler handler;
    public CreateShortUrlFunction(CreateShortUrlHandler handler) {
        this.handler = handler;
    }
    public CreateShortUrlResponse handleRequest(CreateShortUrlRequest request) {
        String shortCode = handler.createShortUrl(request.getOriginalUrl());
        return new CreateShortUrlResponse(shortCode);
    }
}