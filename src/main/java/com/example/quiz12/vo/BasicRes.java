package com.example.quiz12.vo;

// 放HTTO
public class BasicRes {
    private int code;

    private String message;

    public BasicRes() {
    }

    public BasicRes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
