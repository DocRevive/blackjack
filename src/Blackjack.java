import java.util.ArrayList;
import java.util.List;

/**
 * The game of Blackjack in Java.
 * Rules: The dealer must draw to 16 and stand on all 17s,
 * the dealer peeks for blackjack, and only 3 splits at most
 * are allowed.
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Blackjack
{
    private final List<Card> SHOE = new ArrayList<>();
    private final List<Hand> PLAYER_HANDS = new ArrayList<>();
    private final Hand DEALER_HAND = new Hand();
    private final int numOfDecks;
    private final int numOfHands = 2;
    private boolean isRoundOngoing = false;
    private int currentHandIndex = 0;

    /**
     * Constructor for a game of Blackjack
     *
     * @param  numOfDecks  whole number of 52-card decks to use
     */
    public Blackjack(int numOfDecks)
    {
        this.numOfDecks = numOfDecks;
        for (int i = 0; i < numOfHands; i++) {
            PLAYER_HANDS.add(new Hand(1));
        }
    }

    /*
     * Accessors
     */

    /**
     * Gets whether a round is ongoing
     *
     * @return whether the round is in progress
     */
    public boolean isRoundOngoing()
    {
        return isRoundOngoing;
    }

    /**
     * Gets the dealer's hand
     *
     * @return dealer's Hand object
     */
    public Hand getDealerHand()
    {
        return DEALER_HAND;
    }

    /**
     * Gets a list of the hands in the current round
     *
     * @return index of hand in PLAYER_HANDS
     */
    public List<Hand> getPlayerHands()
    {
        return PLAYER_HANDS;
    }

    /**
     * Gets the index of the hand the player is currently playing
     *
     * @return index of the player's hands
     */
    public int getCurrentHandIndex()
    {
        return currentHandIndex;
    }

    /**
     * Gets the hand the player is currently playing
     *
     * @return current player Hand
     */
    public Hand getCurrentHand()
    {
        return PLAYER_HANDS.get(currentHandIndex);
    }

    /**
     * String status of the round - current hands, which hands have
     * won/lost and how, or if it is still the player's turn, the
     * number of cards left in the shoe, and how many hands left to
     * play.
     *
     * @return information about the current round
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i <= currentHandIndex; i++) {
            result.append(toString(i)).append("\n\n");
        }

        result.append(getRoundMetadata());
        return result.toString();
    }

    /**
     * Returns the status of the hand - cards, scores, and results.
     *
     * @param  handIndex  which hand to view
     * @return            information about the current hand
     */
    public String toString(int handIndex)
    {
        String result = "";

        Hand currentHand = PLAYER_HANDS.get(handIndex);

        result += "[HAND " + (handIndex + 1)
                + "]\nPlayer: " + currentHand
                + "\n\nDealer: " + DEALER_HAND.toString(isRoundOngoing);

        if (!isRoundOngoing) {
            result += "\n\n" + currentHand.determineResultReason(DEALER_HAND);
        }

        return result;
    }

    /**
     * Returns metadata on the round - cards left in shoe, remaining
     * hands.
     *
     * @return information about the round
     */
    public String getRoundMetadata()
    {
        String result = "";

        if (isRoundOngoing) {
            result += "Unresolved hands: " +
                    (PLAYER_HANDS.size() - currentHandIndex) + "\n";
        }
        result += "Cards remaining: " + SHOE.size();

        return result;
    }

    /*
     * Mutators
     */

    /**
     * Fills the shoe (box of cards) with the appropriate cards
     * of a 52-card deck for as many decks as there are
     */
    public void fillShoe()
    {
        String[] ranks = new String[]
                {"2", "3", "4", "5", "6", "7",
                        "8", "9", "10", "J", "Q", "K", "A"};

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
        PLAYER_HANDS.clear();
        DEALER_HAND.clearCards();
        isRoundOngoing = true;

        for (int i = 0; i < numOfHands; i++) {
            PLAYER_HANDS.add(new Hand(1));
        }
        currentHandIndex = 0;

        /*
         * Players are dealt to first. Two cards are dealt to each
         * person.
         */
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < numOfHands; j++) {
                // Dealt one at a time to each hand
                draw(PLAYER_HANDS.get(j), false);
            }
            // Detect dealer blackjacks ("peek") to stop the round
            draw(DEALER_HAND, true);
        }

        /*
         * If the first hand is a blackjack, resolve and begin testing
         * for subsequent blackjacks
         */
        if (PLAYER_HANDS.get(0).isBlackjack()) resolveHand();
    }

    /**
     * Draws one card, removes it from the shoe, adds it to the
     * provided hand. If checkForResolution is true, it resolves the hand
     * if there is a bust or a blackjack. If there are no cards left
     * in the shoe to draw, it is refilled.
     *
     * @param  hand  hand to transfer the card to
     */
    public void draw(Hand hand, boolean checkForResolution)
    {
        int cardIndex = (int)
                (Math.random() * SHOE.size()); // Get a random card
        hand.addCard(SHOE.get(cardIndex));         // Add it to the hand
        SHOE.remove(cardIndex);                // Remove it from the shoe

        if (SHOE.size() == 0) {
            fillShoe();
        }

        if (checkForResolution && Hand.handScore(hand, true) >= 21) {
            resolveHand();
        }
    }

    /**
     * Move onto the next hand. If there are none left, ends the player's
     * turn and goes through the dealer's. The dealer will automatically
     * draw cards, continuing to do so until the score is at least a hard/soft
     * 17 (rules).
     */
    public void resolveHand()
    {
        int dealerScore = Hand.handScore(DEALER_HAND, true);

        // If no hands remain or dealer has blackjack
        if (PLAYER_HANDS.size() - 1 - currentHandIndex == 0) {
            // If player has not busted
            if (Hand.handScore(getCurrentHand(), true) <= 21) {
                /*
                 * Dealers are bound by strict rules. They cannot hit on or
                 * after hard 17, and in some games (such as this one) not on
                 * soft 17.
                 */
                while (dealerScore < 17) {
                    draw(DEALER_HAND, false);
                    dealerScore = Hand.handScore(DEALER_HAND, true);
                }
            }

            isRoundOngoing = false;
        } else if (DEALER_HAND.isBlackjack()) {
            isRoundOngoing = false;
            currentHandIndex = PLAYER_HANDS.size() - 1;
        } else {
            /*
             * Otherwise, move onto the next hand. Check the next hand for
             * blackjack - if it is, resolve again.
             */

            currentHandIndex++;
            if (getCurrentHand().isBlackjack()) {
                resolveHand();
            }
        }
    }

    /**
     * Ends the player's turn and resolves the hand, moving onto
     * the next or, if there are none, letting the dealer draw.
     */
    public void stand()
    {
        resolveHand();
    }

    /**
     * If a game is ongoing and the player hasn't busted, draw
     * a card and add it to the hand.
     */
    public void hit()
    {
        int playerScore = Hand.handScore(getCurrentHand(), false);

        // If hand isn't empty (game ongoing) and player hasn't busted
        if (playerScore != 0 && playerScore <= 21) {
            draw(getCurrentHand(), true);
        }
    }

    public void split()
    {

    }

    /**
     * Double down on a bet. This means doubling the bet on the hand,
     * hitting once, then standing. Can only happen when there are
     * two cards
     */
    public void doubleDown()
    {
        Hand currentHand = getCurrentHand();

        if (currentHand.numberOfCards() != 2) return;

        // Double the former multiplier
        currentHand.setMultiplierOfBet(2 * currentHand.getMultiplierOfBet());

        // Hit once and stand
        hit();
        stand();
    }
}
