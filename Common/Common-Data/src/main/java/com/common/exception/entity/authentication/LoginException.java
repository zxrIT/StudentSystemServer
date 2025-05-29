package com.common.exception.entity.authentication;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}
