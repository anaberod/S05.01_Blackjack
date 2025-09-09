package cat.itacademy.Blackjack.model;


import cat.itacademy.Blackjack.model.enums.GameStatus;
import cat.itacademy.Blackjack.model.enums.Winner;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Representa una partida "viva" de Blackjack.
 * Se guarda en MongoDB porque cambia mucho durante el juego.
 * Aquí NO hay lógica del juego; solo datos (estado).
 */
@Document(collection = "games")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    /** Identificador único de la partida (Mongo). */
    @Id
    private String id;

    /** Jugador asociado a la partida (id en MySQL). */
    private String playerId;

    /** Mazo restante (las cartas que aún no han salido). */
    private List<Card> deck;

    /** Mano del jugador. */
    private Hand playerHand;

    /** Mano del dealer (la banca). */
    private Hand dealerHand;

    /** Estado de la partida: EN_CURSO o TERMINADA. */
    private GameStatus status;

    /** Ganador al terminar: PLAYER, DEALER o DRAW. Nulo si la partida sigue en curso. */
    private Winner winner;

    /** Fechas de auditoría (si habilitas @EnableMongoAuditing). */
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
