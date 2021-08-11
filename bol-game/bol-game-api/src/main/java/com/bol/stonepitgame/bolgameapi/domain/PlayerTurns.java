package com.bol.stonepitgame.bolgameapi.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PlayerTurns {

    PlayerA ("A"), PlayerB ("B");

    private String turn;

    PlayerTurns(String turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return turn;
    }
}
