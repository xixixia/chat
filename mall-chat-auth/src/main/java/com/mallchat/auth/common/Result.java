package com.mallchat.auth.common;

/** TODO: update docs. */
public class Result<T> {
    /** TODO: update docs. */
    public static final int SUCCESS = 0;

    /** TODO: update docs. */
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

    /** TODO: update docs. */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMessage("OK");
        result.setData(data);
        return result;
    }

    /** TODO: update docs. */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /** TODO: update docs. */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ERROR);
        result.setMessage(message);
        return result;
    }
}