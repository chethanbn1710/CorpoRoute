# CorpoRoute

CorpoRoute is a corporate ride-hailing platform designed to streamline employee transportation for organizations. The platform introduces a **Ride Now, Pay Later (RNPL)** model that allows companies to provide transportation benefits to employees while managing payments through centralized corporate billing, credit limits, and financial controls.

---

## Project Status

✅ **Backend Core System Completed (Phases 0-5)**

The backend currently supports:

* Company Management
* User Management
* Authentication & Authorization
* Ride Booking Lifecycle
* Driver Management
* Credit Reservation System (RNPL)

---

## Tech Stack

### Backend

* Java 21+
* Spring Boot 3.5
* Spring Data JPA
* Spring Security
* JWT Authentication
* Hibernate ORM
* Maven

### Database

* PostgreSQL 17

### Development Tools

* VS Code
* Git & GitHub
* Postman

---

## Core Business Model

CorpoRoute operates as a corporate transportation platform where:

* Employees belong to companies.
* Companies receive corporate credit limits.
* Drivers work directly for CorpoRoute.
* Drivers are independent of company ownership.
* Companies receive priority ride servicing.
* Ride costs accumulate against company credit.
* Billing is handled centrally through the RNPL model.

---

## Current Project Structure

```text
src/main/java/com/corporoute

├── config
├── controller
│   ├── AuthController.java
│   ├── CompanyController.java
│   ├── RideController.java
│   └── UserController.java
│
├── dto
│   └── LoginRequest.java
│
├── entity
│   ├── Company.java
│   ├── Ride.java
│   └── User.java
│
├── enums
│   ├── RideStatus.java
│   └── Role.java
│
├── exception
│   ├── CompanyNotFoundException.java
│   ├── CreditLimitExceededException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidRideStateException.java
│   ├── RideNotFoundException.java
│   └── UserNotFoundException.java
│
├── repository
│   ├── CompanyRepository.java
│   ├── RideRepository.java
│   └── UserRepository.java
│
├── security
│   ├── JwtFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
│
├── service
│   ├── CompanyService.java
│   ├── RideService.java
│   └── UserService.java
│
└── CorporouteApplication.java
```

---

# Implemented Features

## Company Management

### APIs

```http
POST   /companies
GET    /companies
GET    /companies/{id}
PUT    /companies/{id}
DELETE /companies/{id}
```

### Company Fields

| Field              | Type       |
| ------------------ | ---------- |
| id                 | Long       |
| name               | String     |
| creditLimit        | BigDecimal |
| outstandingBalance | BigDecimal |
| reservedCredit     | BigDecimal |

---

## User Management

### Roles

```text
ADMIN
EMPLOYEE
DRIVER
```

### APIs

```http
POST   /users
GET    /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}
```

### User Features

* Password encryption using BCrypt
* Company association for employees
* Driver availability tracking
* Driver location tracking

---

## Authentication & Authorization

### Authentication APIs

```http
POST /auth/register
POST /auth/login
```

### Features

* JWT Token Generation
* JWT Validation Filter
* BCrypt Password Hashing
* Spring Security Integration
* Role-Based Access Control

---

## Ride Management

### Ride Statuses

```text
PENDING
ACCEPTED
COMPLETED
CANCELLED
```

### APIs

```http
POST /rides
GET  /rides
GET  /rides/{id}
```

### Employee APIs

```http
GET /rides/my-bookings
PUT /rides/{id}/cancel
```

### Driver APIs

```http
GET /rides/pending
GET /rides/my-assignments

PUT /rides/{id}/accept
PUT /rides/{id}/complete
```

---

## Driver Management

### Driver Availability

Drivers can explicitly go online or offline.

```http
PUT /users/me/online
PUT /users/me/offline
```

### Driver Location

Drivers can update their current location.

```http
PUT /users/me/location
```

Example:

```json
{
  "location": "Electronic City"
}
```

### Driver Workflow

```text
ONLINE
    ↓
VIEW PENDING RIDES
    ↓
ACCEPT RIDE
    ↓
DRIVER OFFLINE (AUTO)
    ↓
COMPLETE RIDE
    ↓
DRIVER ONLINE (AUTO)
```

---

## RNPL Financial Engine

### Credit Reservation Model

At ride creation:

```text
reservedCredit += fare
```

At ride completion:

```text
reservedCredit -= fare
outstandingBalance += fare
```

At ride cancellation:

```text
reservedCredit -= fare
```

### Credit Validation

Before a ride is created:

```text
(outstandingBalance + reservedCredit + fare)
<= creditLimit
```

If exceeded:

```http
400 Bad Request
```

```text
Credit limit exceeded
```

---

## Security Matrix

| Endpoint                  | Access   |
| ------------------------- | -------- |
| /auth/**                  | Public   |
| /companies/**             | ADMIN    |
| /users/**                 | ADMIN    |
| POST /rides               | EMPLOYEE |
| GET /rides/my-bookings    | EMPLOYEE |
| PUT /rides/{id}/cancel    | EMPLOYEE |
| GET /rides/pending        | DRIVER   |
| GET /rides/my-assignments | DRIVER   |
| PUT /rides/{id}/accept    | DRIVER   |
| PUT /rides/{id}/complete  | DRIVER   |

---

## Database Tables

### companies

```text
id
name
credit_limit
outstanding_balance
reserved_credit
```

### users

```text
id
name
email
password
role
available
current_location
company_id
```

### rides

```text
id
pickup_location
drop_location
fare
status
employee_id
driver_id
company_id
```

---

## Running the Application

### Prerequisites

* Java 21+
* PostgreSQL 17+
* Maven

---

### Database

```sql
CREATE DATABASE corporoute;
```

---

### Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/corporoute
spring.datasource.username=postgres
spring.datasource.password=<your_password>
```

---

### Start Application

Linux/Mac:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

---

## Roadmap

### Phase 0: Foundation ✅

* Spring Boot Setup
* PostgreSQL Integration
* Hibernate/JPA Setup

### Phase 1: Company Management ✅

* Company CRUD
* Validation
* Exception Handling

### Phase 2: User Management ✅

* User Entity
* Roles
* Company Relationships

### Phase 3: Authentication & Authorization ✅

* JWT Authentication
* BCrypt Encryption
* Spring Security

### Phase 4: Ride Module ✅

* Ride Creation
* Ride Lifecycle
* Ride Cancellation

### Phase 5: Driver Management ✅

* Driver Availability
* Driver Location
* Ride Acceptance
* Ride Completion
* Driver Workflows

### Phase 6: Smart Dispatch Engine 🚧

* Available Driver Discovery
* Location Coordinates (Lat/Lng)
* Distance Calculation
* Driver Matching
* Auto Assignment Logic

### Phase 7: Dynamic Fare & Incentive Engine 🚧

* Fare Calculation
* Peak Hour Pricing
* Driver Incentives
* Priority Company Benefits

### Phase 8: Administration & Analytics 🚧

* Admin Dashboard APIs
* Ride Analytics
* Credit Monitoring
* Billing Reports

---

## Vision

CorpoRoute aims to become a corporate-first transportation platform by combining enterprise billing, credit-based ride financing, intelligent driver dispatching, and operational visibility into a single scalable ecosystem.
