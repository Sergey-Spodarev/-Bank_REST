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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberEncryptor encryptor;

    public CardService(CardRepository cardRepository,
                       UserRepository userRepository,
                       CardNumberEncryptor encryptor) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.encryptor = encryptor;
    }

    public CardResponseDTO createCard(Long userId, CreateCardDTO createDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        if (createDTO.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be in the past");
        }

        Card card = new Card();
        card.setOwnerName(createDTO.getOwnerName().trim());
        card.setExpiryDate(createDTO.getExpiryDate());
        card.setStatus(Status.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        String encrypted = encryptor.encrypt(createDTO.getCardNumberPlain());
        card.setCardNumberEncrypted(encrypted);

        Card saved = cardRepository.save(card);
        log.info("Card created successfully for user: {}", userId);
        return convertToResponse(saved);
    }

    public CardResponseDTO blockCard(Long cardId) {
        Card card = getCardById(cardId);
        checkCardExpiry(card);

        card.setStatus(Status.BLOCKED);
        Card saved = cardRepository.save(card);
        log.info("Card {} blocked by ADMIN", cardId);
        return convertToResponse(saved);
    }

    public CardResponseDTO activateCard(Long cardId) {
        Card card = getCardById(cardId);
        checkCardExpiry(card);

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot activate expired card");
        }

        card.setStatus(Status.ACTIVE);
        Card saved = cardRepository.save(card);
        log.info("Card {} activated by ADMIN", cardId);
        return convertToResponse(saved);
    }

    public CardResponseDTO unblockCard(Long cardId) {
        return activateCard(cardId);
    }

    public Page<CardResponseDTO> getAllCardsWithFilters(String ownerName, Status status, Pageable pageable) {
        Page<Card> cards = cardRepository.findAllWithFilters(ownerName, status, pageable);
        return cards.map(this::convertToResponse);
    }

    @Transactional
    public void transferMoney(CardTransferRequestDTO transferDTO) {
        if (transferDTO.getFromCardId().equals(transferDTO.getToCardId())) {
            throw new IllegalArgumentException("Source and target cards must be different");
        }

        User user = getCurrentUser();

        Card fromCard = cardRepository.findByIdWithUser(transferDTO.getFromCardId())
                .orElseThrow(() -> CardNotFoundException.byId(transferDTO.getFromCardId()));
        Card toCard = cardRepository.findByIdWithUser(transferDTO.getToCardId())
                .orElseThrow(() -> CardNotFoundException.byId(transferDTO.getToCardId()));

        if (!fromCard.getUser().getId().equals(user.getId()) ||
                !toCard.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Transfer allowed only between your own cards");
        }

        checkCardExpiry(fromCard);
        checkCardExpiry(toCard);

        if (fromCard.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException("Source card is not active");
        }
        if (toCard.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException("Target card is not active");
        }

        if (fromCard.getBalance().compareTo(transferDTO.getAmount()) < 0) {
            throw new InsufficientFundsException("Not enough balance");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(transferDTO.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferDTO.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        log.info("Transfer completed: {} from card {} to card {}",
                transferDTO.getAmount(), transferDTO.getFromCardId(), transferDTO.getToCardId());
    }

    public BigDecimal getBalance(Long cardId) {
        User user = getCurrentUser();
        Card card = getCardById(cardId);
        checkCardExpiry(card);

        if (!card.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return card.getBalance();
    }

    public void deleteCard(Long cardId) {
        Card card = getCardById(cardId);
        cardRepository.delete(card);
        log.info("Card {} deleted", cardId);
    }

    public Page<CardResponseDTO> getMyCardsWithFilter(String ownerName, Status status, Pageable pageable) {
        User user = getCurrentUser();
        Page<Card> cards = cardRepository.findByUserWithFilters(user, ownerName, status, pageable);

        cards.forEach(this::checkCardExpiry);

        return cards.map(this::convertToResponse);
    }

    public CardResponseDTO blockOwnCard(Long cardId) {
        User user = getCurrentUser();
        Card card = getCardById(cardId);
        checkCardExpiry(card);

        if (!card.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only block your own cards");
        }

        card.setStatus(Status.BLOCKED);
        Card saved = cardRepository.save(card);
        log.info("Card {} blocked by owner", cardId);
        return convertToResponse(saved);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateExpiredCards() {
        LocalDate today = LocalDate.now();
        List<Card> expiredCards = cardRepository.findByExpiryDateBeforeAndStatusNot(today, Status.EXPIRED);

        for (Card card : expiredCards) {
            card.setStatus(Status.EXPIRED);
            log.info("Card {} marked as EXPIRED", card.getId());
        }

        cardRepository.saveAll(expiredCards);
        log.info("Updated {} expired cards", expiredCards.size());
    }

    private Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> CardNotFoundException.byId(cardId));
    }

    private void checkCardExpiry(Card card) {
        if (card.getExpiryDate().isBefore(LocalDate.now()) && card.getStatus() != Status.EXPIRED) {
            card.setStatus(Status.EXPIRED);
            cardRepository.save(card);
            log.debug("Card {} automatically marked as EXPIRED", card.getId());
        }
    }

    private CardResponseDTO convertToResponse(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setOwnerName(card.getOwnerName());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());

        String decrypted = encryptor.decrypt(card.getCardNumberEncrypted());
        dto.setMaskedCardNumber(CardNumberEncryptor.maskCardNumber(decrypted));

        return dto;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}