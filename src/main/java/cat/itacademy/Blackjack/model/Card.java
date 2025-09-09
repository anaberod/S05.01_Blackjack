package cat.itacademy.Blackjack.model;

import cat.itacademy.Blackjack.model.enums.Rank;
import cat.itacademy.Blackjack.model.enums.Suit;

import java.util.Objects;

public class Card {

    private Suit suit;   // Palo (♠, ♥, ♦, ♣)
    private Rank rank;   // Valor (2...10, J, Q, K, A)

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    @Override
    public boolean equals(Object o) { // Dos cartas se consideran iguales si tienen el mismo palo y el mismo valor
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        // Genera un código único en base a palo y valor.
        // Sirve para que en colecciones como HashSet o HashMap no se guarden duplicadas.
        return Objects.hash(suit, rank);
    }
}
