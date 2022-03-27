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
    private double currentBet = -1;

    /**
     * Constructor for Bankroll
     *
     * @param  funds  initial bankroll
     */
    public Bankroll(double funds)
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
    public double getFunds()
    {
        return funds;
    }

    /**
     * Getter for 'currentBet'
     *
     * @return current bet
     */
    public double getCurrentBet()
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
     * Set the bet for a new round.
     *
     * @param  bet  numerical bet <= available funds
     */
    public void setBet(double bet)
    {
        if (funds < bet) {
            throw new IllegalArgumentException("Can't bet more than the current funds.");
        } else {
            funds -= bet;
            currentBet = bet;
        }
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
