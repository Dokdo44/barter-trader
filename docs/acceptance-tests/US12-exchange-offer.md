# US12: Offer an Exchange Item

## Acceptance Tests

1. Given I am signed in as a receiver and viewing an available product, when I select one or more of the provider's preferred items and send the request, then the application stores a pending exchange proposal with the selected items.
2. Given I am signed in as a receiver and viewing an available product, when I try to send a request without selecting an offered item, then I see an error and no proposal is stored.
3. Given I have already submitted an exchange proposal for a product, when I try to submit another proposal for that product, then I see an error and no duplicate proposal is stored.
4. Given I am signed in as the provider for a product with an exchange proposal, when I open that proposal, then I can see the receiver details, pending status, and selected offered items.
5. Given I am signed in as the provider for a product with a pending exchange proposal, when I accept the proposal, then its stored status becomes `accepted`.
6. Given I am signed in as the provider for a product with a pending exchange proposal, when I decline the proposal, then its stored status becomes `declined`.
