import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Card> cards;
    private double betMultiplier = 1;

    /**
     * Default constructor
     */
    public Hand() {
        this.cards = new ArrayList<>();
    }

    /**
     * Overloaded constructor
     *
     * @param  betMultiplier  how much of the original bet that this
     *                          hand is worth. double = *2
     * @param  firstCard        one initial card in the hand
     */
    public Hand(double betMultiplier, Card firstCard)
    {
        this.betMultiplier = betMultiplier;
        this.cards = new ArrayList<>();
        this.cards.add(firstCard);
    }

    /**
     * Copy constructor
     *
     * @param  hand  Hand object to create a copy of
     */
    public Hand(Hand hand)
    {
        this.betMultiplier = hand.getBetMultiplier();
        this.cards = new ArrayList<>(hand.getCards());
    }

    /**
     * Determines the score of a hand.
     *
     * @param  hand       hand to analyze
     * @param  scoreOnly  whether to disregard the distinction between
     *                    soft & hard hands and return the score only
     * @return            positive number for hard hands, negative number
     *                    for soft hands (score is the absolute value),
     *                    0 is an empty hand, and a number > 21 is bust
     */
    public static int handScore(Hand hand, boolean scoreOnly)
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
         * Soft = flexible, hard = fixed
         */

        for (Card card : hand.getCards()) {
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

        return scoreOnly ? sum : (isSoft ? -sum : sum);
    }

    /*
     * Accessors
     */

    /**
     * Gets the list of cards.
     *
     * @return List of cards in the hand
     */
    public List<Card> getCards()
    {
        return cards;
    }

    /**
     * Gets the current multiplier of the original bet for this hand
     *
     * @return how much of the original bet that this
     *         hand is worth. double = *2
     */
    public double getBetMultiplier()
    {
        return betMultiplier;
    }

    /**
     * Counts the number of cards in the hand.
     *
     * @return number of cards
     */
    public int numberOfCards()
    {
        return cards.size();
    }

    /**
     * Determines whether the hand is a blackjack, or has two cards
     * whose values add up to 21 (ace & 10 or face card).
     *
     * @return whether hand is blackjack
     */
    public boolean isBlackjack()
    {
        return cards.size() == 2 && Hand.handScore(this, true) == 21;
    }

    /**
     * Determines whether the hand can be split. It must
     * have only two cards, both of the same rank.
     *
     * @return whether the hand is splittable
     */
    public boolean isSplittable()
    {
        if (cards.size() == 2) {
            return cards.get(0).getRank().equals(cards.get(1).getRank());
        }

        return false;
    }

    /**
     * Returns a String with all the cards in the hand.
     *
     * @return String representation of the hand
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        int playerScore = Hand.handScore(this, false);

        if (isBlackjack()) {
            result.append("Blackjack\n");
        } else {
            result.append(playerScore < 0 ? "Soft " : "")
                    .append(Math.abs(playerScore)).append("\n");
        }

        for (Card card : cards) {
            result.append(card).append(" ");
        }

        return result.toString();
    }

    /**
     * Returns a String representation of the dealer's hand.
     *
     * The second card in the hand is face-down only if the hand has
     * two cards and is not a blackjack. This means the round is
     * still ongoing, so the dealer's second card should be face-down.
     *
     * @param  isRoundOngoing  whether the round is in progress
     * @return                 String representation of the hand
     */
    public String toString(boolean isRoundOngoing)
    {
        if (isRoundOngoing && cards.size() == 2) {
            /*
             * The shown score (cards.get(0)) is just the value of the first
             * card. It shouldn't reveal the value of the whole hand.
             *
             * The character sequences are the ANSI color escape sequences for
             * black & reset, respectively. The two dashes are a placeholder
             * for the actual rank and suit.
             */
            return cards.get(0).getValue() + "\n" + cards.get(0)
                    + " \u001B[40m--\u001B[0m";
        } else {
            // Round is over so everything can be shown
            return toString();
        }
    }

    /**
     * Determines whether the player won, lost, or tied with the
     * dealer. 0 = lose, 1 = win, 2 = blackjack, 3 = tie
     *
     * @param  dealerHand  the Hand of the dealer
     * @return            round result/winner
     */
    public int determineHandResult(Hand dealerHand)
    {
        int playerScore = Hand.handScore(this, true);
        int dealerScore = Hand.handScore(dealerHand, true);
        int dealerCardsSize = dealerHand.numberOfCards();

        if (playerScore == 0 || dealerScore == 0) return 0;

        if (playerScore > 21) {
            return 0;
        } else if (dealerScore > 21) {
            return 1;
        } else if (playerScore > dealerScore) {
            return isBlackjack() ? 2 : 1;
        } else if (dealerScore > playerScore) {
            return 0;
        } else if (dealerScore == 21) {
            // Both the player and the dealer have 21
            if (dealerCardsSize == cards.size()) {
                return 3;
            } else if (dealerCardsSize == 2) {
                // Only dealer has blackjack
                return 0;
            } else if (cards.size() == 2){
                // Only player has blackjack
                return 2;
            } else {
                // Both have 21
                return 3;
            }
        } else {
            // Have same non-21 score
            return 3;
        }
    }

    /**
     * Determines why the player won, lost, or tied with the dealer.
     *
     * @param  dealerHand  the Hand of the dealer
     * @return             result reason
     */
    public String determineResultReason(Hand dealerHand)
    {
        int playerScore = Hand.handScore(this, true);
        int dealerScore = Hand.handScore(dealerHand, true);
        int dealerCardsSize = dealerHand.numberOfCards();

        if (playerScore > 21) {
            return "Bust! You lost.";
        } else if (dealerScore > 21) {
            return "Dealer bust! You won!";
        } else if (playerScore > dealerScore) {
            return isBlackjack() ? "Blackjack! You won!" : "You won!";
        } else if (dealerScore > playerScore) {
            return dealerHand.isBlackjack() ? "Dealer blackjack! The dealer won." : "The dealer won.";
        } else if (dealerScore == 21) {
            // Both the player and the dealer have 21
            if (dealerCardsSize == cards.size()) {
                return "Push! You tied.";
            } else if (dealerCardsSize > cards.size()) {
                return "Dealer blackjack! You lost.";
            } else {
                return "Blackjack! You won!";
            }
        } else {
            // Have same non-21 score
            return "Push! You tied.";
        }
    }

    /*
     * Mutators
     */

    /**
     * Adds a card to the hand.
     *
     * @param  card  the Card to be added
     */
    public void addCard(Card card)
    {
        this.cards.add(card);
    }

    /**
     * Removes all cards from the hand.
     */
    public void clearCards()
    {
        this.cards.clear();
    }

    /**
     * Sets a new multiplier of the original bet for this hand.
     *
     * @param  betMultiplier  how much of the original bet that this
     *                          hand is worth. double = *2
     */
    public void setBetMultiplier(double betMultiplier)
    {
        this.betMultiplier = betMultiplier;
    }
}
