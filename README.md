# Currency Exchange Service

## Description

Service for managing currencies and retrieving exchange rates using external APIs.

## Settings

1. Set up the `application.yaml` file for your environment. This file includes settings such as the database URL and encryption keys.
2. Make sure the `JASYPT_ENCRYPTOR_PASSWORD` environment variable is set for encryption and decryption purposes.
3. Create the required PostgreSQL database and apply necessary scripts.

## Endpoints

- **`GET /currencies`** - Returns a list of currencies stored in the database.
  - **Response**:
  ```json
  [
      {
          "id": "USD",
          "name": "United States Dollar",
          "symbol": "$"
      },
      {
          "id": "EUR",
          "name": "Euro",
          "symbol": "€"
      }
  ]
  
- **`POST /currencies`** - Adds a new currency to the database for exchange rate retrieval.
  - **Request**:
  ```json
  {
      "id": "GBP",
      "name": "British Pound",
      "symbol": "£"
  }
  ```
   - **Response**:
   ```json
  {
      "id": "GBP",
      "name": "British Pound",
      "symbol": "£"
  }
   ```
 - **`GET /currencies/{currencyCode}`** - Retrieves the exchange rate for a given currency.
   - **Response**:
   ```json
   {
      "exchangeRate": 1.2345
   }
   ```

### Explanation of the Code in the Controller

Your CurrencyController exposes three main endpoints:

1. **GET /currencies**: Retrieves a list of currencies from the database and returns them in JSON format.
2. **POST /currencies**: Allows you to add a new currency to the database.
3. **GET /currencies/{currencyCode}**: Fetches the exchange rate for a specific currency code.

The JASYPT_ENCRYPTOR_PASSWORD is used to decrypt sensitive data, such as database credentials, and should be securely set in the environment.

This should give you a good starting point for setting up your service with the required environment variables and documented endpoints.
