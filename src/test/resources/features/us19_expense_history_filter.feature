Feature: Expense history filtered by dates
  As a Walletix user
  I want to filter my expenses by a period
  So that I only see the transactions of the period I am interested in

  Background:
    Given a logged-in user with an expense category and a merchant

  # US19 - Filtro aplicado exitosamente
  Scenario: Filter the expenses of the current month
    Given the user has an expense described as "This month" dated today
    And the user has an expense described as "Two months ago" dated 2 months ago
    When the user filters the expenses from the first day of this month until today
    Then only the expense "This month" is shown

  # US19 - Rango inválido
  Scenario: Filter with a start date after the end date
    When the user filters the expenses from "2026-12-31" until "2026-01-01"
    Then the request is rejected with status 400
    And the message "The start date cannot be after the end date" is shown
