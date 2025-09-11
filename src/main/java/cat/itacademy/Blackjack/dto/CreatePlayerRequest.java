package cat.itacademy.Blackjack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Petición para crear un jugador nuevo.
 * Se usa en el endpoint POST /player.
 */
@Data
public class CreatePlayerRequest {

    @NotBlank(message = "name is required")
    @Size(min = 3, max = 30, message = "name must be 3-30 characters")
    private String name;
}
