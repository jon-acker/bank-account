package com.example.demo;

import com.example.demo.bank.Counterparty;
import com.example.demo.bank.CounterpartyType;
import com.example.demo.bank.Currency;
import com.example.demo.bank.Payment;
import com.example.demo.bank.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class PaymentSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    private ResultActions response;

    @Before
    public void setup() {
        paymentRepository.clear();
    }

    @When("I submit a payment of {double} {string} to account {string} sort code {string}")
    public void i_submit_a_payment_of_gbp_to_account_sort_code(Double amount, String currency, String accountNumber, String sortCode) throws Exception {
        var payment = new Payment(amount, Currency.valueOf(currency), new Counterparty(CounterpartyType.SORT_CODE_ACCOUNT_NUMBER, accountNumber, sortCode));

        response = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(payment)));
    }

    @Then("a HTTP CREATED response should be returned with the following json:")
    public void a_http_created_response_should_be_returned_with_the_following_json(String expectedJson) throws Exception {
        response
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson))
        ;
    }

    @Then("a HTTP Bad Request should be returned with message: {string}")
    public void aHTTPBadRequestShouldBeReturnedWithTheFollowingErrorMessage(String message) throws Exception {
        System.out.println(response.andReturn().getResponse().getContentAsString());
        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));
    }

    @When("I retrieve all payments")
    public void i_retrieve_all_payments() throws Exception {
        response = mockMvc.perform(get("/payments"));
    }

    @When("I retrieve all {string} payments")
    public void i_retrieve_all_currency_payments(String currency) throws Exception {
        response = mockMvc
                .perform(get("/payments")
                        .queryParam("currencies", currency));
    }

    @Then("a HTTP OK status should be returned")
    public void a_http_ok_status_should_be_returned() throws Exception {
        response.andExpect(status().isOk());
    }

    @Then("I should receive the following list:")
    public void i_should_receive_the_following_list(List<Map<String, String>> expectedPayments) throws Exception {
        var stringResponse = response.andReturn().getResponse().getContentAsString();

        Payment[] payments = new ObjectMapper().readValue(stringResponse, Payment[].class);

        assertEquals(expectedPayments.size(), payments.length);

        for (int i = 0; i < payments.length; i++) {
            assertEquals(expectedPayments.get(i).get("amount"), payments[i].amount().toString());
            assertEquals(expectedPayments.get(i).get("account"), payments[i].counterparty().accountNumber());
            assertEquals(expectedPayments.get(i).get("sort code"), payments[i].counterparty().sortCode());
        }
    }

    @When("I retrieve all payments that are at minimum {string} {string}")
    public void iRetrieveAllPaymentsThatAreAtMinimum(String amount, String currency) throws Exception {
        response = mockMvc
                .perform(get("/payments")
                        .queryParam("minAmount", amount)
                        .queryParam("currencies", currency));
    }
}
