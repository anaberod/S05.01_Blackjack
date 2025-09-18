package cat.itacademy.Blackjack;

import cat.itacademy.Blackjack.application.logic.GameLogic;
import cat.itacademy.Blackjack.model.Card;
import cat.itacademy.Blackjack.model.Hand;
import cat.itacademy.Blackjack.model.enums.Rank;
import cat.itacademy.Blackjack.model.enums.Suit;
import cat.itacademy.Blackjack.model.enums.Winner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    private final GameLogic logic = new GameLogic();

    @Test
    void aceCountsAs11_or1_toBestScore() {

        Hand h1 = new Hand(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.HEARTS, Rank.SIX)
        )));
        assertEquals(17, logic.handValue(h1));


        Hand h2 = new Hand(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.NINE)
        )));
        assertEquals(19, logic.handValue(h2));
    }

    @Test
    void naturalBlackjack_twoCards_21_true() {
        Hand bj = new Hand(new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.TEN) // (10/J/Q/K)
        )));
        assertTrue(logic.isBlackjack(bj));
    }

    @Test
    void bust_whenOver21() {
        Hand bust = new Hand(new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.NINE),
                new Card(Suit.HEARTS, Rank.FIVE)
        )));
        assertTrue(logic.isBust(bust));
    }

    @Test
    void dealerStandsOnSoft17() {

        Hand dealer = new Hand(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.DIAMONDS, Rank.SIX)
        )));

        List<Card> deck = new ArrayList<>(List.of(new Card(Suit.CLUBS, Rank.TWO))); // 17 -> 19

        logic.playDealer(dealer, deck);


        assertEquals(17, logic.handValue(dealer));
        assertEquals(2, dealer.getCards().size());
        assertEquals(1, deck.size());
    }

    @Test
    void evaluateWinner_playerBeatsDealer() {
        Hand player = new Hand(new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.QUEEN) // 20
        )));
        Hand dealer = new Hand(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.NINE) // 18
        )));
        assertEquals(Winner.PLAYER, logic.evaluateWinner(player, dealer));
    }

    @Test
    void evaluateNaturalBlackjack_allCases() {
        Hand pBJ = new Hand(new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.KING)
        )));
        Hand dBJ = new Hand(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.DIAMONDS, Rank.QUEEN)
        )));
        Hand none = new Hand(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.EIGHT),
                new Card(Suit.DIAMONDS, Rank.NINE)
        )));


        assertEquals(Winner.DRAW, logic.evaluateNaturalBlackjack(pBJ, dBJ));

        assertEquals(Winner.PLAYER, logic.evaluateNaturalBlackjack(pBJ, none));

        assertEquals(Winner.DEALER, logic.evaluateNaturalBlackjack(none, dBJ));

        assertNull(logic.evaluateNaturalBlackjack(none, none));
    }
}