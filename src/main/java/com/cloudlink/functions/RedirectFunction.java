package com.cloudlink.functions;

import com.cloudlink.dto.RedirectRequest;
import com.cloudlink.dto.RedirectResponse;
import com.cloudlink.service.UrlService;

public class RedirectFunction {

    private final UrlService urlService;

    public RedirectFunction(UrlService urlService) {
        this.urlService = urlService;
    }

    public RedirectResponse handle(RedirectRequest request) {

        String originalUrl = urlService.redirect(request.getShortCode());

        return new RedirectResponse(originalUrl);
    }
}