package com.cloudlink.dto;

public class RedirectResponse {

    private String originalUrl;

    public RedirectResponse() {
    }

    public RedirectResponse(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}