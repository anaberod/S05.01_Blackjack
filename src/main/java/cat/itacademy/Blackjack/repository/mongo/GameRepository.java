package cat.itacademy.Blackjack.repository.mongo;

import cat.itacademy.Blackjack.model.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio reactivo para partidas (Game) en MongoDB.
 * Guarda y recupera el estado de las partidas en curso.
 */
@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {//nterface que ya trae un montón de métodos preparados
    // ReactiveMongoRepository ya te da métodos como:
    // findById(String id), save(Game game), deleteById(String id), findAll()
}

