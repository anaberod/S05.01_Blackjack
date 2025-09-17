package cat.itacademy.Blackjack.application.service;

import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.error.PlayerNotFound;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.repository.sql.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    /** Crear jugador con nombre único (devuelve PlayerView directo) */
    public Mono<PlayerView> createPlayer(CreatePlayerRequest req) {
        final String name = sanitizeName(req.getName());
        if (name == null || name.isBlank()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "name cannot be blank"));
        }

        return playerRepository.findViewByName(name)
                .flatMap(pv -> Mono.<PlayerView>error(new ResponseStatusException(HttpStatus.CONFLICT,"player name already exists")))
                .switchIfEmpty(Mono.defer(() ->
                        playerRepository.save(Player.builder().name(name).build())
                                .map(p -> new PlayerView(p.getId(), p.getName()))
                ));
    }

    public Mono<PlayerView> renamePlayer(Long id, PlayerRenameRequest req) {
        final String newName = sanitizeName(req.getNewName());
        if (newName == null || newName.isBlank()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"newName cannot be blank"));
        }

        return playerRepository.findViewById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFound(id)))
                .flatMap(current -> {
                    // Si no cambia el nombre, devolvemos tal cual
                    if (newName.equals(current.getName())) {
                        return Mono.just(current);
                    }

                    // ¿Existe ese nombre en otro jugador?
                    return playerRepository.findViewByName(newName)
                            .flatMap(existing -> {
                                // Si existe y NO es el mismo id -> conflicto
                                if (!existing.getId().equals(id)) {
                                    return Mono.<PlayerView>error(
                                            new ResponseStatusException(HttpStatus.CONFLICT,"player name already exists"));
                                }
                                // (En la práctica no llegarías aquí porque newName != current.getName())
                                return Mono.just(existing);
                            })
                            // Si no existe, hacemos el UPDATE
                            .switchIfEmpty(
                                    playerRepository.updateName(id, newName)
                                            .flatMap(rows -> rows > 0
                                                    ? playerRepository.findViewById(id) // releemos y devolvemos el DTO
                                                    : Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"rename failed")))
                            );
                });
    }


    private String sanitizeName(String raw) {
        if (raw == null) return null;
        return raw.trim().replaceAll("\\s+", " ");
    }
}
