package cat.itacademy.Blackjack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateGameRequest {

    @NotNull(message = "playerId is required")
    private Long playerId;
}
