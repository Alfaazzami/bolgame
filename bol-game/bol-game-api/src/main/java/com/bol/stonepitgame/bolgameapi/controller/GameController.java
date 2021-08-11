package com.bol.stonepitgame.bolgameapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bol.stonepitgame.bolgameapi.constants.BolConstants;
import com.bol.stonepitgame.bolgameapi.domain.Game;
import com.bol.stonepitgame.bolgameapi.exceptions.BolException;
import com.bol.stonepitgame.bolgameapi.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/gameapi")
@Slf4j
@Api(value = "Bol game API. Endpoints for Creating and Playing the Game")
public class GameController {

    @Autowired
    private GameService gameService;

    private Integer pitStones = 6;

    @PostMapping
    @ApiOperation(value = "Endpoint for creating new Bol game instance. It returns a Game object with unique "
                    + "GameId used for sowing the game",
                    produces = "Application/JSON", response = Game.class, httpMethod = "POST")
    public ResponseEntity<Game> createGame() throws Exception {

        log.info("creating new game instance... ");
        Game game = gameService.createNewGame(pitStones);
        log.info("Game instance created. Id=" + game.getId());
        log.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(game));
        gameService.updateGame(game);
        return ResponseEntity.ok(game);
    }

    @PutMapping(value = "{gameId}/pits/{pitId}")
    @ApiOperation(value = "Endpoint for playing the game. It keeps the history of the Game instance for consecutive "
                    + "requests. ",
                    produces = "Application/JSON", response = Game.class, httpMethod = "PUT")
    public ResponseEntity<Game> playGame(
                    @PathVariable(value = "gameId") String gameId,
                    @PathVariable(value = "pitId") Integer pitId) throws Exception {

        log.info("Play event triggered for the GameId {} pitIndex {}", gameId, pitId);
        if (pitId == null || pitId < 1 || pitId >= BolConstants.leftPitId || pitId == BolConstants.rightPitId)
            throw new BolException("Invalid pit Index. It should be between 1 and 6 or 8 and 13");
        Game game = gameService.loadGame(gameId);
        game = gameService.play(game, pitId);
        gameService.updateGame(game);
        log.info("Play event is executed for the GameId {} pitIndex {}", gameId, pitId);
        log.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(game));
        return ResponseEntity.ok(game);
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Endpoint for returning the latest status of the Game",
                    produces = "Application/JSON", response = Game.class, httpMethod = "GET")
    public ResponseEntity<Game> gameStatus(
                    @PathVariable(value = "id") String gameId) throws Exception {
        return ResponseEntity.ok(gameService.loadGame(gameId));
    }

}

