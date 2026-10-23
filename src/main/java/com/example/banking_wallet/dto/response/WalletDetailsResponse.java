package com.example.banking_wallet.dto.response;

import java.math.BigDecimal;

public record WalletDetailsResponse(
        Long walletId,
        String name,
        String email,
        BigDecimal balance
) {


}
