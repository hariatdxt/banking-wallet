package com.example.banking_wallet.repository;

import com.example.banking_wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByEmail(String email);

    Optional<Wallet> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
    Optional<Wallet> findByIdForUpdate(
            @Param("walletId") Long walletId
    );
}
//What does this query do?
//The query fetches the wallet and requests a database-level write lock on its row.
//
//When the service calls findByIdForUpdate(), the database protects that row until the surrounding transaction completes.
//
//This lock is useful only when called within an appropriate transaction, so we'll invoke it from a service method annotated with Spring's @Transactional.