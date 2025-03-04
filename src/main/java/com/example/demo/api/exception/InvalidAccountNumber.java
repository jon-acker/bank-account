package com.example.demo.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidAccountNumber extends IllegalArgumentException {
    public InvalidAccountNumber() {
        super("Invalid account number");
    }
}
