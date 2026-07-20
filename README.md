# QuizApp Microservices System

An online quiz application built on a Microservices architecture using the Spring Cloud ecosystem and Java Spring Boot. This project utilizes a centralized source code management model (Monorepo).

---

## Architecture

The system consists of the following core components:

*   **service-registry**: Eureka Server, serving as the central hub for service registration and discovery.
*   **api-gateway**: The single entry point integrating Spring Cloud Gateway (WebMVC/WebFlux) to handle routing and load balancing.
*   **quiz-service**: Service handling the management of quizzes and coordinating the test-taking logic.
*   **question-service**: Service managing the question bank, categorizing, and providing question data to quiz-service.

---

## Tech Stack

*   **Backend:** Java Core, Spring Boot, Hibernate / Spring Data JPA.
*   **Microservices:** Spring Cloud Gateway, Spring Cloud Netflix Eureka, Spring Cloud LoadBalancer.
*   **Build Tool:** Maven.

---

## Setup & Installation

### 1. Prerequisites
*   Java Development Kit (JDK) 17 or higher.
*   Apache Maven 3.x.
*   Corresponding databases must be running in the background.

### 2. Startup Order (Required Sequence)

To ensure system stability, services must be started in the exact order listed below:

1.  **service-registry**: Start this service first on the default port 8761. Access the URL http://localhost:8761 to verify the Eureka Dashboard interface.
2.  **question-service & quiz-service**: Start these business services so they can automatically register their instances with the Eureka Server.
3.  **api-gateway**: Start this service last to serve as the gateway routing all incoming requests from Clients.

---

## API Routing

All requests from the Client (Postman, Frontend) must pass through the API Gateway at the configured port 8765. The Gateway automatically routes traffic based on URL prefixes:

| Endpoint (Via Gateway) | Target Service | Description |
| :--- | :--- | :--- |
| http://localhost:8765/quiz/** | quiz-service | APIs related to quiz management and execution |
| http://localhost:8765/question/** | question-service | APIs related to question configuration and management |

---
