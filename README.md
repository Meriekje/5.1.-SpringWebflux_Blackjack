# Blackjack Reactive Game

A reactive Blackjack game application built with Spring WebFlux, featuring dual database architecture with MongoDB and MySQL.

## Features

- **Reactive Programming**: Built with Spring WebFlux for non-blocking, asynchronous operations
- **Dual Database Architecture**:
    - MongoDB for game data (flexible document storage)
    - MySQL for player statistics (relational data)
- **Global Exception Handling**: Centralized error management with custom exceptions
- **API Documentation**: Auto-generated documentation with Swagger/OpenAPI
- **Unit Testing**: Comprehensive testing with JUnit 5 and Mockito
- **Dockerization**: Full Docker support with multi-stage builds
- **Validation**: Request validation with Bean Validation
- **Reactive Repositories**: Non-blocking database operations



## Quick Start

### Prerequisites

- Java 
- Maven 
- Docker & Docker Compose
- MongoDB
- MySQL

### Running with Docker Compose 

```bash
# Clone the repository
git clone <repository-url>
cd BLACKJACK

# Start all services
docker-compose up -d

# Application will be available at http://localhost:8080