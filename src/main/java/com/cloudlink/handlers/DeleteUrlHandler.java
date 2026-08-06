package com.cloudlink.handlers;

import com.cloudlink.service.UrlService;

import java.rmi.server.UnicastRemoteObject;

public class DeleteUrlHandler {
    private final UrlService urlService;
    public DeleteUrlHandler(UrlService urlService){
        this.urlService = urlService;

    }
    public void delete(String code){
        urlService.deleteUrl(code);
    }
}
