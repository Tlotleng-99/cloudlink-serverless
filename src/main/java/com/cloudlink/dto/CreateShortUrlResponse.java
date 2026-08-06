package com.cloudlink.dto;

public class CreateShortUrlResponse {
    private String ShortCode;

    public CreateShortUrlResponse(){
    }
    public CreateShortUrlResponse(String ShortCode){
        this.ShortCode = ShortCode;
    }
    public String getShortCode(){
        return ShortCode;
    }
    public void setShortCode(String ShortCode){
        this.ShortCode = ShortCode;
    }
}
