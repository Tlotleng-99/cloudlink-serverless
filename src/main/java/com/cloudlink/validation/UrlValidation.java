package com.cloudlink.validation;


import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidation {
    public boolean isValid(String url){
        if(url ==null || url.isBlank()){
            return false;
        }
        try{
            URI uri = new URI(url);
            return uri.getScheme() !=null && uri.getHost() !=null;

        }catch (URISyntaxException e){
            return  false;
        }
    }


}
