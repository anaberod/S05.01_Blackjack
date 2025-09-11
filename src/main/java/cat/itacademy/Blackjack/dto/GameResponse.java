package cat.itacademy.Blackjack.dto;

import cat.itacademy.Blackjack.model.enums.GameStatus;
import cat.itacademy.Blackjack.model.enums.Winner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta que devuelve el estado de una partida de Blackjack.
 * Se usa en GET /game/{id}, POST /game/new y POST /game/{id}/play.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

    private String gameId;
    private Long playerId;
    private GameStatus status;
    private Winner winner; // null si la partida sigue en curso
    private HandView playerHand;
    private HandView dealerHand;

    /**
     * Representación simplificada de una mano.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HandView {
        private List<CardView> cards;
        private int value;
    }

    /**
     * Representación simplificada de una carta.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardView {
        private String suit;
        private String rank;
    }
}
