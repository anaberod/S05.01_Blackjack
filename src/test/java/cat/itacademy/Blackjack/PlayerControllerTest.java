package cat.itacademy.Blackjack;


import cat.itacademy.Blackjack.controller.PlayerController;
import cat.itacademy.Blackjack.application.service.PlayerService;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    PlayerService playerService;

    @InjectMocks
    PlayerController controller;

    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient
                .bindToController(controller)
                // si tienes GlobalExceptionHandler, puedes añadirlo:
                // .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createPlayer_returns201_andBody() {
        // given
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("Ana");

        PlayerView returned = PlayerView.builder()
                .id(11L)                // OJO: Long, no long
                .name("Ana")
                .build();

        when(playerService.createPlayer(any(CreatePlayerRequest.class)))
                .thenReturn(Mono.just(returned));

        // when + then
        webTestClient.post()
                .uri("/player")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PlayerView.class)
                .isEqualTo(returned);

        verifyNoMoreInteractions(playerService);
    }

    @Test
    void renamePlayer_returns200_andBody() {
        // given
        Long id = 7L;

        var renameReq = new cat.itacademy.Blackjack.dto.PlayerRenameRequest();
        renameReq.setNewName("Ana María");

        PlayerView returned = PlayerView.builder()
                .id(id)
                .name("Ana María")
                .build();

        when(playerService.renamePlayer(id, renameReq))
                .thenReturn(Mono.just(returned));

        // when + then
        webTestClient.put()
                .uri("/player/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(renameReq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PlayerView.class)
                .isEqualTo(returned);
    }
}
