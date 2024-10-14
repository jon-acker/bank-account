package com.example.demo;

import com.example.demo.api.MainController;
import com.example.demo.bank.Counterparty;
import com.example.demo.bank.CounterpartyType;
import com.example.demo.bank.Currency;
import com.example.demo.bank.Payment;
import com.example.demo.bank.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class PaymentSteps {

    @Autowired
    private MockMvc mockMvc;

    private ResultActions response;

    @When("I submit a payment of {double} GBP to account {string} sort code {string}")
    public void i_submit_a_payment_of_gbp_to_account_sort_code(Double amount, String accountNumber, String sortCode) throws Exception {
        var payment = new Payment(amount, Currency.GBP, new Counterparty(CounterpartyType.SORT_CODE_ACCOUNT_NUMBER, accountNumber, sortCode));

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
}
