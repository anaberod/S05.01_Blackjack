package cat.itacademy.Blackjack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Elemento del ranking de jugadores.
 * Representa la posición de un jugador con su nombre y número de victorias.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingItem {

    private Long playerId;
    private String playerName;
    private int wins;     // número de partidas ganadas

    // opcionales, si quisieras enriquecer el ranking
    private int losses;   // número de derrotas
    private int draws;    // número de empates
}
