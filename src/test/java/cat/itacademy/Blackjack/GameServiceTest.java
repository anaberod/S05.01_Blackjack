package cat.itacademy.Blackjack;

import cat.itacademy.Blackjack.application.logic.GameLogic;
import cat.itacademy.Blackjack.application.mapper.GameMapper;
import cat.itacademy.Blackjack.application.service.GameService;
import cat.itacademy.Blackjack.dto.CreateGameRequest;
import cat.itacademy.Blackjack.dto.GameResponse;
import cat.itacademy.Blackjack.dto.PlayRequest;
import cat.itacademy.Blackjack.model.*;
import cat.itacademy.Blackjack.model.enums.*;
import cat.itacademy.Blackjack.repository.mongo.GameRepository;
import cat.itacademy.Blackjack.repository.sql.GameResultRepository;
import cat.itacademy.Blackjack.repository.sql.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock GameRepository gameRepository;
    @Mock PlayerRepository playerRepository;
    @Mock GameResultRepository gameResultRepository;

    // Lógica real; podremos forzar barajas con doReturn(...)
    @Spy GameLogic gameLogic = new GameLogic();

    @Mock GameMapper gameMapper;

    @InjectMocks GameService gameService;

    // -------------------- STUBS COMUNES --------------------
    @BeforeEach
    void commonStubs() {
        // puede no usarse si el test termina con error antes de guardar
        lenient().when(gameResultRepository.save(any())).thenReturn(Mono.empty());

        // puede no usarse en tests que nunca guardan
        lenient().when(gameRepository.save(any(Game.class)))
                .thenAnswer(inv -> {
                    Game g = inv.getArgument(0, Game.class);
                    if (g.getId() == null) g.setId("g-auto");
                    return Mono.just(g);
                });

        // puede no usarse si el flujo acaba en error
        lenient().when(gameMapper.toResponse(any(Game.class)))
                .thenAnswer(inv -> {
                    Game g = inv.getArgument(0, Game.class);
                    return GameResponse.builder()
                            .gameId(g.getId())
                            .playerId(g.getPlayerId())
                            .status(g.getStatus())
                            .winner(g.getWinner())
                            .playerHand(null)
                            .dealerHand(null)
                            .build();
                });
    }


    // ======== TEST 1: createGame sin blackjack natural -> IN_PROGRESS ========
    @Test
    void createGame_returnsInProgress_whenNoNaturalBlackjack() {
        Long playerId = Long.valueOf(1L);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(Player.builder().id(playerId).name("Ana").build()));

        // Mazo determinista (drawTo roba del FINAL de la lista)
        List<Card> deck = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TWO),
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.HEARTS, Rank.FOUR),
                new Card(Suit.SPADES, Rank.FIVE)
        ));
        doReturn(deck).when(gameLogic).createShuffledDeck();

        CreateGameRequest req = new CreateGameRequest();
        req.setPlayerId(playerId);

        Mono<GameResponse> result = gameService.createGame(req);

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getStatus() == GameStatus.IN_PROGRESS
                        && playerId.equals(r.getPlayerId()))
                .verifyComplete();

        verify(playerRepository).findById(playerId);
        verify(gameRepository).save(any(Game.class));
        // no se persiste GameResult aún
        verifyNoMoreInteractions(gameResultRepository);
    }

    // ======== TEST 2: HIT -> jugador se pasa -> FINISHED y gana DEALER ========
    @Test
    void playHit_whenPlayerBusts_finishesAndDealerWins() {
        String gameId = "gX";
        Long playerId = Long.valueOf(1L);

        Hand player = new Hand(new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.SPADES, Rank.SEVEN)
        )));
        Hand dealer = new Hand(new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.SIX)
        )));
        // Última carta del deck (la que se roba) = SIX -> 10+7+6 = 23
        List<Card> deck = new ArrayList<>(List.of(new Card(Suit.HEARTS, Rank.SIX)));

        Game game = Game.builder()
                .id(gameId)
                .playerId(playerId)
                .playerHand(player)
                .dealerHand(dealer)
                .deck(deck)
                .status(GameStatus.IN_PROGRESS)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));

        PlayRequest hit = new PlayRequest();
        hit.setAction(Action.HIT);

        Mono<GameResponse> result = gameService.play(gameId, hit);

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getStatus() == GameStatus.FINISHED
                        && r.getWinner() == Winner.DEALER)
                .verifyComplete();

        verify(gameRepository).findById(gameId);
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(gameResultRepository, atLeastOnce()).save(any());
    }

    // ======== TEST 3: si la partida ya está FINISHED -> error GameAlreadyFinished ========
    @Test
    void play_whenGameAlreadyFinished_emitsGameAlreadyFinished() {
        String gameId = "gZ";

        Game finished = Game.builder()
                .id(gameId)
                .playerId(Long.valueOf(1L))
                .playerHand(new Hand(new ArrayList<>()))
                .dealerHand(new Hand(new ArrayList<>()))
                .deck(new ArrayList<>())
                .status(GameStatus.FINISHED)
                .winner(Winner.DEALER)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(finished));

        PlayRequest anyMove = new PlayRequest();
        anyMove.setAction(Action.HIT);

        Mono<GameResponse> result = gameService.play(gameId, anyMove);

        StepVerifier.create(result)
                .expectError(cat.itacademy.Blackjack.error.GameAlreadyFinished.class)
                .verify();

        verify(gameRepository).findById(gameId);
        verify(gameRepository, never()).save(any(Game.class));
        verifyNoInteractions(gameResultRepository);
    }

    // ======== TEST 4: createGame con BLACKJACK natural -> FINISHED & gana PLAYER ========
    @Test
    void createGame_finishesWhenNaturalBlackjack_playerWinsImmediately() {
        Long playerId = Long.valueOf(1L);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(Player.builder().id(playerId).name("Ana").build()));

        // drawTo roba del FINAL -> el orden hace que al repartir el jugador tenga BLACKJACK
        List<Card> deck = new ArrayList<>(List.of(
                // “Relleno” del dealer (no BLACKJACK)
                new Card(Suit.CLUBS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.FOUR),
                // Cartas del jugador (desde el final): AS + DIEZ
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.SPADES, Rank.ACE)
        ));
        doReturn(deck).when(gameLogic).createShuffledDeck();

        CreateGameRequest req = new CreateGameRequest();
        req.setPlayerId(playerId);

        Mono<GameResponse> result = gameService.createGame(req);

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getStatus() == GameStatus.FINISHED
                        && r.getWinner() == Winner.PLAYER)
                .verifyComplete();

        // Al cerrar, se persiste resultado
        verify(gameResultRepository, atLeastOnce()).save(any());
    }

    // ======== TEST 5: STAND -> juega dealer y pierde -> gana PLAYER ========
    @Test
    void playStand_playerWins_dealerPlaysAndLoses() {
        String gameId = "gST";
        Long playerId = Long.valueOf(1L);

        // Mano fuerte del jugador (20)
        Hand player = new Hand(new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.QUEEN)
        )));
        // Dealer con 11 inicial
        Hand dealer = new Hand(new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.SPADES, Rank.FIVE)
        )));
        // El dealer roba un DIEZ y se pasa
        List<Card> deck = new ArrayList<>(List.of(new Card(Suit.CLUBS, Rank.TEN)));

        // Juego inicial
        Game game = Game.builder()
                .id(gameId)
                .playerId(playerId)
                .playerHand(player)
                .dealerHand(dealer)
                .deck(deck)
                .status(GameStatus.IN_PROGRESS)
                .build();

        // Juego final esperado tras aplicar STAND
        Game gameFinished = game.toBuilder()
                .status(GameStatus.FINISHED)
                .winner(Winner.PLAYER)
                .build();

        // Stubs: findById -> primero devuelve el juego en curso, luego el ya finalizado
        when(gameRepository.findById(gameId))
                .thenReturn(Mono.just(game), Mono.just(gameFinished));

        // save devuelve la versión finalizada
        when(gameRepository.save(any(Game.class)))
                .thenReturn(Mono.just(gameFinished));

        // guardado histórico sin efecto
        when(gameResultRepository.save(any())).thenReturn(Mono.empty());

        PlayRequest stand = new PlayRequest();
        stand.setAction(Action.STAND);

        // when
        Mono<GameResponse> result = gameService.play(gameId, stand);

        // then
        StepVerifier.create(result)
                .expectNextMatches(r -> r.getStatus() == GameStatus.FINISHED
                        && r.getWinner() == Winner.PLAYER)
                .verifyComplete();

        verify(gameRepository, atLeastOnce()).findById(gameId);
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(gameResultRepository, atLeastOnce()).save(any());
    }


}
