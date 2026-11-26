Feature: Notification preferences
  As a Walletix user
  I want to choose which alerts I receive
  So that I only receive the information I consider relevant

  Background:
    Given a logged-in user with an expense category and a merchant

  # US25 - "...el sistema debe actualizar mis preferencias y aplicarlas"
  Scenario: The user turns off the budget alerts
    Given the user turns off the budget alerts and keeps achievements and challenges
    And the user has a budget of 50.00 for the expense category this month
    When the user registers an expense of 80.00 described as "Big purchase"
    Then the notification preferences show alerts off, achievements on and challenges on
    And the user has no budget alert in the notifications
