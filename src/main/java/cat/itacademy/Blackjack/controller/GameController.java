package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.GameService;
import cat.itacademy.Blackjack.dto.CreateGameRequest;
import cat.itacademy.Blackjack.dto.GameResponse;
import cat.itacademy.Blackjack.dto.PlayRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



@Tag(name = "Game", description = "game endpoints")
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;


    @Operation(summary = "Create new game", description = "Start a single-player blackjack game.If there is a natural blackjack, the game is closed immediately. ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game created",
                    content = @Content(schema = @Schema(implementation = GameResponse.class))),
            @ApiResponse(responseCode = "404", description = "Player not found", content = @Content)
    })
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GameResponse> createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request);
    }


    @Operation(summary = "Get game by id",description = "Returns the current state of a game")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = GameResponse.class))),
            @ApiResponse(responseCode = "404", description = "Game not found", content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<GameResponse> getGame(@PathVariable String id) {
        return gameService.getGame(id);
    }


    @Operation(summary = "Play action", description = "Execute a move (HIT o STAND) in the game")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update status",
                    content = @Content(schema = @Schema(implementation = GameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid move", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Game is already over", content = @Content)
    })
    @PostMapping("/{id}/play")
    public Mono<GameResponse> play(
            @PathVariable String id,
            @RequestBody PlayRequest request) {
        return gameService.play(id, request);
    }
}
