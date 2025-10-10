package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.CardTransferRequestDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.util.CardNumberEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardNumberEncryptor encryptor;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_ShouldCreateCardSuccessfully() {
        // Given
        Long userId = 1L;
        CreateCardDTO createDTO = new CreateCardDTO();
        createDTO.setCardNumberPlain("4111111111111234");
        createDTO.setOwnerName("John Doe");
        createDTO.setExpiryDate(LocalDate.now().plusYears(2));

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Card card = new Card();
        card.setId(1L);
        card.setOwnerName("John Doe");
        card.setStatus(Status.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(encryptor.encrypt("4111111111111234")).thenReturn("encrypted_number");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(encryptor.decrypt("encrypted_number")).thenReturn("4111111111111234");

        // When
        CardResponseDTO result = cardService.createCard(userId, createDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getOwnerName());
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(userRepository).findById(userId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_WithExpiredDate_ShouldThrowException() {
        // Given
        Long userId = 1L;
        CreateCardDTO createDTO = new CreateCardDTO();
        createDTO.setExpiryDate(LocalDate.now().minusDays(1));

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(userId, createDTO));
    }

    @Test
    void createCard_WithNonExistentUser_ShouldThrowException() {
        // Given
        Long userId = 999L;
        CreateCardDTO createDTO = new CreateCardDTO();
        createDTO.setExpiryDate(LocalDate.now().plusYears(1));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> cardService.createCard(userId, createDTO));
    }

    @Test
    void blockCard_ShouldBlockCardSuccessfully() {
        // Given
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Status.ACTIVE);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(encryptor.decrypt(anyString())).thenReturn("4111111111111234");

        // When
        CardResponseDTO result = cardService.blockCard(cardId);

        // Then
        assertNotNull(result);
        assertEquals(Status.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_ShouldActivateCardSuccessfully() {
        // Given
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Status.BLOCKED);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(encryptor.decrypt(anyString())).thenReturn("4111111111111234");

        // When
        CardResponseDTO result = cardService.activateCard(cardId);

        // Then
        assertNotNull(result);
        assertEquals(Status.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_WithExpiredCard_ShouldThrowException() {
        // Given
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Status.BLOCKED);
        card.setExpiryDate(LocalDate.now().minusDays(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // When & Then
        assertThrows(IllegalStateException.class, () -> cardService.activateCard(cardId));
    }

    @Test
    void transferMoney_ShouldTransferSuccessfully() {
        // Given
        setupSecurityContext();

        CardTransferRequestDTO transferDTO = new CardTransferRequestDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        User currentUser = new User();
        currentUser.setId(1L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setStatus(Status.ACTIVE);
        fromCard.setBalance(new BigDecimal("500.00"));
        fromCard.setUser(currentUser);
        fromCard.setExpiryDate(LocalDate.now().plusYears(1));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(new BigDecimal("200.00"));
        toCard.setUser(currentUser);
        toCard.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findByIdWithUser(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdWithUser(2L)).thenReturn(Optional.of(toCard));
        when(userDetails.getUser()).thenReturn(currentUser);

        // When
        cardService.transferMoney(transferDTO);

        // Then
        assertEquals(new BigDecimal("400.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("300.00"), toCard.getBalance());
        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void transferMoney_WithSameCard_ShouldThrowException() {
        // Given
        CardTransferRequestDTO transferDTO = new CardTransferRequestDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(1L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cardService.transferMoney(transferDTO));
    }

    @Test
    void transferMoney_WithInsufficientFunds_ShouldThrowException() {
        // Given
        setupSecurityContext();

        CardTransferRequestDTO transferDTO = new CardTransferRequestDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("600.00"));

        User currentUser = new User();
        currentUser.setId(1L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setStatus(Status.ACTIVE);
        fromCard.setBalance(new BigDecimal("500.00"));
        fromCard.setUser(currentUser);
        fromCard.setExpiryDate(LocalDate.now().plusYears(1));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(new BigDecimal("200.00"));
        toCard.setUser(currentUser);
        toCard.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findByIdWithUser(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdWithUser(2L)).thenReturn(Optional.of(toCard));
        when(userDetails.getUser()).thenReturn(currentUser);

        // When & Then
        assertThrows(InsufficientFundsException.class, () -> cardService.transferMoney(transferDTO));
    }

    @Test
    void transferMoney_WithDifferentUsers_ShouldThrowException() {
        // Given
        setupSecurityContext();

        CardTransferRequestDTO transferDTO = new CardTransferRequestDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        User currentUser = new User();
        currentUser.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setStatus(Status.ACTIVE);
        fromCard.setBalance(new BigDecimal("500.00"));
        fromCard.setUser(currentUser);
        fromCard.setExpiryDate(LocalDate.now().plusYears(1));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(new BigDecimal("200.00"));
        toCard.setUser(otherUser);
        toCard.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findByIdWithUser(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdWithUser(2L)).thenReturn(Optional.of(toCard));
        when(userDetails.getUser()).thenReturn(currentUser);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> cardService.transferMoney(transferDTO));
    }

    @Test
    void getBalance_ShouldReturnBalance() {
        // Given
        setupSecurityContext();

        Long cardId = 1L;
        User currentUser = new User();
        currentUser.setId(1L);

        Card card = new Card();
        card.setId(cardId);
        card.setBalance(new BigDecimal("1500.75"));
        card.setUser(currentUser);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userDetails.getUser()).thenReturn(currentUser);

        // When
        BigDecimal balance = cardService.getBalance(cardId);

        // Then
        assertEquals(new BigDecimal("1500.75"), balance);
    }

    @Test
    void getBalance_WithDifferentUser_ShouldThrowException() {
        // Given
        setupSecurityContext();

        Long cardId = 1L;
        User currentUser = new User();
        currentUser.setId(1L);

        User cardOwner = new User();
        cardOwner.setId(2L);

        Card card = new Card();
        card.setId(cardId);
        card.setBalance(new BigDecimal("1500.75"));
        card.setUser(cardOwner);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userDetails.getUser()).thenReturn(currentUser);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> cardService.getBalance(cardId));
    }

    @Test
    void blockOwnCard_ShouldBlockOwnCardSuccessfully() {
        // Given
        setupSecurityContext();

        Long cardId = 1L;
        User currentUser = new User();
        currentUser.setId(1L);

        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Status.ACTIVE);
        card.setUser(currentUser);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(userDetails.getUser()).thenReturn(currentUser);
        when(encryptor.decrypt(anyString())).thenReturn("4111111111111234");

        // When
        CardResponseDTO result = cardService.blockOwnCard(cardId);

        // Then
        assertNotNull(result);
        assertEquals(Status.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void blockOwnCard_WithDifferentUser_ShouldThrowException() {
        // Given
        setupSecurityContext();

        Long cardId = 1L;
        User currentUser = new User();
        currentUser.setId(1L);

        User cardOwner = new User();
        cardOwner.setId(2L);

        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Status.ACTIVE);
        card.setUser(cardOwner);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userDetails.getUser()).thenReturn(currentUser);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> cardService.blockOwnCard(cardId));
    }

    @Test
    void getMyCardsWithFilter_ShouldReturnFilteredCards() {
        // Given
        setupSecurityContext();

        User currentUser = new User();
        currentUser.setId(1L);

        Card card = new Card();
        card.setId(1L);
        card.setUser(currentUser);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        Page<Card> cardPage = new PageImpl<>(List.of(card));
        Pageable pageable = Pageable.unpaged();

        when(userDetails.getUser()).thenReturn(currentUser);
        when(cardRepository.findByUserWithFilters(currentUser, "John", Status.ACTIVE, pageable))
                .thenReturn(cardPage);
        when(encryptor.decrypt(anyString())).thenReturn("4111111111111234");

        // When
        Page<CardResponseDTO> result = cardService.getMyCardsWithFilter("John", Status.ACTIVE, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByUserWithFilters(currentUser, "John", Status.ACTIVE, pageable);
    }

    @Test
    void deleteCard_ShouldDeleteCardSuccessfully() {
        // Given
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // When
        cardService.deleteCard(cardId);

        // Then
        verify(cardRepository).delete(card);
    }

    @Test
    void getCardById_WithNonExistentCard_ShouldThrowException() {
        // Given
        Long cardId = 999L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardService.getBalance(cardId));
    }

    private void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }
}