package cat.itacademy.Blackjack.error;

public class InvalidAction extends RuntimeException {
    public InvalidAction(String action) {
        super("Invalid action: " + action);
    }
}
