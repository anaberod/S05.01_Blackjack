# Blackjack Reactive API (Spring Boot + WebFlux)

**Repository:** [S05.01_Blackjack](https://github.com/anaberod/S05.01_Blackjack.git)

---

## 📄 Description – Exercise Statement

This project is a **reactive Java API** for a simplified Blackjack game.  
The API is designed to connect and manage data across **two different databases**: **MongoDB** and **MySQL**.

The Blackjack game is implemented with the required functionalities to play a match (player vs dealer), including player management, card hands, and game rules.

The application is fully documented (Swagger/OpenAPI) and properly tested with JUnit/Mockito.

---
## 💻 Technologies Used

-   **Java 21**
-   **Spring Boot 3.3**
-   **Spring WebFlux** (reactive programming)
-   **Spring Data R2DBC** (MySQL reactive driver)
-   **Spring Data MongoDB Reactive**
-   **Lombok**
-   **MapStruct**
-   **Swagger / OpenAPI 3**
-   **JUnit5 / Mockito**
-   **Docker & Docker Compose**

------------------------------------------------------------------------
## 📋 Requirements

-   Java 21
-   Maven 3.9+
-   Docker & Docker Compose
-   MongoDB 6.x
-   MySQL 8.x

------------------------------------------------------------------------
## 🛠️ Installation

Clone the repository:

``` bash
git clone https://github.com/anaberod/S05.01_Blackjack.git
cd S05.01_Blackjack
```

Build the project:

``` bash
./mvnw clean package -DskipTests
```

------------------------------------------------------------------------

## ▶️ Execution

Run locally with Maven:

``` bash
./mvnw spring-boot:run
```

Or run with Docker Compose (MongoDB + MySQL + API):

``` bash
docker compose up --build
```

The API will be available at:\
👉 <http://localhost:8080/swagger-ui.html>

------------------------------------------------------------------------
## 🌐 Deployment (Docker)

### Build the image

``` bash
docker build -t blackjack-api:local .
```

### Run the container

``` bash
docker run --rm -p 8080:8080 blackjack-api:local
```

### Push to GitHub Packages

``` bash
docker tag blackjack-api:local ghcr.io/anaberod/s05.01_blackjack:v0.1.0
docker push ghcr.io/anaberod/s05.01_blackjack:v0.1.0
```

------------------------------------------------------------------------

## 📖 API Endpoints

### Player

-   **POST** `/player` → Create new player\
-   **PUT** `/player/{id}` → Rename player

### Game

-   **POST** `/game/new` → Create new game\
-   **GET** `/game/{id}` → Get game details\
-   **POST** `/game/{id}/play` → Play a move\
-   **DELETE** `/game/{id}/delete` → Delete a game

### Ranking

-   **GET** `/ranking` → Get players ranking

All endpoints are documented in Swagger.

------------------------------------------------------------------------
## 🧪 Tests

Implemented with **JUnit5** and **Mockito**.

Available tests:

-   `BlackjackApplicationTests` → verifies Spring context loads\
-   `GameControllerTest` → tests REST endpoints of game controller\
-   `GameLogicTest` → validates Blackjack game logic (dealer vs player)\
-   `GameServiceTest` → ensures game service reactive methods\
-   `PlayerControllerTest` → tests REST endpoints of player controller\
-   `PlayerServiceTest` → ensures player service reactive methods\
-   `RankingControllerTest` → tests REST endpoints of ranking
    controller\
-   `RankingServiceTest` → ensures ranking service reactive methods

Run all tests:

``` bash
./mvnw test
```

------------------------------------------------------------------------

## 🤝 Contributing

1.  Fork the repository\
2.  Create a feature branch (`git checkout -b feature/xyz`)\
3.  Commit your changes (`git commit -m 'Add xyz'`)\
4.  Push the branch (`git push origin feature/xyz`)\
5.  Open a Pull Request

------------------------------------------------------------------------

## 📄 License

This project is for educational purposes (IT Academy).\
Feel free to explore and adapt it.