package com.mallchat.exception;

/**
 * Forbidden exception for permission errors.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
