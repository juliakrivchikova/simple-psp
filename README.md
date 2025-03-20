# Simple PSP System

## Overview

This project implements a simple **Payment Service Provider (PSP)** system that handles payment requests, routes them to
different acquirers based on BIN card rules, and processes transactions accordingly. It is built using **Kotlin**, *
*Spring Boot**, and **Spring WebFlux** for non-blocking processing with **Kotlin Coroutines**.

## Features

- **API for Payment Processing**: Accepts and validates payment details.
- **BIN-Based Routing**: Determines the correct acquirer based on the sum of BIN digits.
- **Mock Acquirers**: Simulates transaction approval or denial.
- **Transaction Persistence**: Stores transactions in an in-memory repository.
- **Exception Handling**: Includes basic error handling.
- **Extensible Architecture**: Easily supports additional acquirers in the future.
- **Non-Blocking Processing**: Uses **Spring WebFlux** with **Kotlin Coroutines** for efficient transaction handling.

## API Endpoints

### Process Payment

- **Endpoint:** `POST /api/v1/payments`
- **Request Body:**
  ```json
  {
    "cardNumber": "4242424242424242",
    "expiryDate": {
      "month": 12,
      "year": 2029
  },
    "cvv": "123",
    "amount": {
      "value": 100.50,
      "currency": "USD"
    },
    "merchantId": "merchant123"
  }
  ```
- **Response:**
  ```json
  {
  "error": null,
  "result": {
    "id": "4854c47b-7cb3-41ae-aa31-86a2fca25fa3",
    "status": "APPROVED"
  }

}
  ```

## Setup and Running the Service

### Prerequisites

- Java 17+
- Kotlin 1.9.25+
- Maven 3.8.1+

### Running Locally

1. **Clone the Repository**
   ```sh
   git clone https://github.com/juliakrivchikova/simple-psp.git
   cd simple-psp
   ```
2. **Build the Project**
   ```sh
   mvn clean install
   ```
3. **Run the Service**
   ```sh
   java -jar target/simple-psp-0.0.1-SNAPSHOT.jar
   ```
4. **Test the API**

   Request for Approved Transaction:
   ```sh
   curl -X POST http://localhost:8080/api/v1/payments -H "Content-Type: application/json" -d '{ "cardNumber": "4242424242424242", "expiryDate": { "year": 2029, "month": "12" }, "cvv": "123", "amount": { "value": 100.50, "currency": "USD" }, "merchantId": "merchant123" }'
   ```

   Request for Denied Transaction:
   ```sh
   curl -X POST http://localhost:8080/api/v1/payments -H "Content-Type: application/json" -d '{ "cardNumber": "4111111111111111", "expiryDate": { "year": 2029, "month": "12" }, "cvv": "123", "amount": { "value": 100.50, "currency": "USD" }, "merchantId": "merchant123" }'
   ```

## Design Choices

- **Spring WebFlux with Kotlin Coroutines:**
    - Provides non-blocking processing to handle high throughput efficiently.
    - Spring Boot acts as an **IoC (Inversion of Control) container**, managing dependencies and configurations.
    - Spring WebFlux enables **asynchronous, event-driven HTTP handling**, improving scalability.
- **Kotlin for Coroutines:**
    - Allows writing **non-blocking code in an imperative way**, making it more readable and maintainable compared to
      callback-based approaches.
    - Reduces boilerplate and simplifies concurrency handling.
- **Layered Architecture:**
    - **`PaymentController` (API Layer):** Handles HTTP requests and responses, exposing the API endpoints.
    - **`TransactionProcessor` (Business Layer):** Decouples business logic from lower-level details, allowing support
      for different transaction request origins.
    - **`TransactionService` (Persistence Layer):** Ensures business logic is independent of entity persistence,
      improving maintainability and testability.
    - **`AcquirerClient` (Integration Layer):** Abstracts communication with different acquirers, encapsulating all
      acquirer-specific logic and supporting multiple acquirer implementations.

## Implementation Assumptions

- Authentication is assumed to be out of scope
- Concurrent data access (transaction modification) is assumed to be out of scope
- Situations when acquirer system is failed to be resolved or interaction with an acquirer is failed are assumed to be
  out of scope

## Error Handling

- **400 Bad Request:** Invalid payment details.
- **500 Internal Server Error:** Unexpected processing failures.
- **Transaction Failures:** The transaction ID and failure reason are returned to the client.