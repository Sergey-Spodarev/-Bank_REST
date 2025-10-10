package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.dto.CardTransferRequestDTO;
import com.example.bankcards.entity.Status;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/cards") // ✅ Единообразный путь
@Tag(name = "Card Management", description = "APIs for managing bank cards")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new card", description = "ADMIN only - Create a new bank card for user")
    @ApiResponse(responseCode = "201", description = "Card created successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<CardResponseDTO> createCard(
            @PathVariable Long userId,
            @Valid @RequestBody CreateCardDTO createDTO) {
        CardResponseDTO response = cardService.createCard(userId, createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{cardId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block card", description = "ADMIN only - Block a card")
    public ResponseEntity<CardResponseDTO> blockCard(@PathVariable Long cardId) {
        CardResponseDTO response = cardService.blockCard(cardId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate card", description = "ADMIN only - Activate a blocked card")
    public ResponseEntity<CardResponseDTO> activateCard(@PathVariable Long cardId) {
        CardResponseDTO response = cardService.activateCard(cardId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/my/{cardId}/block")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Block own card", description = "USER - Block their own card")
    public ResponseEntity<CardResponseDTO> blockOwnCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.blockOwnCard(cardId));
    }

    @PostMapping("/transfer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Transfer between cards", description = "Transfer money between user's own cards")
    public ResponseEntity<Void> transferMoney(
            @Valid @RequestBody CardTransferRequestDTO transferDTO) {
        cardService.transferMoney(transferDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all cards", description = "ADMIN only - Get all cards with pagination and filters")
    public ResponseEntity<Page<CardResponseDTO>> getAllCards(
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDTO> result = cardService.getAllCardsWithFilters(ownerName, status, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{cardId}/balance")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get card balance", description = "Get balance of user's own card")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long cardId) {
        BigDecimal balance = cardService.getBalance(cardId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my cards", description = "Get user's own cards with pagination and filters")
    public ResponseEntity<Page<CardResponseDTO>> getMyCards(
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDTO> result = cardService.getMyCardsWithFilter(ownerName, status, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete card", description = "ADMIN only - Delete a card")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}