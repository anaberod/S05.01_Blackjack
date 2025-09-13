package cat.itacademy.Blackjack.error;

public class PlayerNotFound extends RuntimeException {
    public PlayerNotFound(Long playerId) {
        super("Player not found: " + playerId);
    }
}
