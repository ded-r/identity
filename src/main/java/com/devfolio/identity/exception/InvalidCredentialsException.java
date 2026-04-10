package com.devfolio.identity.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String errMsg, Throwable err) {
        super(errMsg, err);
    }
}
