package cat.itacademy.Blackjack.repository;

import cat.itacademy.Blackjack.model.GameResult;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GameResultRepository extends R2dbcRepository<GameResult, Long> {

    // Todos los resultados históricos de un jugador (para ranking)
    Flux<GameResult> findByPlayerId(Long playerId);
}
