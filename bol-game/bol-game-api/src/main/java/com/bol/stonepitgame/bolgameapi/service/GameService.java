package com.bol.stonepitgame.bolgameapi.service;

import java.sql.SQLDataException;

import com.bol.stonepitgame.bolgameapi.domain.Game;

public interface GameService {

    Game createNewGame(final int stones);

    Game loadGame(String id) throws SQLDataException;

    Game updateGame(Game game);

    Game play(Game game, int requestedPitId) throws Exception;
}
