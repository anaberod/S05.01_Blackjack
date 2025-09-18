package cat.itacademy.Blackjack.application.logic;

import cat.itacademy.Blackjack.model.Card;
import cat.itacademy.Blackjack.model.Hand;
import cat.itacademy.Blackjack.model.enums.Rank;
import cat.itacademy.Blackjack.model.enums.Winner;
import org.springframework.stereotype.Component;
import cat.itacademy.Blackjack.model.enums.Suit;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GameLogic {

    private final SecureRandom rng = new SecureRandom();


    public List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>(52);
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                deck.add(new Card(s, r));
            }
        }
        Collections.shuffle(deck, rng);
        return deck;
    }


    public void drawTo(Hand hand, List<Card> deck, int n) {
        for (int i = 0; i < n; i++) {
            if (deck.isEmpty()) throw new IllegalStateException("Deck exhausted");
            hand.getCards().add(deck.remove(deck.size() - 1));
        }
    }


    public int handValue(Hand hand) {
        int total = 0;
        int aces = 0;
        for (Card c : hand.getCards()) {
            total += c.getRank().getValue(); // A=1, J/Q/K=10
            if (c.getRank() == Rank.ACE) aces++;
        }
        // Elevar As de 1 a 11 mientras no nos pasemos
        while (aces > 0 && total + 10 <= 21) {
            total += 10;
            aces--;
        }
        return total;
    }


    public boolean isBlackjack(Hand hand) {
        return hand.getCards().size() == 2 && handValue(hand) == 21;
    }


    public boolean isBust(Hand hand) {
        return handValue(hand) > 21;
    }


    public void playDealer(Hand dealer, List<Card> deck) {
        while (true) {
            int value = handValue(dealer);
            if (value >= 17) break; // se planta en 17 duro o blando
            drawTo(dealer, deck, 1);
        }
    }


    public Winner evaluateWinner(Hand player, Hand dealer) {
        int p = handValue(player);
        int d = handValue(dealer);

        if (p > 21) return Winner.DEALER;
        if (d > 21) return Winner.PLAYER;
        if (p > d)   return Winner.PLAYER;
        if (p < d)   return Winner.DEALER;
        return Winner.DRAW;
    }


    public Winner evaluateNaturalBlackjack(Hand player, Hand dealer) {
        boolean pBJ = isBlackjack(player);
        boolean dBJ = isBlackjack(dealer);
        if (pBJ && dBJ) return Winner.DRAW;
        if (pBJ)         return Winner.PLAYER;
        if (dBJ)         return Winner.DEALER;
        return null;
    }
}
