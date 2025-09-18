package cat.itacademy.Blackjack.controller;

import cat.itacademy.Blackjack.application.service.RankingService;
import cat.itacademy.Blackjack.dto.RankingItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@Tag(name = "Ranking", description = "Check the ranking by number of victories")
@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;


    @Operation(summary = "Get ranking", description = "Returns the list of players sorted by wins (descending).")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingItem.class)))
    )
    @GetMapping
    public Flux<RankingItem> getRanking() {
        return rankingService.getRanking();
    }
}
