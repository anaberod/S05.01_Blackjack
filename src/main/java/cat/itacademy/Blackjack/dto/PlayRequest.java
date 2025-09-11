package cat.itacademy.Blackjack.dto;

import cat.itacademy.Blackjack.model.enums.Action;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Petición para realizar una jugada en una partida existente.
 * Solo indica la acción del jugador: HIT (pedir carta) o STAND (plantarse).
 */
@Data
public class PlayRequest {

    @NotNull(message = "action is required")
    private Action action;
}
