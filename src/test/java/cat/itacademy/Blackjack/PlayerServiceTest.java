package cat.itacademy.Blackjack;

import cat.itacademy.Blackjack.application.mapper.PlayerMapper;
import cat.itacademy.Blackjack.application.service.PlayerService;
import cat.itacademy.Blackjack.dto.CreatePlayerRequest;
import cat.itacademy.Blackjack.dto.PlayerRenameRequest;
import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.error.PlayerNotFound;
import cat.itacademy.Blackjack.model.Player;
import cat.itacademy.Blackjack.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock PlayerRepository playerRepository;
    @Mock PlayerMapper playerMapper;

    @InjectMocks PlayerService playerService;

    // ========== createPlayer ==========

    @Test
    void createPlayer_success_whenNameIsUnique() {
        // given
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("Ana");

        // nombre libre
        when(playerRepository.findByName("Ana")).thenReturn(Mono.empty());
        // guardar devuelve el mismo Player con id asignado
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(inv -> {
                    Player p = inv.getArgument(0, Player.class).toBuilder().id(11L).build();
                    return Mono.just(p);
                });
        // mapper Player -> View
        when(playerMapper.toView(any(Player.class)))
                .thenAnswer(inv -> {
                    Player p = inv.getArgument(0, Player.class);
                    return PlayerView.builder().id(p.getId()).name(p.getName()).build();
                });

        // when
        Mono<PlayerView> out = playerService.createPlayer(req);

        // then
        StepVerifier.create(out)
                .expectNextMatches(v -> v.getId().equals(11L) && v.getName().equals("Ana"))
                .verifyComplete();

        verify(playerRepository).findByName("Ana");
        verify(playerRepository).save(any(Player.class));
        verify(playerMapper).toView(any(Player.class));
        verifyNoMoreInteractions(playerRepository, playerMapper);
    }

    @Test
    void createPlayer_fails_whenNameAlreadyExists() {
        // given
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("Ana");

        when(playerRepository.findByName("Ana"))
                .thenReturn(Mono.just(Player.builder().id(1L).name("Ana").build()));

        // when
        Mono<PlayerView> out = playerService.createPlayer(req);

        // then
        StepVerifier.create(out)
                .expectErrorSatisfies(ex -> assertTrue(ex instanceof IllegalStateException))
                .verify();

        verify(playerRepository).findByName("Ana");
        verifyNoMoreInteractions(playerRepository, playerMapper);
    }

    // ========== renamePlayer ==========

    @Test
    void renamePlayer_success_whenNewNameUniqueAndPlayerExists() {
        // given
        Long playerId = 5L;
        PlayerRenameRequest req = new PlayerRenameRequest();
        req.setNewName("Ana Maria");

        when(playerRepository.findByName("Ana Maria")).thenReturn(Mono.empty()); // nombre libre
        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(Player.builder().id(playerId).name("Old").build()));
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0, Player.class)));
        when(playerMapper.toView(any(Player.class)))
                .thenAnswer(inv -> {
                    Player p = inv.getArgument(0, Player.class);
                    return PlayerView.builder().id(p.getId()).name(p.getName()).build();
                });

        // when
        Mono<PlayerView> out = playerService.renamePlayer(playerId, req);

        // then
        StepVerifier.create(out)
                .expectNextMatches(v -> v.getId().equals(playerId) && v.getName().equals("Ana Maria"))
                .verifyComplete();

        verify(playerRepository).findByName("Ana Maria");
        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
        verify(playerMapper).toView(any(Player.class));
        verifyNoMoreInteractions(playerRepository, playerMapper);
    }

    @Test
    void renamePlayer_fails_whenPlayerNotFound() {
        // given
        Long playerId = 99L;
        var req = new PlayerRenameRequest();
        req.setNewName("Nuevo");

        // nombre libre
        when(playerRepository.findByName("Nuevo")).thenReturn(Mono.empty());
        // jugador NO existe
        when(playerRepository.findById(playerId)).thenReturn(Mono.empty());

        // when
        Mono<PlayerView> out = playerService.renamePlayer(playerId, req);

        // then
        StepVerifier.create(out)
                .expectErrorSatisfies(ex -> {
                    // ajusta al tipo REAL que lanza tu servicio
                    assertTrue(ex instanceof PlayerNotFound);
                    // opcional: comprobar el mensaje
                    // assertTrue(ex.getMessage().contains(playerId.toString()));
                })
                .verify();

        verify(playerRepository).findByName("Nuevo");
        verify(playerRepository).findById(playerId);
        verifyNoMoreInteractions(playerRepository, playerMapper); // no se guarda ni se mapea
    }

    @Test
    void renamePlayer_fails_whenNewNameAlreadyExists() {
        // given
        Long playerId = 3L;
        PlayerRenameRequest req = new PlayerRenameRequest();
        req.setNewName("Ana");

        when(playerRepository.findByName("Ana"))
                .thenReturn(Mono.just(Player.builder().id(1L).name("Ana").build()));

        // when
        Mono<PlayerView> out = playerService.renamePlayer(playerId, req);

        // then
        StepVerifier.create(out)
                .expectErrorSatisfies(ex -> assertTrue(ex instanceof IllegalStateException))
                .verify();

        verify(playerRepository).findByName("Ana");
        verifyNoMoreInteractions(playerRepository, playerMapper);
    }
}