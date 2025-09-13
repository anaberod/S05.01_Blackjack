package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.RankingService;
import cat.itacademy.Blackjack.dto.RankingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * Controlador REST para exponer el ranking de jugadores.
 */
@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * Obtener el ranking de jugadores ordenado por victorias.
     * GET /ranking
     */
    @GetMapping
    public Flux<RankingItem> getRanking() {
        return rankingService.getRanking();
    }
}
