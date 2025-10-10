package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.dto.CardTransferRequestDTO;
import com.example.bankcards.entity.Status;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @Test
    void createCard_ShouldReturnCreated() {
        // Given
        Long userId = 1L;
        CreateCardDTO createDTO = new CreateCardDTO();
        createDTO.setCardNumberPlain("4111111111111234");
        createDTO.setOwnerName("John Doe");
        createDTO.setExpiryDate(LocalDate.now().plusYears(2));

        CardResponseDTO responseDTO = new CardResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setMaskedCardNumber("**** **** **** 1234");
        responseDTO.setOwnerName("John Doe");
        responseDTO.setStatus(Status.ACTIVE);
        responseDTO.setBalance(BigDecimal.ZERO);

        when(cardService.createCard(eq(userId), any(CreateCardDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<CardResponseDTO> response = cardController.createCard(userId, createDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("**** **** **** 1234", response.getBody().getMaskedCardNumber());
        verify(cardService).createCard(userId, createDTO);
    }

    @Test
    void blockCard_ShouldReturnOk() {
        // Given
        Long cardId = 1L;
        CardResponseDTO responseDTO = new CardResponseDTO();
        responseDTO.setId(cardId);
        responseDTO.setStatus(Status.BLOCKED);

        when(cardService.blockCard(cardId)).thenReturn(responseDTO);

        // When
        ResponseEntity<CardResponseDTO> response = cardController.blockCard(cardId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Status.BLOCKED, response.getBody().getStatus());
        verify(cardService).blockCard(cardId);
    }

    @Test
    void activateCard_ShouldReturnOk() {
        // Given
        Long cardId = 1L;
        CardResponseDTO responseDTO = new CardResponseDTO();
        responseDTO.setId(cardId);
        responseDTO.setStatus(Status.ACTIVE);

        when(cardService.activateCard(cardId)).thenReturn(responseDTO);

        // When
        ResponseEntity<CardResponseDTO> response = cardController.activateCard(cardId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Status.ACTIVE, response.getBody().getStatus());
        verify(cardService).activateCard(cardId);
    }

    @Test
    void blockOwnCard_ShouldReturnOk() {
        // Given
        Long cardId = 1L;
        CardResponseDTO responseDTO = new CardResponseDTO();
        responseDTO.setId(cardId);
        responseDTO.setStatus(Status.BLOCKED);

        when(cardService.blockOwnCard(cardId)).thenReturn(responseDTO);

        // When
        ResponseEntity<CardResponseDTO> response = cardController.blockOwnCard(cardId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Status.BLOCKED, response.getBody().getStatus());
        verify(cardService).blockOwnCard(cardId);
    }

    @Test
    void transferMoney_ShouldReturnOk() {
        // Given
        CardTransferRequestDTO transferDTO = new CardTransferRequestDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.50"));

        doNothing().when(cardService).transferMoney(transferDTO);

        // When
        ResponseEntity<Void> response = cardController.transferMoney(transferDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(cardService).transferMoney(transferDTO);
    }

    @Test
    void getAllCards_ShouldReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        CardResponseDTO card1 = new CardResponseDTO();
        card1.setId(1L);
        CardResponseDTO card2 = new CardResponseDTO();
        card2.setId(2L);

        Page<CardResponseDTO> page = new PageImpl<>(List.of(card1, card2), pageable, 2);

        when(cardService.getAllCardsWithFilters(anyString(), any(), any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<CardResponseDTO>> response = cardController.getAllCards(null, null, 0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
        verify(cardService).getAllCardsWithFilters(null, null, pageable);
    }

    @Test
    void getAllCards_WithFilters_ShouldReturnFilteredPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardResponseDTO> page = new PageImpl<>(List.of(new CardResponseDTO()), pageable, 1);

        when(cardService.getAllCardsWithFilters(eq("John"), eq(Status.ACTIVE), any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<CardResponseDTO>> response = cardController.getAllCards("John", Status.ACTIVE, 0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(cardService).getAllCardsWithFilters("John", Status.ACTIVE, pageable);
    }

    @Test
    void getBalance_ShouldReturnBalance() {
        // Given
        Long cardId = 1L;
        BigDecimal balance = new BigDecimal("1500.75");

        when(cardService.getBalance(cardId)).thenReturn(balance);

        // When
        ResponseEntity<BigDecimal> response = cardController.getBalance(cardId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(balance, response.getBody());
        verify(cardService).getBalance(cardId);
    }

    @Test
    void getMyCards_ShouldReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        CardResponseDTO card = new CardResponseDTO();
        card.setId(1L);

        Page<CardResponseDTO> page = new PageImpl<>(List.of(card), pageable, 1);

        when(cardService.getMyCardsWithFilter(anyString(), any(), any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<CardResponseDTO>> response = cardController.getMyCards(null, null, 0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(cardService).getMyCardsWithFilter(null, null, pageable);
    }

    @Test
    void deleteCard_ShouldReturnNoContent() {
        // Given
        Long cardId = 1L;
        doNothing().when(cardService).deleteCard(cardId);

        // When
        ResponseEntity<Void> response = cardController.deleteCard(cardId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(cardService).deleteCard(cardId);
    }
}