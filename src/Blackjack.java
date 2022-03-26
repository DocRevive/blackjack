import java.util.ArrayList;

/**
 * The game of Blackjack in Java.
 * Rules: The dealer must draw to 16 and stand on all 17s,
 * and the dealer peeks for blackjack.
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Blackjack
{
    private final ArrayList<Card> SHOE = new ArrayList<>();
    private final ArrayList<Card> PLAYER_HAND = new ArrayList<>();
    private final ArrayList<Card> DEALER_HAND = new ArrayList<>();
    private final int numOfDecks;
    private boolean isInRound = false;

    /**
     * Constructor for a game of Blackjack
     *
     * @param  numOfDecks  whole number of 52-card decks to use
     */
    public Blackjack(int numOfDecks)
    {
        this.numOfDecks = numOfDecks;
    }

    /*
     * Accessors
     */

    /**
     * Gets whether a round is ongoing
     *
     * @return whether the round is in progress
     */
    public boolean isInRound()
    {
        return isInRound;
    }

    /**
     * Determines the score of a hand
     *
     * @param  hand  hand to analyze
     * @return       positive number for hard hands, negative number
     *               for soft hands (score is the absolute value),
     *               0 is an empty hand, and a number > 21 was the
     *               score when it busted
     */
    public int handScore(ArrayList<Card> hand)
    {
        int sum = 0;
        boolean isSoft = false;
        /*
         * In Blackjack, aces can count as either 11 (default) or 1,
         * depending on whether the score has exceeded 21. If there
         * remains an ace with a value of 11 in the hand, the hand is
         * considered "soft"; if a card that would cause the score to
         * exceed 21 is drawn, the 11-ace can change to 1 to prevent
         * the bust. If there are no aces or each one has a value of 1,
         * the hand is "hard".
         *
         * Soft -> flexible, hard -> fixed
         */

        for (Card card : hand) {
            int nextAddition = card.getValue();

            if (nextAddition == 11) {
                // If 11 causes sum to go over 21
                if (sum + 11 > 21) {
                    /*
                     * Try 1. Test it on the next "soft-evaluation"
                     * (score may already be 21, so 21+1->bust)
                     */
                    nextAddition = 1;
                } else {
                    // If not, add 11 to sum and set. Skip the soft-evaluation
                    sum += 11;
                    isSoft = true;
                    continue;
                }
            }

            /* Ensure that any soft sum that exceeds 21 is justly scored */
            // If it will exceed 21
            if (sum + nextAddition > 21) {
                // If it is soft
                if (isSoft) {
                    /*
                     * Make the ace's value 1 and add the current card.
                     * 10 is the max. value of this card, so we don't have
                     * to check if it exceeds again; 10-10=0 change (same score)
                     */
                    sum -= 10 - nextAddition;
                    // No longer soft. Only one soft ace can exist at a time (11*2>21).
                    isSoft = false;
                } else {
                    // If it is hard (and exceeds 21), it has busted
                    return sum + nextAddition;
                }
            } else {
                // If it doesn't exceed, add nextAddition normally
                sum += nextAddition;
            }
        }

        return isSoft ? -sum : sum;
    }

    /**
     * String status of the round - current hands, who has won/lost
     * and how, or if it is still the player's turn, and the number
     * of cards left in the shoe.
     *
     * @return information about the current round
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        int playerScore = handScore(PLAYER_HAND);
        int dealerScore = handScore(DEALER_HAND);

        result.append("Player: ").append(playerScore < 0 ? "Soft " : "")
                .append(Math.abs(playerScore)).append("\n");
        for (Card card : PLAYER_HAND) {
            result.append(card).append(" ");
        }

        result.append("\n\nDealer: ");
        if (isInRound) {
            Card dealerFirst = DEALER_HAND.get(0);
            // Blank face-down card
            result.append(dealerFirst.getValue()).append("\n").append(dealerFirst)
                    .append(" \u001B[40m--\u001B[0m");
        } else {
            result.append(dealerScore < 0 ? "Soft " : "").append(Math.abs(dealerScore))
                    .append("\n");
            for (Card card : DEALER_HAND) {
                result.append(card).append(" ");
            }
        }

        result.append("\n\nCards remaining: ").append(SHOE.size());

        if (!isInRound) result.append("\n").append(determineResultReason());

        return result.toString();
    }

    /**
     * Determines whether the player won, lost, or tied with the
     * dealer. 0 = lose, 1 = win, 2 = blackjack, 3 = tie
     *
     * @return round result/winner
     */
    public int determineRoundResult()
    {
        int playerScore = Math.abs(handScore(PLAYER_HAND));
        int dealerScore = Math.abs(handScore(DEALER_HAND));

        if (playerScore == 0 || dealerScore == 0) return 0;

        if (playerScore == dealerScore) {
            return 3;
        } else if (playerScore == 21) {
            return 2;
        } else if (playerScore > 21 || (dealerScore <= 21 && dealerScore > playerScore)) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Determines why the player won, lost, or tied with the dealer.
     *
     * @return result reason
     */
    public String determineResultReason()
    {
        int playerScore = Math.abs(handScore(PLAYER_HAND));
        int dealerScore = Math.abs(handScore(DEALER_HAND));

        if (playerScore == dealerScore) {
            return "Push! You tied.";
        } else if (playerScore == 21) {
            return "Blackjack! You won!";
        } else if (dealerScore == 21) {
            return "Dealer blackjack! You lost.";
        } else if (playerScore > 21) {
            return "Bust! You lost.";
        } else if (dealerScore > 21) {
            return "Dealer bust! You won!";
        } else if (dealerScore > playerScore) {
            return "The dealer won.";
        } else {
            return "You won!";
        }
    }

    /*
     * Mutators
     */

    /**
     * Fills the shoe ("draw pile") with the appropriate cards of a
     * 52-card deck for as many decks as there are
     */
    public void fillShoe()
    {
        String[] ranks = new String[]
                {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        // For each rank
        for (String rank : ranks) {
            /*
             * Repeat loop of 4 (number of each rank in a 52-card deck)
             * for how many decks there are
             */
            for (int i = 0; i < numOfDecks; i++) {
                // Loop through suits
                for (int j = 0; j < 4; j++) {
                    SHOE.add(new Card(rank, j));
                }
            }
        }
    }

    /**
     * Resets hands and starts a new round.
     */
    public void deal()
    {
        PLAYER_HAND.clear();
        DEALER_HAND.clear();
        isInRound = true;

        /*
         * Player is dealt to first, then the deal alternates between
         * dealer and player
         */
        draw(PLAYER_HAND);
        draw(DEALER_HAND);
        draw(PLAYER_HAND);
        draw(DEALER_HAND);
    }

    /**
     * Draws one card, removes it from the shoe, adds it to the
     * provided ArrayList hand. Then, changes isInRound if the
     * round is over due to a bust or a blackjack.
     * If there are no cards left in the shoe to draw, it refills it.
     *
     * @param  hand  hand to transfer the card to
     */
    public void draw(ArrayList<Card> hand)
    {
        int cardIndex = (int)
                (Math.random() * SHOE.size()); // Get a random card
        hand.add(SHOE.get(cardIndex));         // Add it to the hand
        SHOE.remove(cardIndex);                // Remove it from the shoe

        if (SHOE.size() == 0) {
            fillShoe();
        }

        if (Math.abs(handScore(hand)) >= 21) {
            isInRound = false;
        }
    }

    /**
     * If a game is ongoing and the player hasn't busted, draw
     * a card and add it to the hand.
     */
    public void hit()
    {
        int playerScore = handScore(PLAYER_HAND);

        // If hand isn't empty (game ongoing) and player hasn't busted
        if (playerScore != 0 && playerScore <= 21) {
            draw(PLAYER_HAND);
        }
    }

    /**
     * Ends the player's turn and goes through the dealer's. The
     * dealer will automatically draw cards, continuing to do
     * so until the score exceeds a hard/soft 17 (rules).
     */
    public void stand()
    {
        int dealerScore = handScore(DEALER_HAND);

        if (dealerScore == 0) return;  // Empty hand (hasn't dealed)

        /*
         * Dealers are bound by strict rules. They cannot hit on or
         * after hard 17, and in some games (such as this one) not on
         * soft 17.
         */
        while (Math.abs(dealerScore) < 17) {
            draw(DEALER_HAND);
            dealerScore = handScore(DEALER_HAND);
        }

        isInRound = false;
    }
}
