package com.example.banking_wallet.dto.response;

import java.time.Instant;

public record ApiErrorResponse(
        boolean success,
        String message,
        Instant timestamp) {
}
