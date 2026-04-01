package com.mallchat.exception;

/**
 * Unauthorized exception for authentication errors.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
