package cat.itacademy.Blackjack.application.service;

import cat.itacademy.Blackjack.application.mapper.PlayerMapper;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.error.PlayerNotFound;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.repository.sql.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    /** Crear un jugador nuevo con nombre único */
    public Mono<PlayerView> createPlayer(CreatePlayerRequest req) {
        final String name = req.getName();

        return playerRepository.findByName(name)
                // Si EXISTE, error
                .flatMap(p -> Mono.<PlayerView>error(new IllegalStateException("player name already exists")))
                // Si NO existe, creamos (¡nada de null aquí!)
                .switchIfEmpty(Mono.defer(() ->
                        playerRepository.save(Player.builder().name(name).build())
                                .map(playerMapper::toView)
                ));
    }

    /** Renombrar jugador, verificando que el nuevo nombre no exista */
    public Mono<PlayerView> renamePlayer(Long id, PlayerRenameRequest req) {
        final String newName = req.getNewName();

        return playerRepository.findByName(newName)
                // Si el nuevo nombre ya existe → error
                .flatMap(p -> Mono.<PlayerView>error(new IllegalStateException("player name already exists")))
                // Si no existe ese nombre, seguimos con el renombrado
                .switchIfEmpty(Mono.defer(() ->
                        playerRepository.findById(id)
                                // Ajusta la excepción al tipo que quieras testear
                                .switchIfEmpty(Mono.error(new PlayerNotFound(id)))
                                .flatMap(existing -> {
                                    existing.setName(newName);
                                    return playerRepository.save(existing);
                                })
                                .map(playerMapper::toView)
                ));
    }
    /** Limpieza de espacios en nombres */
    private String sanitizeName(String raw) {
        if (raw == null) return null;
        return raw.trim().replaceAll("\\s+", " ");
    }
}
