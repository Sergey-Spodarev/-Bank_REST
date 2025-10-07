package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public class SameUsernameException extends RuntimeException {
    private final String username;
    private final String errorCode;

    public SameUsernameException(String username) {
        super(String.format("New username '%s' is the same as current", username));
        this.username = username;
        this.errorCode = "SAME_USERNAME";
    }
}
