package cat.itacademy.Blackjack.repository.sql;

import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.model.Player;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Long> {

    // --- PROYECCIONES A DTO (PlayerView) ---
    @Query("""
           SELECT id AS id, name AS name
           FROM players
           WHERE name = :name
           """)
    Mono<PlayerView> findViewByName(@Param("name") String name);

    @Query("""
           SELECT id AS id, name AS name
           FROM players
           WHERE id = :id
           """)
    Mono<PlayerView> findViewById(@Param("id") Long id);

    // --- ENTIDAD (si la necesitas en otros puntos) ---
    @Query("""
           SELECT id, name,
                  created_at AS createdAt,
                  updated_at AS updatedAt
           FROM players
           WHERE id = :id
           """)
    Mono<Player> findOneById(@Param("id") Long id);

    @Query("""
           SELECT id, name,
                  created_at AS createdAt,
                  updated_at AS updatedAt
           FROM players
           WHERE name = :name
           """)
    Mono<Player> findByName(@Param("name") String name);

    // --- UPDATE solo toca name y updated_at (nunca created_at) ---
    @Modifying
    @Query("""
           UPDATE players
           SET name = :name, updated_at = CURRENT_TIMESTAMP(6)
           WHERE id = :id
           """)
    Mono<Integer> updateName(@Param("id") Long id, @Param("name") String name);
}
