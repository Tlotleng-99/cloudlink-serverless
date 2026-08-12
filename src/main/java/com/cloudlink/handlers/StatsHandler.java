package com.cloudlink.handlers;

import com.cloudlink.exception.UrlNotFoundException;

import com.cloudlink.service.UrlService;

public class StatsHandler {
    private final UrlService urlService;

    public StatsHandler(UrlService urlService) {
        this.urlService = urlService;
    }


    public Object getStats(String code) {
        try {
            return urlService.getStats(code);
        } catch (UrlNotFoundException e) {
            return null;
        }
    }
}
