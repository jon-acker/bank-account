package com.example.demo.bank;

import com.example.demo.api.exception.InvalidAccountNumber;
import org.apache.coyote.BadRequestException;

public record Payment(Double amount, Currency currency, Counterparty counterparty) {
}
