package cat.itacademy.Blackjack;

import cat.itacademy.Blackjack.application.service.GameService;
import cat.itacademy.Blackjack.controller.GameController;
import cat.itacademy.Blackjack.dto.CreateGameRequest;
import cat.itacademy.Blackjack.dto.GameResponse;
import cat.itacademy.Blackjack.dto.PlayRequest;
import cat.itacademy.Blackjack.error.GlobalExceptionHandler;
import cat.itacademy.Blackjack.model.enums.Action;
import cat.itacademy.Blackjack.model.enums.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    GameService gameService;

    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        var controller = new GameController(gameService);
        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createGame_returns201_andBody() {
        // given
        final Long playerId = Long.valueOf(1L);

        CreateGameRequest req = new CreateGameRequest();
        req.setPlayerId(playerId);

        GameResponse returned = GameResponse.builder()
                .gameId("g1")
                .playerId(playerId)
                .status(GameStatus.IN_PROGRESS)
                .build();

        when(gameService.createGame(any(CreateGameRequest.class)))
                .thenReturn(Mono.just(returned));

        // when + then
        webTestClient.post()
                .uri("/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(GameResponse.class)
                .isEqualTo(returned);
    }

    @Test
    void getGame_returns200_andBody() {
        // given
        final String gameId = "g42";
        final Long playerId = Long.valueOf(7L);

        GameResponse returned = GameResponse.builder()
                .gameId(gameId)
                .playerId(playerId)
                .status(GameStatus.IN_PROGRESS)
                .build();

        when(gameService.getGame(gameId))
                .thenReturn(Mono.just(returned));

        // when + then
        webTestClient.get()
                .uri("/game/{id}", gameId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(GameResponse.class)
                .isEqualTo(returned);
    }

    @Test
    void play_returns200_andBody() {
        // given
        final String gameId = "g99";
        final Long playerId = Long.valueOf(3L);

        PlayRequest req = new PlayRequest();
        req.setAction(Action.HIT);

        GameResponse returned = GameResponse.builder()
                .gameId(gameId)
                .playerId(playerId)
                .status(GameStatus.IN_PROGRESS) // podría ser FINISHED si acaba
                .build();

        when(gameService.play(gameId, req))
                .thenReturn(Mono.just(returned));

        // when + then
        webTestClient.post()
                .uri("/game/{id}/play", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(GameResponse.class)
                .isEqualTo(returned);
    }
}
