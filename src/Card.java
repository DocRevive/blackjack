import java.util.Arrays;

/**
 * Record that represents individual cards of a 52-card deck.
 *
 * Suits:
 *   0: Hearts
 *   1: Diamonds
 *   2: Clubs
 *   3: Spades
 *
 * @param  rank         one of the 13 ranks (ace, king, ...)
 * @param  suit         0-3 index for the suit in this order:
 *                      hearts, diamonds, clubs, spades
 */
public record Card(String rank, int suit)
{
    /*
     *
     */

    /**
     * Constructor for a card
     */
    public Card {
        String[] validRanks = new String[]
                {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        if (!Arrays.asList(validRanks).contains(rank)) {
            throw new IllegalArgumentException("Invalid rank: " + rank);
        }

        if (suit < 0 || suit > 3) {
            throw new IllegalArgumentException("Suit is out of bounds, must be a number 0-3 for hearts, diamonds, clubs, and spades, respectively");
        }

    }

    /*
     * Accessors
     */

    /**
     * Returns a String representation of the card with
     * red/black ANSI color escape sequences, the rank, and
     * the symbol of the suit. Hearts &amp; diamonds are red,
     * clubs &amp; spades are black.
     *
     * @return the rank and the suit highlighted in red or black
     */
    @Override
    public String toString()
    {
        /*
         * Suits 0 and 1 are hearts and diamonds, so the red cards.
         * 2 and 3 are black. "\u001B[0m" resets the current coloring
         * attribute set by 41 (red bg) or 40 (black bg).
         */
        return (suit < 2 ? "\u001B[41m" : "\u001B[40m")
                + rank + "♥♦♣♠".charAt(suit) + "\u001B[0m";
    }

    /**
     * Returns the numerical value of the card. 2-8 are just
     * those numbers, face cards all equal 10, and aces are
     * initially 11.
     *
     * @return numerical value of the card's rank
     */
    public int getValue()
    {
        try {
            return Integer.parseInt(rank);
        } catch (final NumberFormatException e) {
            if (rank.equals("A")) {
                return 11;
            } else {
                return 10;
            }
        }
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
}
