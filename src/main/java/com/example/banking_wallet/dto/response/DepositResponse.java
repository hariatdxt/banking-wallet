package com.example.banking_wallet.dto.response;

import java.math.BigDecimal;

public record DepositResponse(
        Long walletId,
        BigDecimal previousBalance,
        BigDecimal amount,
        BigDecimal currentBalance
) {
}
