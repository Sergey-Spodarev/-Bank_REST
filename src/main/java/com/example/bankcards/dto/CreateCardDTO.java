package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCardDTO {
    @NotBlank(message = "Card number is required")
    private String cardNumberPlain;

    @NotBlank
    private String ownerName;

    @NotNull
    private LocalDate expiryDate;
}
