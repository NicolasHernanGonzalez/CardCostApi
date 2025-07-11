# 💳 Card Cost API

## 🧾 Description

**Card Cost API** Its goal is to expose a REST API that allows querying the clearing cost of a credit/debit card using its number (PAN), as well as performing CRUD operations on the table of costs associated with each issuing country.

---

## ⚙️ Technologies and Tools Used

- **Java**: 17
- **Spring Boot**: 3.x
- **Maven**: For dependency management and build
- **Spring Web**: For creating REST endpoints
- **Spring Validation**: For automatic input validations
- **Spring Cache + Caffeine**: Caching with maximum size and expiration for BIN lookup
- **Resilience4j**: Circuit breaker for external service calls
- **JUnit & Mockito**: Unit tests
- **Testcontainers & SpringBootTest**: Integration tests
- **RestTemplate**: HTTP client to call the public API at [https://binlist.net](https://binlist.net)
- **H2 Database**: In-memory database for testing and development
- **Docker**: Containerization for easier deployment

---

## 🏗️ Project Structure

```plaintext
com.cardcostapi
├── controller       # REST endpoints
├── service          # Business logic
├── repository       # Database access
├── domain/model     # Entities and DTOs
├── external         # binlist.net client
├── config           # Configuration (cache, restTemplate, etc.)
├── exception        # Centralized error handling
└── CardCostApiApplication.java
```

---

## 🔧 Exposed Endpoints

### Get cost by card number
- **POST** `/api/payment-cards-cost`
```json
{
  "card_number": "4571736009872913"
}
```
**Response Body:**
```json
{
  "country": "AR",
  "cost": 77
}
```
- **Response (200 OK)**:

---

### CRUD operations for country cost

#### 📥 Create new cost
- **POST** `/api/cost`
- **Request Body**:
```json
{
  "country": "UY",
  "cost": 90.1
}
```
- **Response (201 Created)**:
```json
{
  "country": "UY",
  "cost": 90.1
}
```

#### 📖 Get cost by country
- **GET** `/api/cost/{country}`
- **Path Param**: country code (e.g. `US`, `GR`)
- **Response (200 OK)**:
```json
{
  "country": "US",
  "cost": 5.0
}
```
- **Response (404 Not Found)** if the country is not found

#### 📝 Update cost by ID
- **PUT** `/api/cost/{id}`
- **Path Param**: internal ID of the entity (numeric)
- **Request Body**:
```json
{
  "country": "US",
  "cost": 6.5
}
```
- **Response (200 OK)**:
```json
{
  "country": "US",
  "cost": 6.5
}
```
- **Response (404 Not Found)** if not found

#### ❌ Delete cost by country
- **DELETE** `/api/cost/{country}`
- **Path Param**: country code
- **Response (204 No Content)**

---

## 🚨 Error Handling

The API handles errors in a centralized way with consistent responses. Some covered scenarios:

- **400 Bad Request**: Invalid data or validation errors.
- **404 Not Found**: Country or resource not found.
- **409 Conflict**: Integrity violation or duplicates.
- **424 Failed Dependency**: Error in external dependencies (e.g. binlist.net).

---

## 🚀 How to Run

### Prerequisites
- Java 17
- Maven 3.x

### Local execution
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Testing

To run all tests:
```bash
mvn test
```

Includes unit tests for business logic and integration tests for endpoints and mocked calls to binlist.net.

---

## ⚠️ Notes
- The BIN is assumed to be 8 digits long (although the exercise states 6). Validation is enforced accordingly.

---

## 📈 Scalability and Future Improvements

- Ready for horizontal scaling (stateless, no session or in-memory state)
- **Spring Cache with Caffeine** used to reduce load on external service with configurable expiration and max size
- Persistence layer decoupled using interface (`ClearingCostRepository`), allowing easy replacement of JPA with Redis, DynamoDB, or other services without changing business logic
- Follows SOLID principles, especially **Open/Closed** and **Dependency Inversion**, to facilitate extensibility and testing
- **Clear separation of responsibilities** (Controller, Service, Repository, External Client)

---

## 🔒 Concurrency, Caching, Resilience Strategy

To prevent multiple concurrent requests from hitting the external provider (`https://lookup.binlist.net`), a combination of in-memory caching, per-BIN locking, rate limiting, and circuit breaking has been implemented:

- **In-memory cache:** A `ConcurrentHashMap` is used as a simple cache. It stores both valid responses (country codes) and a `"NOT_FOUND"` marker for invalid BINs, to avoid redundant external calls.

- **Per-BIN locks:** A secondary `ConcurrentHashMap<String, Object>` is used to synchronize concurrent requests for the same BIN. This ensures that only one thread makes the external API call, while others wait for the cached response.

- **Custom Rate Limiter:** A sliding window rate limiter prevents exceeding a configurable number of requests per minute to the external API.

- **Circuit Breaker** The external call is protected with a `@CircuitBreaker` (Resilience4j). If the provider starts failing (e.g., too many `5xx/4xx` errors), the breaker opens and temporarily blocks access to prevent further overload.
This strategy reduces external calls, protects the provider, and ensures a reliable and consistent experience under high concurrency.
Although the Rate Limiter handles 429 errors, the Circuit Breaker protects the API from other external failures like timeouts, connection issues, or unexpected responses. It opens the circuit after detecting repeated failures to avoid system overload.

 <img width="200" height="500" alt="image" src="https://github.com/user-attachments/assets/278e3776-6c23-4954-a424-2cc1cf2196c4" />

   #### Key Configuration:

   - `minimumNumberOfCalls=5`: Requires at least 5 calls to evaluate failures.
   - `failureRateThreshold=50`: If 50% of calls fail, the circuit opens.
   - `slidingWindowSize=10`: Sliding window of 10 calls for failure evaluation.
   - `waitDurationInOpenState=30s`: Stays open for 30 seconds once triggered.
   - `permittedNumberOfCallsInHalfOpenState=2`: Allows 2 trial calls in half-open state.
   - `automaticTransitionFromOpenToHalfOpenEnabled=true`: Automatically switches to half-open mode.
   - `ignore-exceptions=com.ng.exceptions.TooManyRequestsException`: Ignores 429 errors managed by the rate limiter.
---



## 🐳 Running with Docker Compose

If you have Docker and Docker Compose installed, you can easily run the app with:

```bash
docker-compose up --build
```

This builds the image from the `Dockerfile` and exposes the API at:

> http://localhost:8080

No external DB is required since the app uses an **embedded H2 database** for temporary persistence.

## 👨‍💻 Author

- **Name**: Nicolas Gonzalez

---
