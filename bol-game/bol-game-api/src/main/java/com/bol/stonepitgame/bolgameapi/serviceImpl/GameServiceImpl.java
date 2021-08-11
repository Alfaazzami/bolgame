package com.bol.stonepitgame.bolgameapi.serviceImpl;

import java.sql.SQLDataException;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bol.stonepitgame.bolgameapi.constants.BolConstants;
import com.bol.stonepitgame.bolgameapi.domain.Game;
import com.bol.stonepitgame.bolgameapi.domain.PlayerTurns;
import com.bol.stonepitgame.bolgameapi.domain.SmallPit;
import com.bol.stonepitgame.bolgameapi.repository.BolGameRepository;
import com.bol.stonepitgame.bolgameapi.service.GameService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    @Autowired
    private BolGameRepository bolGameRepository;

    /**
     * Method to create a game instance and store in DB
     * @param stones - initial stones into the pit to create game
     * @return the game created
     */
    @Override public Game createNewGame(final int stones) {
        final Game game = new Game(stones);
        bolGameRepository.save(game);
        log.info("New Game instance saved in DB");
        return game;
    }

    /**
     * Method to load the game existing already
     * @param id of the game existing
     * @return the game already created and in db
     * @throws SQLDataException
     */
    @Override
    public Game loadGame(final String id) throws SQLDataException {
        final Optional<Game> gameOptional = bolGameRepository.findById(id);
        if (!gameOptional.isPresent())
            throw new SQLDataException("Game id " + id + " not found!");
        log.info("Game instance loaded from DB");
        return gameOptional.get();
    }

    /**
     * Method to update the game instance being played in DB
     * @param game
     * @return updated game
     */
    public Game updateGame(Game game) {
        game = bolGameRepository.save(game);
        log.info("Game instance {} updated in DB", game.getId());
        return game;
    }

    /**
     * Method to store and update the db on every event of sowing
     * @param game            , being played
     * @param requestedPitId, pit index for which the stones are being sown
     * @return the game after every event of sowing
     * @throws Exception
     */
    @Override
    public Game play(final Game game, final int requestedPitId) throws Exception {

        //STEP1: Check if there is no movement ( i.e when the pit id is the big pits id),if so return the game
        if (requestedPitId == BolConstants.rightPitId || requestedPitId == BolConstants.leftPitId)
            return game;

        //STEP2: Set the player turn for the first move of the game based on the pit id
        if (game.getPlayerTurn() == null) {
            if (requestedPitId < BolConstants.rightPitId)
                game.setPlayerTurn(PlayerTurns.PlayerA);
            else
                game.setPlayerTurn(PlayerTurns.PlayerB);
        }

        //STEP3: Check if the correct player is playing the turn
        if (game.getPlayerTurn() == PlayerTurns.PlayerA && requestedPitId > BolConstants.rightPitId ||
                        game.getPlayerTurn() == PlayerTurns.PlayerB && requestedPitId < BolConstants.rightPitId)
            return game;

        //STEP4: Get the no.of stones in the requested pit ID
        SmallPit selectedPit = game.getPit(requestedPitId);
        int stones = selectedPit.getStones();

        //STEP5: If the pit is empty, then No movement
        if (stones == BolConstants.emptyStone)
            return game;
        selectedPit.setStones(BolConstants.emptyStone);

        //STEP6: keep the pit index, for sowing the stones in right pits
        game.setCurrentPitIndex(requestedPitId);

        //STEP7: sow all stones except the last one
        IntStream.range(0, stones - 1)
                        .forEach(index -> {
                            try {
                                sowRight(game, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

        //STEP8: Now sow the last stone
        sowRight(game, true);
        int currentPitIndex = game.getCurrentPitIndex();

        //STEP9: switch the turn if the last stone was not sown in any of big pits
        if (currentPitIndex != BolConstants.rightPitId && currentPitIndex != BolConstants.leftPitId)
            game.setPlayerTurn(nextTurn(game.getPlayerTurn()));
        return game;
    }

    /**
     * Method to be called to sow the stone on one pit on right
     * @param game
     * @param lastStone
     * @throws Exception
     */
    private void sowRight(Game game, Boolean lastStone) throws Exception {
        int currentPitIndex = game.getCurrentPitIndex() % BolConstants.totalPits + 1;
        PlayerTurns playerTurn = game.getPlayerTurn();
        if ((currentPitIndex == BolConstants.rightPitId && playerTurn == PlayerTurns.PlayerB) ||
                        (currentPitIndex == BolConstants.leftPitId && playerTurn == PlayerTurns.PlayerA)) {
            currentPitIndex = currentPitIndex % BolConstants.totalPits + 1;
        }
        game.setCurrentPitIndex(currentPitIndex);
        SmallPit targetPit = game.getPit(currentPitIndex);

        //STEP7A: If the sown pit is not the last stone, sow and return to game
        if (!lastStone || currentPitIndex == BolConstants.rightPitId || currentPitIndex == BolConstants.leftPitId) {
            targetPit.sow();
            return;
        }

        //STEP8A: If the sown pit is the last stone, check the opposite player's pit status
        SmallPit oppositePit = game.getPit(BolConstants.totalPits - currentPitIndex);

        //STEP8B: Sowing the last stone and the current player's pit is empty but the opposite pit is not empty,
        //collect the opposite's Pit stones plus the last stone and add them to the Big Pit of current player and
        // make the opposite Pit empty
        if (targetPit.isEmpty() && !oppositePit.isEmpty()) {
            Integer oppositePitStones = oppositePit.getStones();
            oppositePit.clear();
            Integer pitHouseIndex = currentPitIndex < BolConstants.rightPitId ? BolConstants.rightPitId : BolConstants.leftPitId;
            SmallPit pitHouse = game.getPit(pitHouseIndex);
            pitHouse.addStones(oppositePitStones + 1);
            return;
        }
        targetPit.sow();
    }

    public PlayerTurns nextTurn(PlayerTurns currentTurn) {
        if (currentTurn == PlayerTurns.PlayerA)
            return PlayerTurns.PlayerB;
        return PlayerTurns.PlayerA;
    }
}
