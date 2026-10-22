package com.cloudlink.model;

import java.time.Instant;

/** the model represnts everything that is a real object in the application**/

public class UrlMapping {
    private String shortCode;
    //generates the code that will be used as a shortener
    private String originalUrl;
    private long clickCount;
    // this will set my click count
    private Instant createdAt;
    private Instant expiresAt;

    public UrlMapping(String shortCode, String originalUrl,Instant expiresAt){
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.clickCount = 0;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
    }
    public static UrlMapping createNew(String shortCode, String originalUrl,Instant expiresAt){
        return new UrlMapping(shortCode,originalUrl,expiresAt);
    }

    public String getShortCode(){
        return shortCode;
    }
    public String getOriginalUrl(){
        return originalUrl;
    }
    public long getClickCount() {
        return clickCount;
    }
    public Instant getCreatedAt(){
        return createdAt;
    }
    public Instant getExpiresAt(){
        return expiresAt;
    }

    public void setCode(String shortCode){
        this.shortCode = shortCode;
    }

    public void setOriginalUrl(String originalUrl){
        this.originalUrl = originalUrl;
    }



    public void incrementClickCount(){
        this.clickCount++;
    }

    public boolean isExpired(){
        if(expiresAt == null){
            return false;
        }
        return Instant.now().isAfter(expiresAt);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        UrlMapping other = (UrlMapping) obj;

        return shortCode.equals(other.shortCode);
    }

    @Override
    public int hashCode() {
        return shortCode.hashCode();
    }

    /** Rebuilds a mapping that was previously stored (keeps its real click count and creation time). */
    public static UrlMapping restore(String shortCode, String originalUrl, long clickCount,
                                     Instant createdAt, Instant expiresAt){
        UrlMapping mapping = new UrlMapping(shortCode, originalUrl, expiresAt);
        mapping.clickCount = clickCount;
        mapping.createdAt = createdAt;
        return mapping;
    }

}
