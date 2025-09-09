package cat.itacademy.Blackjack.model;


import cat.itacademy.Blackjack.model.enums.Winner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Resultado histórico de una partida de Blackjack.
 * Se guarda en MySQL (R2DBC) para calcular ranking y estadísticas.
 *
 * NOTA:
 * - No es JPA. No uses jakarta.persistence.* ni @Entity.
 * - Los enums se guardan como texto (nombre) en la columna.
 * - finishedAt se rellena automáticamente si activas @EnableR2dbcAuditing.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("game_results")
public class GameResult {

    /** Clave primaria (AUTO_INCREMENT en la tabla). */
    @Id
    private Long id;

    /** Id del jugador al que pertenece la partida (clave de players.id). */
    @Column("player_id")
    private Long playerId;

    /** Id de la partida en Mongo (útil para trazabilidad). */
    @Column("game_id")
    private String gameId;

    /** Quién ganó: PLAYER, DEALER o DRAW. */
    @Column("winner")
    private Winner winner;

    /** Puntuación final del jugador (0-31, normalmente ≤ 21). */
    @Column("player_score")
    private Integer playerScore;

    /** Puntuación final del dealer. */
    @Column("dealer_score")
    private Integer dealerScore;

    /** Momento en que finalizó la partida (se crea este registro). */
    @CreatedDate
    @Column("finished_at")
    private Instant finishedAt;
}

