package cat.itacademy.Blackjack;

import cat.itacademy.Blackjack.application.service.RankingService;
import cat.itacademy.Blackjack.controller.RankingController;
import cat.itacademy.Blackjack.dto.RankingItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingControllerTest {

    @Mock RankingService rankingService;
    @InjectMocks RankingController controller;

    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void getRanking_returns200_andListOrdered() {
        List<RankingItem> data = List.of(
                RankingItem.builder().playerId(1L).playerName("Ana").wins(3).build(),
                RankingItem.builder().playerId(2L).playerName("Bea").wins(1).build()
        );

        // Si tu servicio devuelve Flux<RankingItem>, usa: when(rankingService.getRanking()).thenReturn(Flux.fromIterable(data));
        when(rankingService.getRanking()).thenReturn(Flux.fromIterable(data));

        webTestClient.get()
                .uri("/ranking") // ajusta si tu controller mapea a otra ruta/base path
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].playerId").isEqualTo(1)
                .jsonPath("$[0].wins").isEqualTo(3)
                .jsonPath("$[1].playerId").isEqualTo(2)
                .jsonPath("$[1].wins").isEqualTo(1);

        verify(rankingService).getRanking();
        verifyNoMoreInteractions(rankingService);
    }
}
