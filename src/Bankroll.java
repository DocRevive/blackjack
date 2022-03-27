import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Keeps track of the player's bets and bankroll.
 * Blackjack pays 3 to 2
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Bankroll {
    private double funds;
    private double currentBet = 1;

    /**
     * Empty constructor
     */
    public Bankroll()
    {}

    /*
     * Accessors
     */

    /**
     * Gets the current balance
     *
     * @return current balance
     */
    public double getFunds()
    {
        return funds;
    }

    /**
     * Gets the current balance as a formatted String
     *
     * @return current balance
     */
    public String toString()
    {
        return formatMoney(funds);
    }

    /**
     * Gets the current bet per hand
     *
     * @return current bet
     */
    public double getCurrentBet()
    {
        return currentBet;
    }

    /**
     * Determines whether the player can pay the bet
     *
     * @param  numOfHands  number of hands to duplicate bet on
     * @return             whether player has sufficient funds
     */
    public boolean canPayBet(int numOfHands)
    {
        return numOfHands * currentBet <= funds;
    }

    /*
     * Mutators
     */

    /**
     * Sets the player's bankroll
     *
     * @param  amount  available funds
     */
    public void setFunds(double amount)
    {
        this.funds = amount;
    }

    /**
     * Gives funds to player if they have won/tied.
     *
     * @param  winType        1 = normal win, 2 = blackjack, 3 = tie
     * @param  betMultiplier  how much of the original bet this hand is worth
     */
    public void receiveBet(int winType, double betMultiplier)
    {
        double betChange = betMultiplier * currentBet;

        switch (winType) {
            case 1:
                // Profit is original bet, also returns original bet
                funds += 2 * betChange;
                break;
            case 2:
                // Blackjack pays 3:2, also returns original bet
                funds += 2.5 * betChange;
                break;
            case 3:
                // Tied, so bet is returned
                funds += betChange;
                break;
        }
    }

    /**
     * Set the bet for a new round.
     *
     * @param  bet         numerical bet * numOfHands <= available funds
     */
    public void setBet(double bet)
    {
        currentBet = bet;
    }

    /**
     * Pays an additional unit of currentBet
     */
    public void payBet()
    {
        funds -= currentBet;
    }

    /**
     * Pays a number of units of currentBet
     *
     * @param  numOfTimes  number of times to pay the bet
     */
    public void payBet(int numOfTimes)
    {
        funds -= numOfTimes * currentBet;
    }

    /*
     * Static methods
     */

    /**
     * Turn a double into a readable String currency value.
     *
     * @param  num  number to format
     * @return      readable string
     */
    public static String formatMoney(double num)
    {
        NumberFormat commaGroupFormat = NumberFormat.getInstance();
        commaGroupFormat.setGroupingUsed(true);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        return "$" + decimalFormat.format(num);
    }
}
