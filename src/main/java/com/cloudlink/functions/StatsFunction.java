package com.cloudlink.functions;

import com.cloudlink.dto.StatsRequest;
import com.cloudlink.dto.StatsResponse;
import com.cloudlink.service.UrlService;

public class StatsFunction {

    private final UrlService urlService;

    public StatsFunction(UrlService urlService) {
        this.urlService = urlService;
    }

    public StatsResponse handle(StatsRequest request) {

        var mapping = urlService.getStats(request.getShortCode());

        return new StatsResponse(
                mapping.getShortCode(),
                mapping.getOriginalUrl(),
                mapping.getClickCount()
        );
    }
}