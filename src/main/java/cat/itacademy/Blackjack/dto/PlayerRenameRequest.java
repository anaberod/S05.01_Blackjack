package cat.itacademy.Blackjack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Petición para cambiar el nombre de un jugador.
 * Se usa en el endpoint PUT /player/{id}.
 */
@Data
public class PlayerRenameRequest {

    @NotBlank(message = "newName is required")
    private String newName;
}
