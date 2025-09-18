package cat.itacademy.Blackjack.dto;

import cat.itacademy.Blackjack.model.enums.GameStatus;
import cat.itacademy.Blackjack.model.enums.Winner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HandView {
        private List<CardView> cards;
        private int value;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardView {
        private String suit;
        private String rank;
    }
}
