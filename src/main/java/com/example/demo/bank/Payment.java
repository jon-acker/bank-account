package com.example.demo.bank;

public record Payment(Double amount, Currency currency, Counterparty counterparty) {
}
