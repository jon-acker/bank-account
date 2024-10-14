package com.example.demo.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidSortCode extends IllegalArgumentException {
    public InvalidSortCode() {
        super("Invalid sort code");
    }
}
