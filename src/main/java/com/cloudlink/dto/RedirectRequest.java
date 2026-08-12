package com.cloudlink.dto;

public class RedirectRequest {

    private String shortCode;

    public RedirectRequest() {
    }

    public RedirectRequest(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}