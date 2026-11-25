Feature: Challenges and achievements center
  As a Walletix user
  I want to join savings challenges, register my daily progress and create my own challenges
  So that I build better financial habits and earn points and medals

  Background:
    Given a logged-in user with an expense category and a merchant

  # US27 + US26 - "...el sistema debe agregar el reto a mi lista de retos activos"
  Scenario: The user joins an available challenge and sees it in the active challenges
    Given an official challenge "Week without delivery" of 7 days that started today
    When the user joins the challenge "Week without delivery"
    Then the user is added to the challenge with status 201
    And the challenge "Week without delivery" appears in the active challenges on day 0 of 7

  # US27 + US28 - "...el sistema debe impedir la acción y mostrar un mensaje indicando el requisito no cumplido"
  Scenario: The user cannot join a challenge without meeting its requirements
    Given an official challenge "Only for 30 or older" only for users of at least 30 years
    When the user joins the challenge "Only for 30 or older"
    Then the request is rejected with status 400
    And the message "You must be at least 30 years old (you are 26)" is shown

  # User flow of the report - "Registrar progreso diario": +20 XP, Día X de N
  Scenario: The user registers the daily progress of a challenge
    Given an official challenge "Five days saving" of 5 days that started today
    And the user has joined the challenge "Five days saving"
    When the user registers today's progress in the challenge
    Then the user earns 20 XP and is on day 1 of 5

  # User flow of the report (unhappy path) - "Ya registraste tu progreso de hoy"
  Scenario: The progress can only be registered once per day
    Given an official challenge "Once a day" of 5 days that started today
    And the user has joined the challenge "Once a day"
    And the user registers today's progress in the challenge
    When the user registers today's progress in the challenge
    Then the request is rejected with status 409
    And the message "You already registered your progress for today" is shown

  # US29 - "...el sistema debe guardar el reto y mostrarlo en la lista de retos activos"
  Scenario: The user creates a personal challenge
    When the user creates a personal challenge of type "sin_delivery" for 7 days to save 70.00
    Then the personal challenge "No delivery days" is created, is not official and gives 0 reward points

  # US29 - "...el sistema debe mostrar un mensaje pidiendo completar la información"
  Scenario: A personal challenge without a valid duration is rejected
    When the user creates a personal challenge of type "ahorro_libre" for 0 days to save 70.00
    Then the request is rejected with status 400
    And the message "The duration must be between 1 and 90 days" is shown
