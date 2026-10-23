package com.example.banking_wallet.dto.response;

import java.math.BigDecimal;

public record TransferResponse(
        Long transactionId,
        Long fromWalletId,
        Long toWalletId,
        BigDecimal amount,
        String status
) {
}
