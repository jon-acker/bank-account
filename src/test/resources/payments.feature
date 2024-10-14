Feature: payments

  Scenario: Submitting a valid payment
    When I submit a payment of 100.00 "GBP" to account "12345678" sort code "123456"
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

  Scenario Outline: Submitting an payment with invalid account number or sort code
    When I submit a payment of 100.00 "GBP" to account "<account>" sort code "<sort code>"
    Then a HTTP Bad Request should be returned with message: "<expected message>"
    Examples:
      | account  | sort code | expected message       |
      | 111      | 123456    | Invalid account number |
      | abcdefgh | 123456    | Invalid account number |
      | 12345678 | 333       | Invalid sort code      |


  Scenario: Listing payments
    Given I submit a payment of 100.00 "GBP" to account "12345678" sort code "123456"
    And I submit a payment of 50.00 "GBP" to account "99999999" sort code "123456"
    When I retrieve all payments
    Then a HTTP OK status should be returned
    And I should receive the following list:
      | amount | account  | sort code |
      | 100.0  | 12345678 | 123456    |
      | 50.0   | 99999999 | 123456    |

  Scenario: Listing payments with filtering by currency
    Given I submit a payment of 100.00 "GBP" to account "12345678" sort code "123456"
    And I submit a payment of 50.00 "USD" to account "99999999" sort code "123456"
    When I retrieve all "GBP" payments
    Then I should receive the following list:
      | amount | account  | sort code |
      | 100.0  | 12345678 | 123456    |

  Scenario: Listing payments with filtering by minimum amount
    Given I submit a payment of 100.00 "GBP" to account "12345678" sort code "123456"
    And I submit a payment of 50.00 "GBP" to account "99999999" sort code "123456"
    And I submit a payment of 30.00 "GBP" to account "99999999" sort code "123456"
    When I retrieve all payments that are at minimum "50.00" "GBP"
    Then I should receive the following list:
      | amount | account  | sort code |
      | 100.0  | 12345678 | 123456    |
      | 50.0   | 99999999 | 123456    |
