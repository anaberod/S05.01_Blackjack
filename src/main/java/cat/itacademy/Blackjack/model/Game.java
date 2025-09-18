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


@Document(collection = "games")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Game {


    @Id
    private String id;


    private Long playerId;


    private List<Card> deck;


    private Hand playerHand;


    private Hand dealerHand;


    private GameStatus status;


    private Winner winner;


    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
