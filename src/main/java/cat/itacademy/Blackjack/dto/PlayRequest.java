package cat.itacademy.Blackjack.dto;

import cat.itacademy.Blackjack.model.enums.Action;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class PlayRequest {

    @NotNull(message = "action is required")
    private Action action;
}
