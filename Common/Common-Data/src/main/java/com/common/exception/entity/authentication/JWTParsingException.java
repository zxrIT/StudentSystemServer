package com.common.exception.entity.authentication;

public class JWTParsingException extends RuntimeException {
  public JWTParsingException(String message) {
    super(message);
  }
}
