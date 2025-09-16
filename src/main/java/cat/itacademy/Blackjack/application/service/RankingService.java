package cat.itacademy.Blackjack.application.service;

import cat.itacademy.Blackjack.application.mapper.GameResultMapper;
import cat.itacademy.Blackjack.dto.RankingItem;
import cat.itacademy.Blackjack.model.GameResult;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.repository.sql.GameResultRepository;
import cat.itacademy.Blackjack.repository.sql.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GameResultRepository gameResultRepository; // Histórico (MySQL R2DBC)
    private final PlayerRepository playerRepository;         // Jugadores (MySQL R2DBC)
    private final GameResultMapper gameResultMapper;         // Construye RankingItem

    /**
     * Devuelve el ranking completo, ordenado por número de victorias (descendente).
     */
    public Flux<RankingItem> getRanking() {
        return gameResultRepository.findAll()
                .collectMultimap(GameResult::getPlayerId)                      // Map<Long, Collection<GameResult>>
                .flatMapMany(resultsByPlayer ->
                        playerRepository.findAllById(resultsByPlayer.keySet())     // Cargar nombres
                                .collectMap(Player::getId, Player::getName)            // Map<Long, String>
                                .flatMapMany(idToName ->
                                        Flux.fromStream(
                                                resultsByPlayer.entrySet().stream()
                                                        .map(entry -> {
                                                            Long playerId = entry.getKey();
                                                            String name = idToName.getOrDefault(playerId, "(unknown)");
                                                            return gameResultMapper.toRankingItem(playerId, name, entry.getValue());
                                                        })
                                                        .sorted(Comparator.comparingInt(RankingItem::getWins).reversed()) // victorias desc
                                        )
                                )
                );
    }
}
