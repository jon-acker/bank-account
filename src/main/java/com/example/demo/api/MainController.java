package com.example.demo.api;

import com.example.demo.api.exception.Error;
import com.example.demo.api.exception.InvalidAccountNumber;
import com.example.demo.bank.Counterparty;
import com.example.demo.bank.Currency;
import com.example.demo.bank.Payment;
import com.example.demo.bank.repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.example.demo.bank.CounterpartyType.SORT_CODE_ACCOUNT_NUMBER;

@RestController
public class MainController {

    @Autowired
    private final PaymentRepository paymentRepository = new PaymentRepository();

    Logger log = Logger.getLogger("com.example.demo.api.MainController");

//    public MainController(PaymentRepository paymentRepository) {
//        this.paymentRepository = paymentRepository;
//    }

    @GetMapping("/payments")
    public List<Payment> getPayments(
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) List<Currency> currencies
    ) {
        log.info(Arrays.toString(currencies.toArray()));
        return paymentRepository;
    }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@RequestBody Payment payment) {
        log.info(payment.toString());

        if (payment.counterparty().accountNumber().length() != 8) {
            throw new InvalidAccountNumber("Invalid account number");
        }

        paymentRepository.add(payment);

        return payment;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Error handleException(RuntimeException exception) {
        return new Error(exception.getMessage());
    }
}
