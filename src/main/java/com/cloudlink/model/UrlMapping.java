package com.cloudlink.model;
/** the model represnts everything that is a real object in the application**/

public class UrlMapping {
    private String code;
    //generates the code that will be used as a shortener
    private String originalUrl;
    private int clickCount;
    // this will set my click count
    private String createdAT;

    public UrlMapping(String code, String originalUrl, int clickCount, String createdAt){
        this.code = code;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
        this.createdAT = createdAt;

    }
    public String getCode(){
        return code;
    }
    public String getOriginalUrl(){
        return originalUrl;
    }
    public int getClickCount() {
        return clickCount;
    }
    public String getCreatedAT(){
        return createdAT;
    }

    public void setcode(String code){
        this.code = code;
    }

    public void setOriginalUrl(String originalUrl){
        this.originalUrl = originalUrl;
    }

    public void setClickCount(int clickCount){
        this.clickCount = clickCount;
    }

    public void setCreatedAT(String createdAT){
        this.createdAT = createdAT;
    }

}
