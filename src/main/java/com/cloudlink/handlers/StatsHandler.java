package com.cloudlink.handlers;

import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;
import com.cloudlink.service.UrlService;

public class StatsHandler {
    private final UrlService urlService;

    public StatsHandler(UrlService urlService) {
        this.urlService = urlService;
    }

    /** @return the mapping for the code, or null if there is no such short URL */
    public UrlMapping getStats(String code) {
        try {
            return urlService.getStats(code);
        } catch (UrlNotFoundException e) {
            return null;
        }
    }
}