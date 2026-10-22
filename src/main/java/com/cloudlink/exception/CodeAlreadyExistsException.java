package com.cloudlink.exception;

/** Thrown by a repository when a short code is already taken. */
public class CodeAlreadyExistsException extends RuntimeException {
    public CodeAlreadyExistsException(String code) {
        super("Short code already exists: " + code);
    }
}