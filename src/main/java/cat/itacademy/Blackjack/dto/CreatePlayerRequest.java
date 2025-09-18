package cat.itacademy.Blackjack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class CreatePlayerRequest {

    @NotBlank(message = "name is required")
    @Size(min = 3, max = 30, message = "name must be 3-30 characters")
    private String name;
}
