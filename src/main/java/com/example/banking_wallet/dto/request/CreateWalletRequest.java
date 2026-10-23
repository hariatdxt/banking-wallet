package com.example.banking_wallet.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateWalletRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(
                value = "0.00",
                message = "Initial balance cannot be negative"
        )
        @Digits(
                integer = 17,
                fraction = 2,
                message = "Initial balance must have at most 17 integer digits and 2 decimal places"
        )
        BigDecimal initialBalance

) {
}
