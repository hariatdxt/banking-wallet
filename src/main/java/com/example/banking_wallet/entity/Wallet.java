package com.example.banking_wallet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wallet_email",
                        columnNames = "email"
                )
        }
)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal balance=BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Wallet() {
    }

    public Wallet(String name, String email ,BigDecimal balance) {
        if (balance == null || balance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be null or negative"
            );
        }
        this.name = name;
        this.email = email;
        this.balance = balance;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}