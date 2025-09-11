package cat.itacademy.Blackjack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta simplificada de un jugador.
 * Solo muestra la información básica: id y nombre.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerView {

    private Long id;
    private String name;
}
