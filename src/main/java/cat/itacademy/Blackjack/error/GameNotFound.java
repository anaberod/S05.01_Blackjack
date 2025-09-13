package cat.itacademy.Blackjack.error;

public class GameNotFound extends RuntimeException {
    public GameNotFound(String gameId) {
        super("Game not found: " + gameId);
    }
}
