package com.cloudlink.handlers;

import com.cloudlink.model.UrlMapping;
import com.cloudlink.service.UrlService;

public class StatsHandler {
    private final UrlService urlService;

    public StatsHandler(UrlService urlService) {
        this.urlService = urlService;
    }

    public UrlMapping getStats(String code){
        return urlService.getStats(code);
    }
}
