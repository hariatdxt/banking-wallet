package com.example.banking_wallet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(
        Long id,
        String name,
        String email,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
