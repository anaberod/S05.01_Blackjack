package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.PlayerService;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/player") // prefijo común para el recurso jugador
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /** Crear un nuevo jugador: POST /player -> 201 Created */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlayerView> createPlayer(@RequestBody CreatePlayerRequest request) {
        return playerService.createPlayer(request);
    }

    /** Renombrar un jugador existente: PUT /player/{id} -> 200 OK */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PlayerView> renamePlayer(@PathVariable Long id,
                                         @RequestBody PlayerRenameRequest request) {
        return playerService.renamePlayer(id, request);
    }
}