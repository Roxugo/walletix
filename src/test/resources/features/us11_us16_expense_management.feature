Feature: Expense management
  As a Walletix user
  I want to register, edit and delete my expenses
  So that I can keep control of my spending

  Background:
    Given a logged-in user with an expense category and a merchant

  # US11 - Registro de gasto exitoso
  Scenario: Register a valid expense
    When the user registers an expense of 25.50 described as "Lunch"
    Then the expense is saved with status 201
    And the expense list of the user contains "Lunch"

  # US13 - Edición exitosa
  Scenario: Edit an expense
    Given the user has an expense described as "Taxi" dated today
    When the user changes the description to "Taxi to the airport" and the amount to 45.90
    Then the expense list shows "Taxi to the airport" with an amount of 45.90

  # US14 - Eliminación confirmada
  Scenario: Delete an expense
    Given the user has an expense described as "Cinema" dated today
    When the user confirms the deletion of the expense
    Then the expense no longer appears in the list of the user

  # US16 - Campos vacíos detectados
  Scenario Outline: Required fields are validated
    When the user registers an expense of <amount> described as "<description>"
    Then the request is rejected with status 400
    And the message "<message>" is shown

    Examples:
      | amount | description | message                                 |
      | 20     |             | The description is required             |
      | 0      | No amount   | The amount must be greater than zero    |
