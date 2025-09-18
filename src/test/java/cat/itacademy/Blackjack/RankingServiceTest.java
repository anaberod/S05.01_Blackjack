package cat.itacademy.Blackjack;

import cat.itacademy.Blackjack.application.mapper.GameResultMapper;
import cat.itacademy.Blackjack.application.service.RankingService;
import cat.itacademy.Blackjack.dto.RankingItem;
import cat.itacademy.Blackjack.model.GameResult;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.model.enums.Winner;
import cat.itacademy.Blackjack.repository.sql.GameResultRepository;
import cat.itacademy.Blackjack.repository.sql.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyIterable;


@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock GameResultRepository gameResultRepository;
    @Mock PlayerRepository playerRepository;
    @Mock GameResultMapper gameResultMapper;

    @InjectMocks
    RankingService rankingService;


    private GameResult gr(Long playerId, Winner winner) {

        GameResult r = new GameResult();
        r.setPlayerId(playerId);
        r.setWinner(winner);
        r.setFinishedAt(Instant.now());
        return r;
    }

    private RankingItem item(Long id, String name, int wins) {
        return  RankingItem.builder()
                .playerId(id)
                .playerName(name)
                .wins(wins)
                .build();
    }


    @Test
    void getRanking_ordersByWinsDesc_andMapsNames() {

        Long p1 = 1L, p2 = 2L;
        var allResults = List.of(
                gr(p1, Winner.PLAYER), gr(p1, Winner.PLAYER), gr(p1, Winner.PLAYER),
                gr(p2, Winner.PLAYER)
        );
        when(gameResultRepository.findAll()).thenReturn(Flux.fromIterable(allResults));


        var players = List.of(
                Player.builder().id(p1).name("Ana").build(),
                Player.builder().id(p2).name("Bob").build()
        );
        when(playerRepository.findAllById(anyIterable())).thenReturn(Flux.fromIterable(players));


        when(gameResultMapper.toRankingItem(eq(p1), eq("Ana"), any(Collection.class)))
                .thenAnswer(inv -> {
                    var list = (Collection<?>) inv.getArgument(2);
                    return item(p1, "Ana", list.size());
                });
        when(gameResultMapper.toRankingItem(eq(p2), eq("Bob"), any(Collection.class)))
                .thenAnswer(inv -> {
                    var list = (Collection<?>) inv.getArgument(2);
                    return item(p2, "Bob", list.size());
                });


        var out = rankingService.getRanking();


        StepVerifier.create(out)
                .expectNextMatches(r -> r.getPlayerId().equals(p1) && r.getWins() == 3 && r.getPlayerName().equals("Ana"))
                .expectNextMatches(r -> r.getPlayerId().equals(p2) && r.getWins() == 1 && r.getPlayerName().equals("Bob"))
                .verifyComplete();

        verify(gameResultRepository).findAll();
        verify(playerRepository).findAllById(anyIterable());
        verify(gameResultMapper, times(1)).toRankingItem(eq(p1), eq("Ana"), any(Collection.class));
        verify(gameResultMapper, times(1)).toRankingItem(eq(p2), eq("Bob"), any(Collection.class));
    }


    @Test
    void getRanking_usesUnknownWhenPlayerNameMissing() {
        Long p99 = 99L;
        var results = List.of(
                gr(p99, Winner.PLAYER), gr(p99, Winner.DEALER) // da igual, solo probamos “nombre”
        );
        when(gameResultRepository.findAll()).thenReturn(Flux.fromIterable(results));


        when(playerRepository.findAllById(anyIterable())).thenReturn(Flux.empty());

        when(gameResultMapper.toRankingItem(eq(p99), eq("(unknown)"), any(Collection.class)))
                .thenAnswer(inv -> {
                    var list = (Collection<?>) inv.getArgument(2);

                    return item(p99, "(unknown)", list.size());
                });

        var out = rankingService.getRanking();

        StepVerifier.create(out)
                .expectNextMatches(r -> r.getPlayerId().equals(p99)
                        && r.getPlayerName().equals("(unknown)")
                        && r.getWins() == 2)
                .verifyComplete();

        verify(gameResultRepository).findAll();
        verify(playerRepository).findAllById(anyIterable());
        verify(gameResultMapper).toRankingItem(eq(p99), eq("(unknown)"), any(Collection.class));
    }
}
