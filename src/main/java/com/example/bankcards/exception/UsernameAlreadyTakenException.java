package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public class UsernameAlreadyTakenException extends RuntimeException {
    private final String username;
    private final String errorCode;

    public UsernameAlreadyTakenException(String username) {
        super(String.format("Username %s is already taken.", username));
        this.username = username;
        this.errorCode = "USERNAME_TAKEN";
    }
}
