# Barclays Take Home Test

This project is a multi-module Spring Boot application consisting of an Authentication Service and an API service for managing user accounts and transactions.

## Project Structure

```
barclays-takehome/
├── pom.xml                    # Parent POM file
├── api/                       # Main API service
│   ├── pom.xml
│   └── src/main/java/com/barclays/api/
└── auth-service/              # JWT Authentication service
    ├── pom.xml
    └── src/main/java/com/barclays/auth/
```

## Modules

### 1. Auth Service (Port 8081)
- **Purpose**: JWT token generation and authentication
- **Port**: 8081
- **Main Features**:
  - JWT token generation
  - User authentication
  - Token validation

### 2. API Service (Port 8080 - default)
- **Purpose**: Main business logic for accounts and transactions
- **Port**: 8080
- **Main Features**:
  - User management
  - Account management
  - Transaction processing
  - H2 in-memory database

## Prerequisites

- Java 11 or higher
- Maven 3.6+

## How to Run

### Option 1: Run Both Modules from Root
From the project root directory:

```bash
# Clean and install all modules
mvn clean install

# Run auth-service in one terminal
cd auth-service
mvn spring-boot:run

# Run api service in another terminal (open new terminal)
cd api
mvn spring-boot:run
```

### Option 2: Run Individual Modules

#### Start Auth Service
```bash
cd auth-service
mvn clean spring-boot:run
```

#### Start API Service
```bash
cd api
mvn clean spring-boot:run
```

### Option 3: Using Maven from Root (Parallel)
```bash
# Install all modules first
mvn clean install

# Run both services (you'll need separate terminals)
# Terminal 1:
mvn spring-boot:run -pl auth-service

# Terminal 2:
mvn spring-boot:run -pl api
```

## Testing the Services

### Authentication Service (Port 8081)

#### Generate JWT Token
```bash
curl --location 'http://localhost:8081/auth/token' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=053ECF89538F1A477DA88BC3F2AFA76E' \
--data-raw '{"email":"test.user2@example.com"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3QudXNlcjJAZXhhbXBsZS5jb20iLCJzdWIiOiJ0ZXN0LnVzZXIyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk5MzYwMDAwLCJleHAiOjE2OTkzOTYwMDAsImlzcyI6ImJhcmNsYXlzLXRha2Vob21lIn0.signature"
}
```

### API Service (Port 8080)

The API service requires JWT authentication. First get a token from the auth service, then use it in subsequent requests.

#### Example API Calls (Replace `<JWT_TOKEN>` with actual token from auth service)

##### Get User Accounts
```bash
curl --location 'http://localhost:8080/v1/accounts' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--header 'Content-Type: application/json'
```

##### Create Account
```bash
curl --location 'http://localhost:8080/v1/accounts' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--header 'Content-Type: application/json' \
--data-raw '{
  "accountName": "Savings Account",
  "accountType": "SAVINGS",
  "balance": 1000.00
}'
```

##### Get Users
```bash
curl --location 'http://localhost:8080/v1/users' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--header 'Content-Type: application/json'
```

## Configuration

### Auth Service Configuration (`auth-service/src/main/resources/application.properties`)
```properties
server.port=8081
auth.jwt.secret=change-me-to-a-long-random-secret-key
auth.jwt.issuer=barclays-takehome
auth.jwt.expiry-seconds=36000
```

### API Service Configuration (`api/src/main/resources/application.properties`)
```properties
spring.application.name=api
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

auth.jwt.secret=change-me-to-a-long-random-secret-key
auth.jwt.issuer=barclays-takehome
```

## Database Access

The API service uses H2 in-memory database. You can access the H2 console at:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Running Tests

### Run all tests for both modules
```bash
mvn clean test
```

### Run tests for specific module
```bash
# Auth service tests
cd auth-service
mvn test

# API service tests
cd api
mvn test
```

## Technologies Used

- **Spring Boot 2.7.18**
- **Java 11**
- **JWT (JSON Web Tokens)** - for authentication
- **H2 Database** - in-memory database
- **Spring Security** - for security configuration
- **Spring Data JPA** - for data persistence
- **Maven** - build tool

## Security Notes

- The JWT secret key should be changed in production environments
- In production, use proper secrets management instead of configuration files
- Consider using HTTPS for all communications
- The H2 console should be disabled in production

## Troubleshooting

1. **Port conflicts**: Make sure ports 8080 and 8081 are not in use by other applications
2. **JWT token expired**: Tokens expire after 10 hours (36000 seconds), generate a new token if needed
3. **Authentication failures**: Ensure you're using a valid JWT token from the auth service
4. **Database issues**: The H2 database is in-memory and resets when the application restarts

## Architecture

```
┌─────────────────┐     ┌─────────────────┐
│   Auth Service  │     │   API Service   │
│   (Port 8081)   │────▶│   (Port 8080)   │
│                 │     │                 │
│ - JWT Generation│     │ - User Mgmt     │
│ - Authentication│     │ - Account Mgmt  │
└─────────────────┘     │ - Transactions  │
                        │ - H2 Database   │
                        └─────────────────┘
```
