package com.bol.stonepitgame.bolgameapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bol.stonepitgame.bolgameapi.domain.Game;

@Repository
public interface BolGameRepository extends MongoRepository<Game, String> {
}
