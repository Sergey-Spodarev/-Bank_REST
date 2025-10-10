package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardTransferRequestDTO {
    @NotNull
    private Long fromCardId;

    @NotNull
    private Long toCardId;

    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
}
