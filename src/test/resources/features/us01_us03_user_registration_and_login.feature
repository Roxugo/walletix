Feature: User registration and login
  As a Walletix user
  I want to register and log in with my email and password
  So that I can access my personal financial information

  # US01 - Registro exitoso
  Scenario: Successful registration
    Given a visitor with a valid email, password and required data
    When the visitor submits the registration form
    Then the account is created with status 201
    And the response does not include the password

  # US03 - Correo duplicado
  Scenario: Registration with an email that is already registered
    Given a user that is already registered
    When another visitor tries to register with the same email
    Then the request is rejected with status 409
    And the message "The email is already registered" is shown

  # US02 - Inicio de sesión exitoso
  Scenario: Successful login
    Given a user that is already registered
    When the user logs in with the correct password
    Then the system returns a JWT token
    And the token gives access to the user's own profile

  # US02 - Credenciales incorrectas
  Scenario: Login with a wrong password
    Given a user that is already registered
    When the user logs in with the password "wrongPass1"
    Then the request is rejected with status 401
    And the message "Incorrect email or password" is shown
