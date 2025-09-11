package cat.itacademy.Blackjack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para crear una nueva partida.
 * El cliente debe indicar el jugador que jugará (por su id).
 */
@Data
public class CreateGameRequest {

    @NotNull(message = "playerId is required")
    private Long playerId;
}
