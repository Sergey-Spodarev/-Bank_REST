package com.example.bankcards.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class InsufficientFundsException extends RuntimeException {

    private final BigDecimal requestedAmount;
    private final BigDecimal currentBalance;

    public InsufficientFundsException(String message) {
        super(message);
        this.requestedAmount = null;
        this.currentBalance = null;
    }

    public InsufficientFundsException(BigDecimal requestedAmount, BigDecimal currentBalance) {
        super(String.format(
                "Insufficient funds: requested %s, available %s",
                requestedAmount, currentBalance
        ));
        this.requestedAmount = requestedAmount;
        this.currentBalance = currentBalance;
    }
}
