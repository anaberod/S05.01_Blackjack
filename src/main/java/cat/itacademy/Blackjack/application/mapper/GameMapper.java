package cat.itacademy.Blackjack.application.mapper;

import cat.itacademy.Blackjack.application.logic.GameLogic;
import cat.itacademy.Blackjack.dto.GameResponse;
import cat.itacademy.Blackjack.model.Card;
import cat.itacademy.Blackjack.model.Game;
import cat.itacademy.Blackjack.model.enums.GameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameMapper {

    private final GameLogic gameLogic;


    public GameResponse toResponse(Game game) {
        boolean finished = game.getStatus() == GameStatus.FINISHED;


        GameResponse.HandView playerView = GameResponse.HandView.builder()
                .cards(toCardViews(game.getPlayerHand().getCards()))
                .value(gameLogic.handValue(game.getPlayerHand()))
                .build();


        List<GameResponse.CardView> dealerCardsView = new ArrayList<>();
        int dealerValue = 0;

        if (finished) {
            dealerCardsView = toCardViews(game.getDealerHand().getCards());
            dealerValue = gameLogic.handValue(game.getDealerHand());
        } else {
            List<Card> dealerCards = game.getDealerHand().getCards();
            if (!dealerCards.isEmpty()) {
                dealerCardsView.add(toCardView(dealerCards.get(0))); // 1 visible
            }
            dealerCardsView.add(new GameResponse.CardView("HIDDEN", "HIDDEN")); // la oculta
        }

        GameResponse.HandView dealerView = GameResponse.HandView.builder()
                .cards(dealerCardsView)
                .value(dealerValue)
                .build();

        return GameResponse.builder()
                .gameId(game.getId())
                .playerId(game.getPlayerId())
                .status(game.getStatus())
                .winner(game.getWinner())
                .playerHand(playerView)
                .dealerHand(dealerView)
                .build();
    }



    private List<GameResponse.CardView> toCardViews(List<Card> cards) {
        List<GameResponse.CardView> out = new ArrayList<>(cards.size());
        for (Card c : cards) out.add(toCardView(c));
        return out;
    }

    private GameResponse.CardView toCardView(Card c) {
        return new GameResponse.CardView(c.getSuit().name(), c.getRank().name());
    }
}
