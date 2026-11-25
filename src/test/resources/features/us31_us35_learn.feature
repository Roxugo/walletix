Feature: Learn section with tips, micro-lessons and quizzes
  As a Walletix user
  I want to read financial tips and short micro-lessons and take a quiz after each one
  So that I learn to manage my money and check what I learned

  Background:
    Given a logged-in user with an expense category and a merchant

  # US31 - "...el sistema debe mostrar los consejos pertenecientes a esa categoría"
  Scenario: The user sees the tips of a category
    Given a micro-lesson "Saving 101" of the category "Ahorro" with 2 quiz questions
    And a tip "Write down every expense" of the category "Habitos"
    When the user opens the category "ahorro"
    Then only content of the category "Ahorro" is shown, including "Saving 101"

  # US32 - "...el consejo debe añadirse a mi lista de favoritos" / "...debe eliminarse de la lista de favoritos"
  Scenario: The user saves a tip as favorite and then removes it
    Given a tip "Write down every expense" of the category "Habitos"
    When the user marks "Write down every expense" as favorite
    Then "Write down every expense" is in the favorites list
    When the user marks "Write down every expense" as favorite
    Then the favorites list is empty

  # US33 - "...mostrar un mensaje indicando que el contenido está bloqueado y qué condición debo cumplir"
  Scenario: Locked content shows what the user must do to unlock it
    Given a locked micro-lesson "SMART goals" of the category "Ahorro"
    When the user opens the micro-lesson "SMART goals"
    Then the request is rejected with status 403
    And the message "This content is locked. Complete an official challenge in the Challenges Center to unlock it" is shown

  # US34 - "...el sistema debe mostrar el contenido correspondiente"
  Scenario: The user opens a micro-lesson
    Given a micro-lesson "Saving 101" of the category "Ahorro" with 2 quiz questions
    When the user opens the micro-lesson "Saving 101"
    Then the content and 2 quiz questions without their answers are shown

  # US35 - "...el sistema debe mostrar mi puntaje y retroalimentación"
  Scenario: The user completes a quiz
    Given a micro-lesson "Saving 101" of the category "Ahorro" with 2 quiz questions
    When the user answers all the questions of "Saving 101" correctly
    Then the score is 100, the quiz is passed and each answer has its feedback
