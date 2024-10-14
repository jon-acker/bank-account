Feature: payments

  Scenario: Submitting a valid payment
    When I submit a payment of 100.00 GBP to account "12345678" sort code "123456"
    Then a HTTP CREATED response should be returned with the following json:
    """
    {
      "currency": "GBP",
      "amount": 100.0,
      "counterparty": {
        "type": "SORT_CODE_ACCOUNT_NUMBER",
        "accountNumber": "12345678",
        "sortCode": "123456"
      }
    }
    """

  Scenario: Submitting an payment with invalid account number
    When I submit a payment of 100.00 GBP to account "111" sort code "123456"
    Then a HTTP Bad Request should be returned with message: "Invalid account number"

