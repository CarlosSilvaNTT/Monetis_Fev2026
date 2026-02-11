@registo
  Feature:  Multiple Payments
Scenario Outline: Make multiple payments
Given login and access payments page
When I make a payment from "<ACCOUNT>" with reference "<REFERENCE>", entity "<ENTITY>", amount <AMOUNT> and category "<CATEGORY>"
Then Verify confirmation window appears with payment details
When I click to proceed with payment
Then Verify success payment page appears
When I access transactions page
Then Verify new transaction appears with "<CATEGORY>" category and <AMOUNT> amount

Examples:
  | ACCOUNT  | REFERENCE  | ENTITY | AMOUNT | CATEGORY |
  | checking | INV-902438 | 12345  | 100    | house    |
#  | car      | INV-944438 | 12333  | 500    | car      |
  | bills    | INV-909923 | 22123  | 200    | bills    |
  | checking | INV-990022 | 33221  | 150    | mobile   |