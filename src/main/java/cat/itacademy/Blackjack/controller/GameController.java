package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.GameService;
import cat.itacademy.Blackjack.dto.CreateGameRequest;
import cat.itacademy.Blackjack.dto.GameResponse;
import cat.itacademy.Blackjack.dto.PlayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para gestionar partidas de Blackjack.
 * Expone endpoints para crear, consultar y jugar partidas.
 */
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * Crear una nueva partida para un jugador existente.
     * POST /game/new
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GameResponse> createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request);
    }

    /**
     * Consultar una partida por su ID.
     * GET /game/{id}
     */
    @GetMapping("/{id}")
    public Mono<GameResponse> getGame(@PathVariable String id) {
        return gameService.getGame(id);
    }

    /**
     * Realizar una jugada (HIT o STAND).
     * POST /game/{id}/play
     */
    @PostMapping("/{id}/play")
    public Mono<GameResponse> play(
            @PathVariable String id,
            @RequestBody PlayRequest request) {
        return gameService.play(id, request);
    }
}
