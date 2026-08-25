package com.cloudlink.handlers;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.service.UrlService;

public class RedirectHandler {
    private final UrlService urlService;

    public RedirectHandler(UrlService urlService){
        this.urlService = urlService;
    }

    public String redirect(String code) {
        try{
            return urlService.redirect(code);
        }catch (UrlNotFoundException e) {
            return null;
        }
    }
}
