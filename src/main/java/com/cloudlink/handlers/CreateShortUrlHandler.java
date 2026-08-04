package com.cloudlink.handlers;

import com.cloudlink.service.UrlService;

//my hanlder speaks to the outside world
public class CreateShortUrlHandler {
    private final UrlService urlService;

    public CreateShortUrlHandler(UrlService urlService){
        this.urlService = urlService;
    }

    public String createShortUrl(String originalUrl){
        return urlService.shortenUrl((originalUrl));
    }
}
