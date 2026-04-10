package com.devfolio.identity.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String errMsg, Throwable err) {
        super(errMsg, err);
    }

}
