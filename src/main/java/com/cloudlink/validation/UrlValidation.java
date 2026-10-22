package com.cloudlink.validation;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidation {
    static final int MAX_URL_LENGTH = 2048;

    public boolean isValid(String url) {
        if (url == null || url.isBlank() || url.length() > MAX_URL_LENGTH) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            boolean httpOrHttps = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            return httpOrHttps && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}