package com.example.banking_wallet.controller;


import com.example.banking_wallet.dto.request.CreateWalletRequest;
import com.example.banking_wallet.dto.request.DepositRequest;
import com.example.banking_wallet.dto.request.TransferRequest;
import com.example.banking_wallet.dto.response.*;
import com.example.banking_wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(
            @Valid @RequestBody CreateWalletRequest request
    ) {

        WalletResponse response = walletService.createWallet(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDetailsResponse> getWalletDetails(
            @PathVariable Long walletId
    ) {
        WalletDetailsResponse response =
                walletService.getWalletDetails(walletId);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable Long walletId,
            @Valid @RequestBody DepositRequest request
    ) {
        DepositResponse response = walletService.deposit(walletId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        TransferResponse response = walletService.transfer(request);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable Long walletId
    ) {
        List<TransactionHistoryResponse> transactions =
                walletService.getTransactionHistory(walletId);

        return ResponseEntity.ok(transactions);
    }
}