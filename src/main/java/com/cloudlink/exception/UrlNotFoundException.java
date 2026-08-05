package com.cloudlink.exception;

import com.cloudlink.validation.UrlValidation;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message){
        super(message);
    }
}
