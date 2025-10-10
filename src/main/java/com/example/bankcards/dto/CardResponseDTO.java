package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponseDTO {
    private Long id;
    private String maskedCardNumber;
    private String ownerName;
    private LocalDate expiryDate;
    private Status status;
    private BigDecimal balance;
}
