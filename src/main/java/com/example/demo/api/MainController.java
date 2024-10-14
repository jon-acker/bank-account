package com.example.demo.api;

import com.example.demo.api.exception.Error;
import com.example.demo.api.exception.InvalidAccountNumber;
import com.example.demo.api.exception.InvalidSortCode;
import com.example.demo.bank.Currency;
import com.example.demo.bank.Payment;
import com.example.demo.bank.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

@RestController
public class MainController {

    @Autowired
    private final PaymentRepository paymentRepository;

    Logger log = Logger.getLogger("com.example.demo.api.MainController");

    public MainController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/payments")
    public Stream<Payment> getPayments(
            @RequestParam(required = false, defaultValue = "0") Double minAmount,
            @RequestParam(required = false, defaultValue = "") List<Currency> currencies
    ) {
        Stream<Payment> payments = paymentRepository.stream();

        if (!currencies.isEmpty()) {
            payments = payments.filter(payment -> currencies.contains(payment.currency()));
        }

        return payments.filter(payment -> payment.amount() >= minAmount);
    }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@RequestBody Payment payment) {
        log.info(payment.toString());

        guardAgainstInvalidAccountNumber(payment);
        guardAgainstInvalidSortCore(payment);

        paymentRepository.add(payment);

        return payment;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleException(RuntimeException exception) {
        return new Error(exception.getMessage());
    }

    private static void guardAgainstInvalidSortCore(Payment payment) {
        if (!payment.counterparty().sortCode().matches("\\d{6}")) {
            throw new InvalidSortCode();
        }
    }

    private static void guardAgainstInvalidAccountNumber(Payment payment) {
        if (!payment.counterparty().accountNumber().matches("\\d{8}")) {
            throw new InvalidAccountNumber();
        }
    }
}
