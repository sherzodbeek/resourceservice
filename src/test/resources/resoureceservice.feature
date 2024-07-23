Feature: the file can be uploaded
  Scenario: client makes POST request to /api/resources
    When the client calls /api/resources
    Then the client receives status code of 201
    And the client receives created file id