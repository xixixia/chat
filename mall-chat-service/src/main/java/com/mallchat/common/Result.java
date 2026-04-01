package com.mallchat.common;

/**
  * Standard API response wrapper.
  *
  * @param <T> response data type
  */
public class Result<T> {
    /** Success code. */
    public static final int SUCCESS = 0;

    /** Error code. */
    public static final int ERROR = 1;

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
      * Build success response with data.
      *
      * @param data response data
      * @param <T> data type
      * @return result
      */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMessage("OK");
        result.setData(data);
        return result;
    }

    /**
      * Build success response without data.
      *
      * @param <T> data type
      * @return result
      */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
      * Build error response.
      *
      * @param message error message
      * @param <T> data type
      * @return result
      */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ERROR);
        result.setMessage(message);
        return result;
    }
}
