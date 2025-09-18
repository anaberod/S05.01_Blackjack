package cat.itacademy.Blackjack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingItem {

    private Long playerId;
    private String playerName;
    private int wins;
    private int losses;
    private int draws;
}
