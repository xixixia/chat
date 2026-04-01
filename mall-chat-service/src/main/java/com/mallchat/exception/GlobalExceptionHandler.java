package com.mallchat.exception;

import com.mallchat.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
  * Global exception handler.
  */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
      * Handle business exception.
      *
      * @param ex business exception
      * @return result
      */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException ex) {
        return Result.error(ex.getMessage());
    }

    /**
     * Handle unauthorized exception.
     *
     * @param ex unauthorized exception
     * @return result with 401 status
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result<Void>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(ex.getMessage()));
    }

    /**
     * Handle forbidden exception.
     *
     * @param ex forbidden exception
     * @return result with 403 status
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Result<Void>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.error(ex.getMessage()));
    }

    /**
      * Handle validation exception.
      *
      * @param ex validation exception
      * @return result
      */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Validation error";
        return Result.error(message);
    }

    /**
      * Handle unexpected exception.
      *
      * @param ex exception
      * @return result
      */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        return Result.error("Internal server error");
    }
}
