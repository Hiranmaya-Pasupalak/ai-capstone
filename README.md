# BookWorm — E-Bookstore Platform

A production-ready Spring Boot REST API for an e-bookstore, built from wireframe designs using an agentic IDE workflow.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL 16 |
| Migrations | Flyway 9 |
| Auth | JWT (HMAC-SHA256) |
| Build | Maven 3 (wrapper included) |
| Mapping | Lombok + custom EntityMapper |

---

## Project Structure

```
.
├── bookworm-api-spec.yaml          # OpenAPI 3.0.3 specification (33 endpoints)
├── README.md
└── bookworm/                       # Spring Boot application root
    ├── pom.xml
    ├── mvnw / mvnw.cmd             # Maven wrapper
    ├── src/main/
    │   ├── java/com/bookworm/
    │   │   ├── config/             # SecurityConfig (JWT, CORS)
    │   │   ├── controller/         # 10 REST controllers
    │   │   ├── dto/                # 27 request/response DTOs
    │   │   ├── entity/             # 12 JPA entities
    │   │   ├── exception/          # GlobalExceptionHandler
    │   │   ├── mapper/             # EntityMapper
    │   │   ├── repository/         # 11 Spring Data JPA repositories
    │   │   ├── security/           # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
    │   │   └── service/            # Service interfaces + implementations
    │   └── resources/
    │       ├── application.properties   # All secrets via env vars
    │       └── db/migration/
    │           └── V1__initial_schema.sql
    └── src/test/                   # (test scaffolding present)
```

---

## Prerequisites

- Java 17 (e.g. `brew install openjdk@17`)
- PostgreSQL 16 (e.g. `brew install postgresql@16`)
- Maven is **not** required — use the included `./mvnw` wrapper

---

## Database Setup

```sql
-- Run once as a superuser (e.g. postgres)
CREATE USER bookworm_user WITH PASSWORD 'your_password_here';
CREATE DATABASE bookworm OWNER bookworm_user;
GRANT ALL PRIVILEGES ON DATABASE bookworm TO bookworm_user;
```

Flyway will apply `V1__initial_schema.sql` automatically on first startup.

---

## Configuration — Environment Variables

All sensitive values are injected at runtime. **Never hard-code credentials in source files.**

| Variable | Description | Default (dev only) |
|---|---|---|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `bookworm` |
| `DB_USERNAME` | DB user | `bookworm_user` |
| `DB_PASSWORD` | DB password | *(must be set)* |
| `JWT_SECRET` | HMAC-SHA256 signing key (≥32 chars) | *(must be set)* |
| `SERVER_PORT` | Tomcat listen port | `8080` |
| `JWT_EXPIRATION_MS` | Token lifetime in ms | `86400000` (24 h) |

Generate a strong JWT secret:
```bash
openssl rand -base64 48
```

---

## Running the Application

```bash
export DB_USERNAME=bookworm_user
export DB_PASSWORD=your_password_here
export JWT_SECRET=$(openssl rand -base64 48)

cd bookworm
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api/v1`.

### Build a runnable JAR

```bash
cd bookworm
./mvnw clean package -DskipTests

java -jar target/bookworm-1.0.0.jar \
  --DB_PASSWORD=... \
  --JWT_SECRET=...
```

---

## API Overview

The full OpenAPI 3.0.3 specification is in [`bookworm-api-spec.yaml`](./bookworm-api-spec.yaml).

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

All protected endpoints require a Bearer JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

Obtain a token via `POST /auth/login`.

---

### Endpoint Summary

#### Auth
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a new user |
| POST | `/auth/login` | Public | Login and receive JWT |

#### Books & Catalogue
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/books` | Public | Search & filter books (q, category, format, language, minPrice, maxPrice, sort) |
| GET | `/books/{bookId}` | Public | Get book detail |
| GET | `/books/{bookId}/related` | Public | Related books |
| GET | `/books/sections/recommended` | Optional | Personalised recommendations |
| GET | `/books/sections/bestsellers` | Public | Bestsellers this month |
| GET | `/books/sections/new-launches` | Public | New arrivals |
| GET | `/categories` | Public | List all categories |
| GET | `/categories/{categoryId}/books` | Public | Books in a category |

#### Authors
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/authors` | Public | List / search authors |
| GET | `/authors/{authorId}` | Public | Author profile |
| GET | `/authors/{authorId}/books` | Public | Books by this author |

#### Reviews
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/books/{bookId}/reviews` | Public | List reviews (paginated) |
| POST | `/books/{bookId}/reviews` | Required | Submit a review |

#### Shopping Cart
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/cart` | Required | View cart with totals |
| POST | `/cart/items` | Required | Add item (increments if already in cart) |
| PATCH | `/cart/items/{bookId}` | Required | Update item quantity |
| DELETE | `/cart/items/{bookId}` | Required | Remove item |
| POST | `/cart/coupon` | Required | Apply coupon code |
| DELETE | `/cart/coupon` | Required | Remove coupon |

#### Wishlist
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/wishlist` | Required | Get wishlist |
| PUT | `/wishlist/{bookId}` | Required | Add book to wishlist |
| DELETE | `/wishlist/{bookId}` | Required | Remove from wishlist |

#### Orders
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/orders` | Required | Order history (paginated) |
| POST | `/orders` | Required | Place order from cart |
| GET | `/orders/{orderId}` | Required | Get order detail |
| POST | `/orders/{orderId}/buy-again` | Required | Re-add order items to cart |

#### Payments
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/payments/{paymentSessionId}/initiate` | Required | Initiate payment (card/UPI/wallet) |
| POST | `/payments/{paymentSessionId}/confirm` | Required | Confirm and complete purchase |

#### User Profile
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/users/me` | Required | Get profile |
| GET | `/users/me/addresses` | Required | List saved addresses |
| POST | `/users/me/addresses` | Required | Save a new address |
| DELETE | `/users/me/addresses/{addressId}` | Required | Delete an address |
| GET | `/users/me/gift-points` | Required | Gift points balance |

---

### Example: Full Checkout Flow

```bash
BASE=http://localhost:8080/api/v1

# 1. Register / Login
TOKEN=$(curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"secret123"}' \
  | jq -r '.token')

# 2. Browse and add to cart
curl -s -X POST $BASE/cart/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"bookId":"book-01","quantity":2}'

# 3. Apply a coupon (optional)
curl -s -X POST $BASE/cart/coupon \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"couponCode":"SAVE100"}'

# 4. Place the order
ORDER=$(curl -s -X POST $BASE/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryAddress": {
      "firstName":"Alice","lastName":"Test",
      "addressLine1":"123 MG Road","city":"Bengaluru",
      "state":"Karnataka","pin":"560001",
      "country":"India","phoneNumber":"+919876543210",
      "email":"alice@example.com"
    }
  }')

PSID=$(echo $ORDER | jq -r '.paymentSessionId')

# 5. Initiate payment
TXN=$(curl -s -X POST $BASE/payments/$PSID/initiate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"method":"CREDIT_CARD","cardNumber":"4111111111111111","cardHolderName":"Alice Test","cvv":"123","expiryDate":"12/28"}' \
  | jq -r '.transactionId')

# 6. Confirm payment → purchase complete
curl -s -X POST $BASE/payments/$PSID/confirm \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"transactionId\":\"$TXN\"}"
```

---

## Error Response Format

```json
{ "code": "NOT_FOUND",       "message": "Book not found: xyz" }
{ "code": "BAD_REQUEST",     "message": "Invalid or expired coupon: XYZ" }
{ "code": "UNAUTHORIZED",    "message": "Invalid credentials." }
{ "code": "VALIDATION_ERROR","errors": { "email": "must not be blank" } }
{ "code": "INTERNAL_ERROR",  "message": "An unexpected error occurred." }
```

---

## Seed Data (Applied by Flyway)

| Entity | IDs |
|---|---|
| Categories | `cat-01` Self Help, `cat-02` Non-Fiction, `cat-03` Fiction, `cat-04` Children's |
| Authors | `auth-01` Daniel Reed, `auth-02` Arjun Patel |
| Books | `book-01` The Joy of Minimalism (₹149), `book-02` The Art of Focus (₹399) |
| Coupons | `SAVE100` (₹100 off, active), `EXPIRED50` (expired) |

---

## Running Tests

```bash
cd bookworm
./mvnw test
```

> Unit and integration tests are scaffolded under `src/test/java`.

---

## Licence

MIT
