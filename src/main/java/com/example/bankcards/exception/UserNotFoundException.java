package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final SearchType searchType;
    private final String value;

    public enum SearchType {
        ID, USERNAME
    }

    public UserNotFoundException(SearchType searchType, String value) {
        super(String.format("User not found by %s: %s", searchType.name().toLowerCase(), value));
        this.searchType = searchType;
        this.value = value;
    }

    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException(SearchType.ID, id.toString());
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException(SearchType.USERNAME, username);
    }
}
