package cat.itacademy.Blackjack.error;

public class GameAlreadyFinished extends RuntimeException {
    public GameAlreadyFinished(String gameId) {
        super("Game already finished: " + gameId);
    }
}
