package cat.itacademy.Blackjack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class PlayerRenameRequest {

    @NotBlank(message = "newName is required")
    private String newName;
}
