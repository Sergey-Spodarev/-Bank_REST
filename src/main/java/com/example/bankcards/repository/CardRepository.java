package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);
    Optional<Card> findById(Long id);
    Page<Card> findByUser(User user, Pageable pageable);
    @Query("SELECT c FROM Card c WHERE c.user = :user " +
            "AND (:ownerName IS NULL OR LOWER(c.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%'))) " +
            "AND (:status IS NULL OR c.status = :status)")
    Page<Card> findByUserWithFilters(
            @Param("user") User user,
            @Param("ownerName") String ownerName,
            @Param("status") Status status,
            Pageable pageable
    );
    @Query("SELECT c FROM Card c WHERE " +
            "(:ownerName IS NULL OR LOWER(c.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%'))) " +
            "AND (:status IS NULL OR c.status = :status)")
    Page<Card> findAllWithFilters(
            @Param("ownerName") String ownerName,
            @Param("status") Status status,
            Pageable pageable
    );

    @Query("SELECT c FROM Card c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Card> findByIdWithUser(@Param("id") Long id);

    List<Card> findByExpiryDateBeforeAndStatusNot(LocalDate date, Status status);
}
