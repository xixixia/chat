package com.mallchat.exception;

/**
  * Business exception for domain errors.
  */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
