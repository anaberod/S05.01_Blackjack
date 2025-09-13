package cat.itacademy.Blackjack.application.service;

import cat.itacademy.Blackjack.application.logic.GameLogic;
import cat.itacademy.Blackjack.application.mapper.GameMapper;
import cat.itacademy.Blackjack.dto.CreateGameRequest;
import cat.itacademy.Blackjack.dto.GameResponse;
import cat.itacademy.Blackjack.dto.PlayRequest;
import cat.itacademy.Blackjack.error.GameAlreadyFinished;
import cat.itacademy.Blackjack.error.GameNotFound;
import cat.itacademy.Blackjack.error.InvalidAction;
import cat.itacademy.Blackjack.error.PlayerNotFound;
import cat.itacademy.Blackjack.model.*;
import cat.itacademy.Blackjack.model.enums.Action;
import cat.itacademy.Blackjack.model.enums.GameStatus;
import cat.itacademy.Blackjack.model.enums.Winner;
import cat.itacademy.Blackjack.repository.GameRepository;
import cat.itacademy.Blackjack.repository.GameResultRepository;
import cat.itacademy.Blackjack.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;



@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;               // Mongo (partidas vivas)
    private final PlayerRepository playerRepository;           // MySQL R2DBC (validar player existe)
    private final GameResultRepository gameResultRepository;   // MySQL R2DBC (histórico)
    private final GameLogic gameLogic;                         // Reglas puras del Blackjack
    private final GameMapper gameMapper;                       // Game -> GameResponse

    // ========= CREAR / OBTENER / BORRAR PARTIDA =========

    public Mono<GameResponse> createGame(CreateGameRequest request) {
        Long playerId = request.getPlayerId();

        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.<Player>error(new PlayerNotFound(playerId)))
                .flatMap(player -> {
                    // Preparar baraja y manos
                    List<Card> deck = gameLogic.createShuffledDeck();
                    Hand playerHand = new Hand(new ArrayList<>());
                    Hand dealerHand = new Hand(new ArrayList<>());

                    gameLogic.drawTo(playerHand, deck, 2);
                    gameLogic.drawTo(dealerHand, deck, 2);

                    Game game = new Game();
                    game.setPlayerId(player.getId());
                    game.setDeck(deck);
                    game.setPlayerHand(playerHand);
                    game.setDealerHand(dealerHand);
                    game.setStatus(GameStatus.IN_PROGRESS);

                    // Blackjack natural al repartir
                    Winner natural = gameLogic.evaluateNaturalBlackjack(playerHand, dealerHand);
                    if (natural != null) {
                        game.setStatus(GameStatus.FINISHED);
                        game.setWinner(natural);
                    }

                    return gameRepository.save(game)
                            .flatMap(saved -> saved.getStatus() == GameStatus.FINISHED
                                    ? persistResult(saved).thenReturn(saved)
                                    : Mono.just(saved))
                            .map(gameMapper::toResponse);
                });
    }

    public Mono<GameResponse> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFound(gameId)))
                .map(gameMapper::toResponse);
    }

    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFound(gameId)))
                .flatMap(existing -> gameRepository.deleteById(gameId));
    }




    // ====================== JUGAR ========================

    /** Endpoint unificado del enunciado: /game/{id}/play con { action: HIT|STAND } */
    public Mono<GameResponse> play(String gameId, PlayRequest request) {
        Action action = request.getAction();

        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFound(gameId))) // 👉 personalizado
                .flatMap(game -> {
                    if (game.getStatus() == GameStatus.FINISHED) {
                        return Mono.error(new GameAlreadyFinished(gameId)); // 👉 personalizado
                    }
                    return switch (action) {
                        case HIT -> playHit(game);
                        case STAND -> playStand(game);
                        // Si algún día añadimos acción inválida (ej. "DOUBLE"), lanzamos InvalidAction
                        default -> Mono.error(new InvalidAction(action.name()));
                    };
                })
                .map(gameMapper::toResponse);
    }


    // ------------------ Acciones internas ------------------

    private Mono<Game> playHit(Game game) {
        // Roba 1 carta al jugador
        gameLogic.drawTo(game.getPlayerHand(), game.getDeck(), 1);

        // Si se pasa, termina y gana dealer
        if (gameLogic.isBust(game.getPlayerHand())) {
            game.setStatus(GameStatus.FINISHED);
            game.setWinner(Winner.DEALER);
        }

        return gameRepository.save(game)
                .flatMap(this::maybePersistIfFinished);
    }

    private Mono<Game> playStand(Game game) {
        // Juega el dealer: se planta en 17 (incluye soft 17)
        gameLogic.playDealer(game.getDealerHand(), game.getDeck());

        // Decidir ganador y cerrar
        Winner winner = gameLogic.evaluateWinner(game.getPlayerHand(), game.getDealerHand());
        game.setStatus(GameStatus.FINISHED);
        game.setWinner(winner);

        return gameRepository.save(game)
                .flatMap(this::persistResult)
                .then(gameRepository.findById(game.getId())); // devolver la versión guardada
    }

    // ------------------ Persistencia de histórico ------------------

    private Mono<Game> maybePersistIfFinished(Game game) {
        if (game.getStatus() == GameStatus.FINISHED) {
            return persistResult(game).thenReturn(game);
        }
        return Mono.just(game);
    }

    private Mono<Void> persistResult(Game game) {
        GameResult result = GameResult.builder()
                .playerId(game.getPlayerId())
                .gameId(game.getId())
                .winner(game.getWinner())
                .playerScore(gameLogic.handValue(game.getPlayerHand()))
                .dealerScore(gameLogic.handValue(game.getDealerHand()))
                .build();
        return gameResultRepository.save(result).then();
    }
}
