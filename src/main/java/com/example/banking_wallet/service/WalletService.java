package com.example.banking_wallet.service;

import com.example.banking_wallet.dto.request.CreateWalletRequest;
import com.example.banking_wallet.dto.request.DepositRequest;
import com.example.banking_wallet.dto.request.TransferRequest;
import com.example.banking_wallet.dto.response.*;
import com.example.banking_wallet.entity.Wallet;
import com.example.banking_wallet.entity.WalletTransaction;
import com.example.banking_wallet.entity.WalletTransactionType;
import com.example.banking_wallet.exception.WalletNotFoundException;
import com.example.banking_wallet.repository.WalletRepository;
import com.example.banking_wallet.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private static final BigDecimal MAX_BALANCE =  new BigDecimal("99999999999999999.99");

    public WalletService(
            WalletRepository walletRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {

        String name = request.name().trim();
        String email = request.email().trim();

        if (walletRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "A wallet with this email already exists"
            );
        }

        Wallet wallet = new Wallet(
                name,
                email,
                request.initialBalance()
        );

        Wallet savedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                savedWallet.getId(),
                savedWallet.getName(),
                savedWallet.getEmail(),
                savedWallet.getBalance(),
                savedWallet.getCreatedAt()
        );
    }


    public WalletDetailsResponse getWalletDetails(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        return new WalletDetailsResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getEmail(),
                wallet.getBalance()
        );
    }


    @Transactional
    public DepositResponse deposit(Long walletId, DepositRequest request) {



        // 1. Find and lock the wallet row.
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        // 2. Capture the balance before changing it.
        BigDecimal previousBalance = wallet.getBalance();

        // 3. Calculate the new balance using BigDecimal.
        BigDecimal amount = request.amount();
        BigDecimal currentBalance = previousBalance.add(amount);

        if (currentBalance.compareTo(MAX_BALANCE) > 0) {
            throw new IllegalArgumentException(
                    "Deposit would exceed the maximum supported wallet balance"
            );
        }

        // 4. Update the wallet.
        wallet.setBalance(currentBalance);
        walletRepository.save(wallet);

        // 5. Record the deposit in transaction history.
        WalletTransaction transaction = new WalletTransaction(
                wallet,
                WalletTransactionType.DEPOSIT,
                amount,
                "SUCCESS",
                null
        );

        walletTransactionRepository.save(transaction);

        // 6. Return the operation result.
        return new DepositResponse(
                wallet.getId(),
                previousBalance,
                amount,
                currentBalance
        );
    }


    @Transactional
    public TransferResponse transfer(TransferRequest request) {

        Long fromWalletId = request.fromWalletId();
        Long toWalletId = request.toWalletId();
        BigDecimal amount = request.amount();

        if (fromWalletId.equals(toWalletId)) {
            throw new IllegalArgumentException(
                    "Sender and receiver wallets must be different"
            );
        }

        Long firstWalletId = Math.min(fromWalletId, toWalletId);
        Long secondWalletId = Math.max(fromWalletId, toWalletId);

        Wallet firstWallet = walletRepository.findByIdForUpdate(firstWalletId)
                .orElseThrow(() -> new WalletNotFoundException(firstWalletId));

        Wallet secondWallet = walletRepository.findByIdForUpdate(secondWalletId)
                .orElseThrow(() -> new WalletNotFoundException(secondWalletId));

        Wallet sender = fromWalletId.equals(firstWalletId)
                ? firstWallet
                : secondWallet;

        Wallet receiver = toWalletId.equals(firstWalletId)
                ? firstWallet
                : secondWallet;

        BigDecimal senderPreviousBalance = sender.getBalance();

        if (senderPreviousBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient wallet balance"
            );
        }

        BigDecimal senderCurrentBalance =
                senderPreviousBalance.subtract(amount);

        BigDecimal receiverCurrentBalance =
                receiver.getBalance().add(amount);

        if (receiverCurrentBalance.compareTo(MAX_BALANCE) > 0) {
            throw new IllegalArgumentException(
                    "Transfer would exceed the maximum supported wallet balance"
            );
        }

        sender.setBalance(senderCurrentBalance);
        receiver.setBalance(receiverCurrentBalance);

        walletRepository.save(sender);
        walletRepository.save(receiver);

        String referenceId = UUID.randomUUID().toString();

        WalletTransaction outgoingTransaction =
                walletTransactionRepository.saveAndFlush(
                        new WalletTransaction(
                                sender,
                                WalletTransactionType.TRANSFER_OUT,
                                amount,
                                "SUCCESS",
                                referenceId
                        )
                );

        WalletTransaction incomingTransaction =
                new WalletTransaction(
                        receiver,
                        WalletTransactionType.TRANSFER_IN,
                        amount,
                        "SUCCESS",
                        referenceId
                );

        walletTransactionRepository.save(incomingTransaction);

        return new TransferResponse(
                outgoingTransaction.getId(),
                sender.getId(),
                receiver.getId(),
                amount,
                "SUCCESS"
        );
    }






    public List<TransactionHistoryResponse> getTransactionHistory(
            Long walletId
    ) {

        // 1. Verify that the wallet exists.
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(walletId);
        }

        // 2. Retrieve transactions, newest first.
        return walletTransactionRepository
                .findByWallet_IdOrderByCreatedAtDescIdDesc(walletId)
                .stream()

                // 3. Convert each entity into the API response DTO.
                .map(transaction -> new TransactionHistoryResponse(
                        transaction.getId(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getStatus(),
                        transaction.getCreatedAt()
                ))

                // 4. Collect the results into a list.
                .toList();
    }
}
