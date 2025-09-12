package cat.itacademy.Blackjack.application.service;

import cat.itacademy.Blackjack.application.mapper.GameResultMapper;
import cat.itacademy.Blackjack.dto.RankingItem;
import cat.itacademy.Blackjack.model.GameResult;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.model.enums.Winner;
import cat.itacademy.Blackjack.repository.GameResultRepository;
import cat.itacademy.Blackjack.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GameResultRepository gameResultRepository; // Histórico (MySQL R2DBC)
    private final PlayerRepository playerRepository;         // Jugadores (MySQL R2DBC)
    private final GameResultMapper gameResultMapper;         // (Opcional) si quieres mapear aquí

    /**
     * Construye el ranking agregando el histórico de partidas:
     * - Cuenta victorias, derrotas y empates por jugador.
     * - Completa el nombre del jugador.
     * - Ordena por número de victorias (desc).
     */
    public Flux<RankingItem> getRanking() {
        return gameResultRepository.findAll()
                // Agrupamos todos los GameResult por playerId
                .collectMultimap(GameResult::getPlayerId)
                .flatMapMany(map -> {
                    // map: playerId -> Collection<GameResult>
                    List<RankingItem> items = new ArrayList<>();

                    for (Map.Entry<Long, Collection<GameResult>> e : map.entrySet()) {
                        Long playerId = e.getKey();
                        Collection<GameResult> results = e.getValue();

                        int wins = (int) results.stream().filter(r -> r.getWinner() == Winner.PLAYER).count();
                        int losses = (int) results.stream().filter(r -> r.getWinner() == Winner.DEALER).count();
                        int draws = (int) results.stream().filter(r -> r.getWinner() == Winner.DRAW).count();

                        items.add(RankingItem.builder()
                                .playerId(playerId)
                                .playerName(null) // se completa después
                                .wins(wins)
                                .losses(losses)
                                .draws(draws)
                                .build());
                    }

                    // Orden por victorias (desc)
                    items = items.stream()
                            .sorted(Comparator.comparingInt(RankingItem::getWins).reversed())
                            .collect(Collectors.toList());

                    // Completar nombres en bloque
                    List<Long> ids = items.stream().map(RankingItem::getPlayerId).toList();

                    return playerRepository.findAllById(ids)
                            .collectMap(Player::getId, Player::getName)
                            .flatMapMany(idToName -> Flux.fromIterable(
                                    items.stream()
                                            .map(it -> {
                                                it.setPlayerName(idToName.get(it.getPlayerId()));
                                                return it;
                                            })
                                            .toList()
                            ));
                });
    }

}
