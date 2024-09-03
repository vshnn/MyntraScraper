Feature: To Scrape discounted T-shirts on Myntra

  Scenario Outline: Extract and sort discounted T-shirts by highest discount
    Given I navigate to "https://www.myntra.com"
    When I select the "Men" category
    And I filter by type "Tshirts"
    And I filter by brand "<Brand>"
    Then I extract the discounted T-shirts data
    Then I sort the tshirts by highest discount
    Then I print the sorted data to the console
  Examples:
    | Brand         |
    | Van Heusen    |
    | Nike          |
    | Roadster      |
    | Levis         |
