package com.example.banking_wallet.dto.response;

import com.example.banking_wallet.entity.WalletTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryResponse(
        Long transactionId,
        WalletTransactionType type,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {
}
