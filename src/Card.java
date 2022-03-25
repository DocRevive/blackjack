import java.util.Arrays;

/**
 * Card class that can represent individual cards in
 * a 52-card deck
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Card
{
    private final String rank;
    private final int suit;
    /*
     * Suits:
     * 0: Hearts
     * 1: Diamonds
     * 2: Clubs
     * 3: Spades
     */

    /**
     * Constructor for a card
     *
     * @param  rank         one of the 13 ranks (ace, king, ...)
     * @param  suit         0-3 index for the suit in this order:
     *                      hearts, diamonds, clubs, spades
     */
    public Card(String rank, int suit)
    {
        String[] validRanks = new String[]
                {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        if (!Arrays.asList(validRanks).contains(rank)) {
            throw new IllegalArgumentException("Invalid rank: " + rank);
        }

        if (suit < 0 || suit > 3) {
            throw new IllegalArgumentException("Suit is out of bounds, must be a number 0-3 for hearts, diamonds, clubs, and spades, respectively");
        }

        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Returns a String representation of the card with
     * red/black ANSI color escape sequences, the rank, and
     * the symbol of the suit. Hearts & diamonds are red,
     * clubs & spades are black.
     *
     * @return the rank and the suit highlighted in red or black
     */
    @Override
    public String toString()
    {
        /*
         * Suits 0 and 1 are hearts and diamonds, so the red cards.
         * 2 and 3 are black. \u001B[0m resets the current coloring
         * attribute set by 41 (red bg) or 40 (black bg).
         */
        return (suit < 2 ? "\u001B[41m" : "\u001B[40m")
                + rank + "♥♦♣♠".charAt(suit) + "\u001B[0m";
    }

    /**
     * Gets the rank of the card.
     *
     * @return one of the 13 ranks (ace, king, ...)
     */
    public String getRank()
    {
        return rank;
    }

    /**
     * Gets the suit index of the card.
     *
     * @return 0-3 number for the suit in this order:
     *         hearts, diamonds, clubs, spades
     */
    public int getSuit()
    {
        return suit;
    }
}
