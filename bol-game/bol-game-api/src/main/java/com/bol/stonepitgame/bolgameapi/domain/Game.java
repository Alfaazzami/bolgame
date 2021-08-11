package com.bol.stonepitgame.bolgameapi.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import static com.bol.stonepitgame.bolgameapi.constants.BolConstants.*;

@Data
public class Game implements Serializable {

    @Id
    private String id;

    private List<SmallPit> pits;

    private PlayerTurns playerTurn;

    @JsonIgnore
    private int currentPitIndex;

    public Game() {
        this (defaultPitStones);
    }

    public Game(int pitStones) {
        this.pits = Arrays.asList(
                        new SmallPit(firstPitPlayerA, pitStones),
                        new SmallPit(secondPitPlayerA, pitStones),
                        new SmallPit(thirdPitPlayerA, pitStones),
                        new SmallPit(forthPitPlayerA, pitStones),
                        new SmallPit(fifthPitPlayerA, pitStones),
                        new SmallPit(sixthPitPlayerA, pitStones),
                        new BigPit(rightPitId),
                        new SmallPit(firstPitPlayerB, pitStones),
                        new SmallPit(secondPitPlayerB, pitStones),
                        new SmallPit(thirdPitPlayerB, pitStones),
                        new SmallPit(forthPitPlayerB, pitStones),
                        new SmallPit(fifthPitPlayerB, pitStones),
                        new SmallPit(sixthPitPlayerB, pitStones),
                        new BigPit(leftPitId));
    }

    public Game(String id, Integer pitStones) {
        this (pitStones);
        this.id = id;
    }

    // returns the corresponding pit of particular index
    public SmallPit getPit(Integer pitIndex) throws Exception {
        try {
            return this.pits.get(pitIndex-1);
        }catch (Exception e){
            throw  new Exception("Invalid pitIndex:"+ pitIndex +" has given!");
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                        ", pits=" + pits +
                        ", playerTurn=" + playerTurn +
                        '}';
    }

}
