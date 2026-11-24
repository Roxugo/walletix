Feature: Balance and budget alerts
  As a Walletix user
  I want my balance to follow my incomes and expenses and to be warned when I go over a budget
  So that I always know how much money I have and where I am overspending

  Background:
    Given a logged-in user with an expense category and a merchant
    And the balance of the user is 1000.00

  # US11 - "...el sistema debe guardar el gasto correctamente y actualizar mi saldo"
  Scenario: An expense is subtracted from the balance
    When the user registers an expense of 25.50 described as "Lunch"
    Then the expense is saved with status 201
    And the balance of the user is 974.50

  # US11 - Saldo insuficiente
  Scenario: An expense greater than the balance is rejected
    When the user registers an expense of 1500 described as "New phone"
    Then the request is rejected with status 400
    And the message "The expense exceeds your available balance (S/ 1000.00)" is shown
    And the balance of the user is 1000.00

  # US12 - "...el sistema debe guardar el ingreso y actualizar mi saldo disponible"
  Scenario: An income is added to the balance
    When the user registers an income of 1500.10
    Then the balance of the user is 2500.10

  # US14 - "...el sistema debe eliminar el registro y actualizar mi saldo"
  Scenario: Deleting an expense gives the money back to the balance
    Given the user has an expense described as "Cinema" dated today
    When the user confirms the deletion of the expense
    Then the balance of the user is 1000.00

  # US15 - Alertas generadas por sobre presupuesto
  Scenario: An expense that goes over the monthly budget shows an alert
    Given the user has a budget of 100 for the expense category this month
    And the user has registered an expense of 80
    When the user registers an expense of 30 described as "Dinner"
    Then the expense is saved with status 201
    And the response includes the budget alert "S/ 110.00 of S/ 100.00"

  # US15 - Un solo presupuesto por categoría y mes (si no, la alerta no sabría con cuál comparar)
  Scenario: A second budget for the same category and month is rejected
    Given the user has a budget of 400 for the expense category this month
    When the user creates another budget of 800 for the same category this month
    Then the request is rejected with status 409
    And the message "You already have a budget for that category in that month" is shown

  # US15 - Sin alerta mientras no se supere el presupuesto
  Scenario: An expense within the monthly budget shows no alert
    Given the user has a budget of 100 for the expense category this month
    When the user registers an expense of 80 described as "Groceries"
    Then the expense is saved with status 201
    And the response does not include a budget alert
