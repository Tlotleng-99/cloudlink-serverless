package com.cloudlink.dto;

public class StatsRequest {

    private String shortCode;

    public StatsRequest() {
    }

    public StatsRequest(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}