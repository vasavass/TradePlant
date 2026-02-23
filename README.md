# TradePlant - Crypto Exchange API

TradePlant is a backend RESTful API that simulates a cryptocurrency trading platform. It allows users to manage their portfolios, check balances, and execute buy/sell operations. The core feature of the application is its integration with the live Binance API to fetch real-time market prices for accurate transaction calculations.

## Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot (Spring Web, Spring Boot Starter)
* **Database:** PostgreSQL & Spring Data JPA (Hibernate)
* **External Integration:** Binance REST API (via `RestTemplate`)
* **Documentation:** Swagger / OpenAPI (`springdoc-openapi`)
* **Architecture:** MVC, Repository Pattern, DTOs
* **Build Tool:** Maven

## Core Features

* **Real-Time Market Data:** Fetches live cryptocurrency prices (e.g., BTC, DOGE) directly from the Binance API before executing any trade.
* **Trade Operations:** Secure endpoints for buying and selling assets with strict validation (e.g., checking insufficient funds or negative quantities).
* **Portfolio Management:** Tracks user's cash balance and owned assets dynamically.
* **Graceful Error Handling:** Utilizes `Try-Catch` blocks and custom exceptions to prevent server crashes if the external Binance API goes down or returns invalid data.
* **Interactive Documentation:** Auto-generated Swagger UI interface to easily test all endpoints directly from the browser.

## How to Run
1. Clone the repository.
2. Ensure you have a running PostgreSQL instance and update the `application.properties` file with your database credentials.
3. Run the Spring Boot application.
4. Open your browser and navigate to: `http://localhost:8080/swagger-ui/index.html` to test the API!

http://localhost:8080/swagger-ui/index.html#/
   <img width="1905" height="911" alt="image" src="https://github.com/user-attachments/assets/1d67c2ae-6325-4156-9d80-a2de6b47be4b" />
