package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public class CardNotFoundException extends RuntimeException {
    private final SearchType searchType;
    private final String value;

    public enum SearchType {
        ID
    }

    public CardNotFoundException(SearchType searchType, String value) {
        super(String.format("Card not found for search type: %s", searchType.name().toLowerCase()));
        this.searchType = searchType;
        this.value = value;
    }

    public static CardNotFoundException byId(Long id) {
        return new CardNotFoundException(SearchType.ID, id.toString());
    }
}
