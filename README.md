# CorpoRoute

CorpoRoute is a corporate ride-hailing platform designed to streamline employee transportation for organizations. The platform introduces a **Ride Now, Pay Later (RNPL)** model, enabling companies to manage employee rides through centralized billing, credit limits, and administrative oversight.

## Project Status

🚧 **Backend Foundation Setup Completed**

The project is currently in the initial backend development phase with the core Spring Boot infrastructure successfully configured and integrated with PostgreSQL.

### Completed

- Spring Boot 3.5 setup
- PostgreSQL database integration
- Maven project configuration
- Hibernate/JPA configuration
- Entity mapping with automatic table creation
- Repository layer setup
- REST controller setup
- Initial Git version control setup

## Tech Stack

### Backend
- Java 21+
- Spring Boot 3.5
- Spring Data JPA
- Spring Security
- Hibernate ORM
- Maven

### Database
- PostgreSQL 17

### Development Tools
- VS Code
- Git & GitHub

---

## Current Project Structure

```text
src/main/java/com/corporoute

├── config
├── controller
│   └── TestController.java
├── dto
├── entity
│   └── Company.java
├── repository
│   └── CompanyRepository.java
├── security
├── service
└── CorporouteApplication.java
```

---

## Implemented Components

### Test Controller

A basic REST endpoint has been implemented to verify application startup and request handling.

**Endpoint**

```http
GET /
```

**Response**

```text
CorpoRoute Backend Running!
```

---

### Company Entity

The first domain entity has been created and mapped to PostgreSQL using JPA.

**Fields**

| Field | Type |
|---------|---------|
| id | Long |
| name | String |
| creditLimit | Double |
| outstandingBalance | Double |

Hibernate automatically generates the corresponding database table.

---

### Company Repository

Repository layer implemented using Spring Data JPA.

```java
public interface CompanyRepository
        extends JpaRepository<Company, Long> {
}
```

---

## Database Verification

The application has been successfully verified for:

- PostgreSQL connectivity
- Hibernate entity scanning
- Automatic schema generation
- Table creation through JPA
- Repository initialization

Generated table:

```sql
companies
```

Columns:

```sql
id
name
credit_limit
outstanding_balance
```

---

## Running the Application

### Prerequisites

- Java 21+
- PostgreSQL 17+
- Maven Wrapper (included)

### Database Configuration

Update:

```properties
src/main/resources/application.properties
```

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/corporoute
spring.datasource.username=postgres
spring.datasource.password=<your_password>
```

---

### Start PostgreSQL

Ensure PostgreSQL is running and the database exists:

```sql
CREATE DATABASE corporoute;
```

---

### Run Spring Boot

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

---

## Roadmap

### Phase 1: Foundation ✅

- [x] Spring Boot setup
- [x] PostgreSQL integration
- [x] JPA/Hibernate configuration
- [x] Entity creation
- [x] Repository creation

### Phase 2: Company Management

- [ ] Company CRUD APIs
- [ ] Request validation
- [ ] Exception handling

### Phase 3: Authentication & Authorization

- [ ] User entity
- [ ] Role management
- [ ] JWT authentication
- [ ] Spring Security configuration

### Phase 4: Ride Management

- [ ] Ride booking APIs
- [ ] Driver assignment
- [ ] Ride lifecycle management

### Phase 5: RNPL Financial Engine

- [ ] Corporate credit limits
- [ ] Outstanding balance tracking
- [ ] Credit validation before booking
- [ ] Billing settlement workflows

### Phase 6: Administration

- [ ] Admin dashboard APIs
- [ ] Ride analytics
- [ ] Credit monitoring
- [ ] Financial reporting

---

## Vision

CorpoRoute aims to provide organizations with a scalable and efficient employee transportation management platform by combining ride-hailing capabilities with enterprise-grade billing, financial controls, and operational visibility.