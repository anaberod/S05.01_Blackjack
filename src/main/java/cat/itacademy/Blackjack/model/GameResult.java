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


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("game_results")
public class GameResult {


    @Id
    private Long id;


    @Column("player_id")
    private Long playerId;


    @Column("game_id")
    private String gameId;


    @Column("winner")
    private Winner winner;


    @Column("player_score")
    private Integer playerScore;


    @Column("dealer_score")
    private Integer dealerScore;


    @CreatedDate
    @Column("finished_at")
    private Instant finishedAt;
}

