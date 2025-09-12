package cat.itacademy.Blackjack.application.service;

import cat.itacademy.Blackjack.application.mapper.PlayerMapper;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    /** Crear un jugador nuevo con nombre único */
    public Mono<PlayerView> createPlayer(CreatePlayerRequest request) {
        String name = sanitizeName(request.getName());

        return playerRepository.findByName(name)
                .flatMap(existing -> Mono.error(new IllegalStateException("player name already exists")))
                .switchIfEmpty(
                        playerRepository.save(Player.builder().name(name).build())
                                .map(playerMapper::toView)
                );
    }

    /** Renombrar jugador, verificando que el nuevo nombre no exista */
    public Mono<PlayerView> renamePlayer(Long playerId, PlayerRenameRequest request) {
        String newName = sanitizeName(request.getNewName());

        return playerRepository.findByName(newName)
                .flatMap(exists -> Mono.error(new IllegalStateException("player name already exists")))
                .switchIfEmpty(
                        playerRepository.findById(playerId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("player not found")))
                                .flatMap(player -> {
                                    player.setName(newName);
                                    return playerRepository.save(player);
                                })
                                .map(playerMapper::toView)
                );
    }

    /** Limpieza de espacios en nombres */
    private String sanitizeName(String raw) {
        if (raw == null) return null;
        return raw.trim().replaceAll("\\s+", " ");
    }
}
