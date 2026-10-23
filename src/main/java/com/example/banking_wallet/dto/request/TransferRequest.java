package com.example.banking_wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "Sender wallet ID is required")
        @Positive(message = "Sender wallet ID must be positive")
        Long fromWalletId,

        @NotNull(message = "Receiver wallet ID is required")
        @Positive(message = "Receiver wallet ID must be positive")
        Long toWalletId,

        @NotNull(message = "Transfer amount is required")
        @DecimalMin(
                value = "0.00",
                inclusive = false,
                message = "Transfer amount must be greater than zero"
        )
        @Digits(
                integer = 17,
                fraction = 2,
                message = "Transfer amount must have at most 17 integer digits and 2 decimal places"
        )
        BigDecimal amount

) {
}