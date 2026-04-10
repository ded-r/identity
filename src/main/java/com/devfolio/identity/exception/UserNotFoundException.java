package com.devfolio.identity.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String errMsg, Throwable err) {super(errMsg, err);}

}
