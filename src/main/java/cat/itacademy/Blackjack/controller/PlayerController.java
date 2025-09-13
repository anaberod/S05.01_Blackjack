package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.PlayerService;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para operaciones relacionadas con jugadores.
 * Expone los endpoints definidos en el enunciado.
 */
@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    /**
     * Crear un nuevo jugador.
     * POST /player
     */
    @PostMapping
    public Mono<PlayerView> createPlayer(@RequestBody CreatePlayerRequest request) {
        return playerService.createPlayer(request);
    }

    /**
     * Cambiar el nombre de un jugador existente.
     * PUT /player/{id}
     */
    @PutMapping("/{id}")
    public Mono<PlayerView> renamePlayer(
            @PathVariable Long id,
            @RequestBody PlayerRenameRequest request) {
        return playerService.renamePlayer(id, request);
    }
}
