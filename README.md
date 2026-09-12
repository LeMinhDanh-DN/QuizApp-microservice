# QuizApp Microservices Backend

Backend Microservices system for an online quiz application (**QuizApp**), built on **Java 17**, **Spring Boot 3**, **Spring Cloud**, **Apache Kafka**, and **PostgreSQL**.

> **Full System Documentation (Frontend + Backend):** [Main README.md](../README.md)

---

## System Architecture

![Backend Microservices Architecture Diagram](https://github.com/user-attachments/assets/77f8aebc-c932-487f-a2b7-94ada0653dae )

Summary of architectural layers:
* **Gateway & Discovery Layer**: `api-gateway` (Port `8765`) with JWT authentication filter and `service-registry` (Eureka, Port `8761`).
* **Business Microservices Layer**: `auth-service` (Port `8085`), `user-service` (Port `8086`), `question-service` (Port `8084`), and `quiz-service` (Port `8083`).
* **Event Streaming Layer**: `Apache Kafka` (Port `19092`) & `Zookeeper` (Port `2181`) f  or asynchronous quiz processing.
* **Database Layer**: Database-per-Service using PostgreSQL 15 (`user_db`, `questiondb`, `quizdb`).

---

## Key Features

### 1. Service Discovery & API Gateway
* **Eureka Service Registry**: Dynamic microservice registration and health monitoring.
* **Spring Cloud Gateway**: Single entry point for routing, rate limiting, and JWT token validation with downstream header injection (`X-User-Id`, `X-User-Roles`).

### 2. Authentication & User Management
* **JWT Security**: Token-based authentication with BCrypt password hashing.
* **Inter-Service Sync**: User profile creation and authentication lookup via OpenFeign between Auth and User services.

### 3. Quiz & Question Bank Engine
* **Dynamic Quiz Generation**: Randomized question selection based on category and question count via OpenFeign.
* **Isolated Question Bank**: Decoupled question management, answer validation, and scoring logic.

### 4. Event-Driven Quiz Processing (Apache Kafka)
* **Asynchronous Submissions**: Non-blocking quiz submission using Kafka topics (`quiz-submissions-topic`, `quiz-result-topic`) for high-throughput performance.

### 5. Database-per-Service Architecture
* **Data Autonomy**: Dedicated PostgreSQL databases (`user_db`, `questiondb`, `quizdb`) ensuring loose coupling and microservice independence.

---

## Project Structure

```
QuizApp-microservice-BE/
├── api-gateway/            # Spring Cloud Gateway & JWT Auth Filter
├── auth-service/           # Authentication & JWT Token Service
├── user-service/           # User Management Service
├── question-service/       # Question Bank & Scoring Engine
├── quiz-service/           # Quiz Management & Submission History Service
├── service-registry/       # Eureka Discovery Server
├── db-schemas/             # Database initialization SQL scripts
├── docker-compose.yml      # Infrastructure Docker Compose (PostgreSQL, Kafka, Zookeeper)
├── image.png               # System architecture diagram image
└── pom.xml                 # Parent Maven POM dependencies
```

---

## Microservices Overview

| Microservice | Port | Primary Tech Stack | Description | Database |
| :--- | :---: | :--- | :--- | :--- |
| **`service-registry`** | `8761` | Spring Cloud Eureka | Service registration and discovery | *N/A* |
| **`api-gateway`** | `8765` | Spring Cloud Gateway | Request routing and JWT authentication filter | *N/A* |
| **`auth-service`** | `8085` | Spring Security, JWT, OpenFeign | User registration, authentication, JWT generation | `user_db` |
| **`user-service`** | `8086` | Spring Data JPA | User profile management | `user_db` |
| **`question-service`** | `8084` | Spring Data JPA, Kafka | Question bank, scoring engine, Kafka processing | `questiondb` |
| **`quiz-service`** | `8083` | Spring Data JPA, OpenFeign, Kafka | Quiz creation, submission handling via Kafka | `quizdb` |

---

## Inter-Service Communication

### 1. Synchronous Communication (OpenFeign)
* **`auth-service` -> `user-service`**: User creation (`POST /users/internal/create`) and username lookup (`GET /users/internal/by-username/{username}`).
* **`quiz-service` -> `question-service`**: Generate question IDs (`GET /question/generate`) and fetch question contents (`POST /question/getQuestions`).

### 2. Asynchronous Event-Driven Flow (Apache Kafka)

```
[Client] --(POST /quiz/submit-async/{quizId})--> [api-gateway] --> [quiz-service]
                                                                        │
                                                  Publish QuizSubmittedEvent
                                                                        ▼
                                                   [Topic: quiz-submissions-topic]
                                                                        │
                                                   Consume QuizSubmittedEvent
                                                                        ▼
                                                              [question-service]
                                                                        │ (Calculate Score)
                                                   Publish QuizResultEvent
                                                                        ▼
                                                    [Topic: quiz-result-topic]
                                                                        │
                                                    Consume QuizResultEvent
                                                                        ▼
                                              [quiz-service] --> Save to [quizdb]
```

* **Topic `quiz-submissions-topic`**: Produced by `quiz-service`, consumed by `question-service` (`question-group`).
* **Topic `quiz-result-topic`**: Produced by `question-service`, consumed by `quiz-service` (`quiz-group`).

---

## API Documentation

### 1. Authentication Service (`/auth`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/auth/register` | Register a new user account | No |
| `POST` | `/auth/login` | Authenticate user and return JWT token | No |
| `POST` | `/auth/refresh` | Refresh an expired JWT token | No |
| `POST` | `/auth/logout` | Invalidate user session / refresh token | No |

### 2. User Service (`/users`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `GET` | `/users/me` | Get currently logged-in user profile | Yes |
| `GET` | `/users/all` | Retrieve list of all users | Yes (Admin) |
| `POST` | `/users/internal/create` | Internal: Create user record | Internal (Feign) |
| `GET` | `/users/internal/by-username/{username}` | Internal: Fetch user details by username | Internal (Feign) |

### 3. Question Service (`/question`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `GET` | `/question/all` | Fetch all questions from the question bank | Yes |
| `GET` | `/question/category/{type}` | Fetch questions filtered by category | Yes |
| `POST` | `/question/add` | Add a new question to the bank | Yes (Admin) |
| `GET` | `/question/generate` | Internal: Generate question IDs for quiz | Internal (Feign) |
| `POST` | `/question/getQuestions` | Internal: Fetch question wrappers by IDs | Internal (Feign) |
| `POST` | `/question/getScore` | Internal: Calculate score for given answers | Internal (Feign) |

### 4. Quiz Service (`/quiz`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/quiz/create` | Create a new quiz with randomized questions | Yes |
| `GET` | `/quiz/get/{id}` | Get question wrappers for taking a quiz | Yes |
| `POST` | `/quiz/submit` | Synchronous quiz submission | Yes |
| `POST` | `/quiz/submit-async/{quizId}` | Asynchronous quiz submission via Kafka | Yes |
| `GET` | `/quiz/result/my-history` | Get quiz attempt history for current user | Yes |
| `GET` | `/quiz/result/user/{userId}` | Get quiz history for a specific user | Yes |
| `GET` | `/quiz/result/quiz/{quizId}` | Get quiz result for a specific quiz & user | Yes |

---

## Security Architecture

1. **Authentication**: `auth-service` verifies user credentials and issues a signed JWT Token.
2. **Gateway Filtering**: `api-gateway` executes `JwtAuthenticationFilter` on incoming requests:
   * Validates JWT signature.
   * Extracts `userId` and `roles`, injecting them into request headers (`X-User-Id`, `X-User-Roles`).
3. **Downstream Execution**: Business microservices consume `X-User-Id` directly from request headers.

---

## Database Architecture (PostgreSQL 15)

Docker container `quizapp-postgres-user` (Port `5432`) initializes 3 isolated databases via `./db-schemas/init-dbs.sql`:

* **`user_db`**: User accounts, BCrypt passwords, and roles (`ROLE_USER`, `ROLE_ADMIN`).
* **`questiondb`**: Questions, options, correct answers, categories, and difficulty levels.
* **`quizdb`**: Quizzes, associated question ID mappings, and submission results.

---

## Getting Started

### Prerequisites
* Docker & Docker Compose
* *(Optional for local dev)* Java 17+ and Maven 3.8+

### Option 1: One-Click Full-Stack Deployment (Recommended)
Run all infrastructure (PostgreSQL, Kafka, Zookeeper) and all 6 microservices together in Docker containers:

```bash
docker-compose up --build -d
```

### Option 2: Hybrid Local Development
Run infrastructure in Docker and run microservices locally via Maven/IDE:

1. **Start Infrastructure Services**:
   ```bash
   docker-compose up -d postgres-user-db kafka zookeeper
   ```
2. **Launch Microservices in Order**:
   1. `service-registry` (Port `8761`) - Dashboard: `http://localhost:8761`
   2. `user-service` (Port `8086`)
   3. `auth-service` (Port `8085`)
   4. `question-service` (Port `8084`)
   5. `quiz-service` (Port `8083`)
   6. `api-gateway` (Port `8765`)

---

## API Gateway Route Mappings

| Service | Route Path | Gateway URL |
| :--- | :--- | :--- |
| `auth-service` | `/auth/**` | `http://localhost:8765/auth/**` |
| `user-service` | `/users/**` | `http://localhost:8765/users/**` |
| `question-service` | `/question/**` | `http://localhost:8765/question/**` |
| `quiz-service` | `/quiz/**` | `http://localhost:8765/quiz/**` |

---
