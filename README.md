# bolgame

Technologies used:

  - JAVA 11
  - SpringBoot2
  - Maven - build tool
  - MongoDB - repository
  - Junit
  - Swagger - for API

# How to build and run

  - Go to cd /bol-game
  - Run command 'mvn clean install'
  - Now navigate to cd /bol-game/bol-game-api
  - Run comman mvn spring-boot:run

Application will be hosted in "http://localhost:8080"

Swagger UI in "http://localhost:8080/swagger-ui.html"

- three api's are developed as part of the case study.
- one - to create a new game with default number of stones in a pit
    - http://localhost:8080/gameapi
- two - to play the game whenever a stone is sowen in the further pits
    - http://localhost:8080/gameapi/{gameId}/pits/{pitId}
- three - to load the status of the game
    - http://localhost:8080/gameapi/{gameId}

