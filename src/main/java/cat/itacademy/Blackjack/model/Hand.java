package cat.itacademy.Blackjack.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Hand {

    private List<Card> cards = new ArrayList<>();

    public Hand() {
    }

    public Hand(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    @Override
    public String toString() {
        return "Hand{" +
                "cards=" + cards +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Hand)) return false;
        Hand hand = (Hand) o;
        return Objects.equals(cards, hand.cards);
    }

    @Override
    public int hashCode() {

        return Objects.hash(cards);
    }
}
