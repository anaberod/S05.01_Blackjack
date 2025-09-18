package cat.itacademy.Blackjack.application.mapper;

import cat.itacademy.Blackjack.dto.RankingItem;
import cat.itacademy.Blackjack.model.GameResult;
import cat.itacademy.Blackjack.model.enums.Winner;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GameResultMapper {


    public RankingItem toRankingItem(Long playerId, String playerName, Collection<GameResult> results) {
        int wins = 0, losses = 0, draws = 0;

        for (GameResult r : results) {
            Winner w = r.getWinner();
            if (w == Winner.PLAYER) wins++;
            else if (w == Winner.DEALER) losses++;
            else if (w == Winner.DRAW) draws++;
        }

        return RankingItem.builder()
                .playerId(playerId)
                .playerName(playerName)
                .wins(wins)
                .losses(losses)
                .draws(draws)
                .build();
    }
}
