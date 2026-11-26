Feature: Export my financial data in PDF
  As a Walletix user
  I want to export my financial data in PDF format
  So that I can save it, print it or use it outside the platform

  Background:
    Given a logged-in user with an expense category and a merchant

  # US39 escenario 1 - "...el sistema debe generar un archivo PDF con mis datos y permitir su descarga"
  Scenario: The user exports the report of a month with movements
    Given the user has an expense described as "Weekly market" dated today
    When the user exports the financial report of this month
    Then a PDF file is downloaded that contains "Walletix financial report" and "Weekly market"

  # US39 escenario 2 - "...el sistema debe mostrar un mensaje indicando que no hay información disponible"
  Scenario: The user tries to export a month without movements
    When the user exports the financial report of this month
    Then the request is rejected with status 404
    And the message "There are no movements in the chosen period: there is no information to export" is shown
