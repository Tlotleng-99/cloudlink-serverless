
package com.cloudlink.dto;

public class CreateShortUrlResponse {

    private String shortCode;

    public CreateShortUrlResponse() {
    }

    public CreateShortUrlResponse(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}
