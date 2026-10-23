package com.example.banking_wallet.repository;

import com.example.banking_wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWallet_IdOrderByCreatedAtDescIdDesc(
            Long walletId
    );
}