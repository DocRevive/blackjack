/**
 * Keeps track of the player's bets and bankroll.
 * Blackjack pays 3 to 2
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Bankroll {
    private int funds;
    private int currentBet = -1;

    /**
     * Constructor for Bankroll
     *
     * @param  funds  initial bankroll
     */
    public Bankroll(int funds)
    {
        this.funds = funds;
    }

    /*
     * Accessors
     */

    /**
     * Getter for 'funds'
     *
     * @return current balance
     */
    public int getFunds()
    {
        return funds;
    }

    /**
     * Getter for 'currentBet'
     *
     * @return current bet
     */
    public int getCurrentBet()
    {
        return currentBet;
    }

    /*
     * Mutators
     */

    /**
     * Gives funds to player if they have won/tied.
     *
     * @param  winType  1 = normal win, 2 = blackjack, 3 = tie
     */
    public void receiveBet(int winType)
    {
        switch (winType) {
            case 1:
                // Profit is original bet, also returns original bet
                funds += 2 * currentBet;
                break;
            case 2:
                // Blackjack pays 3:2, also returns original bet
                funds += 2.5 * currentBet;
                break;
            case 3:
                // Tied, so bet is returned
                funds += currentBet;
                break;
        }

        currentBet = -1;
    }

    /**
     * Set the bet for the current round
     *
     * @param  bet  numerical bet less than available funds
     */
    public void setBet(int bet)
    {
        if (funds < bet) {
            throw new IllegalArgumentException("Can't bet more than the current funds.");
        } else {
            funds -= bet;
            currentBet = bet;
        }
    }
}
