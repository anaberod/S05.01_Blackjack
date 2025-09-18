package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.PlayerService;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
@Tag(name = "Player", description = "Player-related endpoints")
public class PlayerController {

    private final PlayerService playerService;



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new player")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created player"),
            @ApiResponse(responseCode = "400", description = "Invalid data or duplicate name")
    })
    public Mono<PlayerView> createPlayer(@RequestBody @Valid CreatePlayerRequest request) {
        return playerService.createPlayer(request);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Rename a player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player successfully renamed"),
            @ApiResponse(responseCode = "404", description = "Player not foung")
    })
    public Mono<PlayerView> renamePlayer(@PathVariable Long id,
                                         @RequestBody PlayerRenameRequest request) {
        return playerService.renamePlayer(id, request);
    }
}