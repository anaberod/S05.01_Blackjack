package cat.itacademy.Blackjack.repository;

import cat.itacademy.Blackjack.model.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para jugadores (MySQL vía R2DBC).
 * NO usa JPA. Provee operaciones CRUD reactivas sobre Player.
 */
@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Long> {

    // Búsqueda por nombre (útil para crear y para renombrar evitando duplicados)
    Mono<Player> findByName(String name);
}
