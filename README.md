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

- Java 21
- Maven 
- Docker & Docker Compose


### Running with Docker Compose 

```bash
# Clone the repository
git clone <repository-url>
cd BLACKJACK

# Start all services
docker-compose up -d

# Application will be available at http://localhost:8080

## 📚 API Endpoints

### Game Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/game/new` | Create new game | `{"playerName": "string"}` |
| GET | `/game/{id}` | Get game details | - |
| POST | `/game/{id}/play` | Make a move | `{"action": "HIT\|STAND"}` |
| DELETE | `/game/{id}/delete` | Delete game | - |

### Player Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/ranking` | Get player rankings | - |
| PUT | `/player/{playerId}` | Update player name | `{"name": "string"}` |

## 🎮 Game Flow Example

### 1. Create a New Game

```bash
curl -X POST http://localhost:8080/game/new \\
  -H "Content-Type: application/json" \\
  -d '{"playerName": "TestPlayer"}'
```

**Response:**
```json
{
  "id": "uuid-game-id",
  "playerId": "1",
  "playerName": "TestPlayer",
  "playerCards": [...],
  "dealerCards": [...],
  "playerScore": 15,
  "dealerScore": 10,
  "status": "IN_PROGRESS",
  "bet": 10.0,
  "winnings": 0.0
}
```

### 2. Make a Move

```bash
curl -X POST http://localhost:8080/game/{game-id}/play \\
  -H "Content-Type: application/json" \\
  -d '{"action": "HIT"}'
```

### 3. Check Game Status

```bash
curl http://localhost:8080/game/{game-id}
```

### 4. View Rankings

```bash
curl http://localhost:8080/ranking
```

## Game Rules

- **Objective**: Get as close to 21 as possible without going over
- **Card Values**:
  - Number cards: Face value
  - Face cards (J, Q, K): 10 points
  - Ace: 11 points (or 1 if it would cause a bust)
- **Actions**:
  - **HIT**: Request another card
  - **STAND**: Keep current hand
- **Winning Conditions**:
  - Player gets 21 (Blackjack)
  - Player closer to 21 than dealer
  - Dealer busts (goes over 21)

## Technology Stack

- **Backend**: Spring Boot 3.x, Spring WebFlux
- **Databases**: MongoDB, MySQL
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Documentation**: Swagger/OpenAPI 3

## Database Schema

### MongoDB (Games Collection)
```json
{
  "id": "string",
  "playerId": "string",
  "playerName": "string",
  "playerCards": [{"suit": "string", "rank": "string", "value": number}],
  "dealerCards": [{"suit": "string", "rank": "string", "value": number}],
  "playerScore": number,
  "dealerScore": number,
  "status": "IN_PROGRESS|PLAYER_WIN|DEALER_WIN|PLAYER_BUST|DEALER_BUST|PUSH",
  "bet": number,
  "winnings": number,
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### MySQL (Players Table)
```sql
CREATE TABLE players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    total_winnings DECIMAL(10,2) DEFAULT 0.00,
    win_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Testing

```bash
# Run all tests
mvn test


```

## API Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Docker Configuration

The application uses multi-stage Docker builds for optimization:

- **Development**: Hot reload with volume mounting
- **Production**: Optimized JAR execution

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `docker` |
| `MONGODB_URI` | MongoDB connection string | `mongodb://mongodb:27017/blackjack` |
| `MYSQL_URL` | MySQL JDBC URL | `jdbc:mysql://mysql:3306/blackjack` |
| `MYSQL_USERNAME` | MySQL username | `blackjack_user` |
| `MYSQL_PASSWORD` | MySQL password | `blackjack_password` |

## 🔧 Development

### Local Development Setup

```bash
# Start only databases
docker-compose up -d mysql mongodb

# Run application in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Building the Application

```bash
# Build JAR
mvn clean package

# Build Docker image
docker build -t blackjack-api .

# Build with Docker Compose
docker-compose build
```

## Deployment

### Production Deployment

```bash
# Production build
docker-compose -f docker-compose.prod.yml up -d
```


## Acknowledgments

- Spring Boot and Spring WebFlux for reactive programming
- MongoDB and MySQL communities
- Docker for containerization support

---

## ** Happy Gaming!**
```
