import java.util.Scanner;

/**
 * Provides terminal user interface for Blackjack
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static final Bankroll bankroll = new Bankroll();
    private static final Blackjack game = new Blackjack();
    private static double prevBal;

    public static void main(String[] args)
    {
        int prevHand = 0;

        // Initialize blackjack settings
        game.setNumOfDecks(
                Integer.parseInt(
                        valuePrompt("Number of decks:", true)));

        game.setNumOfHands(
                Integer.parseInt(
                        valuePrompt("Number of hands:", true)));

        // Initialize bankroll
        bankroll.setFunds(
                Double.parseDouble(
                        valuePrompt("Initial bankroll:", false)));

        prevBal = bankroll.getFunds();
        System.out.println("You can 'set-bet ($)', 'deal', 'set-hands (#)', or 'set-decks (#)'.");
        System.out.println("Say 'help' to see this again.");

        /* Blackjack rounds loop */
        while (true) {
            if (game.isRoundOngoing()) {
                boolean canDouble = game.getCurrentHand().numberOfCards() == 2;
                boolean canSplit = canDouble && game.getCurrentHand().isSplittable();

                // Presenting options based on standard rules
                if (canDouble) {
                    System.out.println("You can 'hit', 'double'"
                            + (canSplit ? ", 'split'" : "")
                            + ", or 'stand'.");
                } else {
                    System.out.println("You can 'hit' or 'stand'.");
                }

                String command = input.nextLine();

                switch (command) {
                    case "exit":
                        System.exit(0);
                    case "hit":
                        game.hit();
                        break;
                    case "stand":
                        game.stand();
                        break;
                    case "double":
                        if (canDouble && bankroll.canPayBet(1)) {
                            game.doubleDown();
                            bankroll.payBet();
                            break;
                        }
                        // If not, fall through to default
                    case "split":
                        if (canSplit && bankroll.canPayBet(1)) {
                            game.split();
                            bankroll.payBet();
                            break;
                        }
                        // If not, fall through to default
                    default:
                        System.out.println("Not an option.");
                        continue;
                }

                if (game.isRoundOngoing()) {
                    // If a hand is resolved, print its result before printing the next hand
                    if (game.getCurrentHandIndex() != prevHand) {
                        System.out.println(game.toString(prevHand) + "\n");
                        prevHand = game.getCurrentHandIndex();
                    }

                    System.out.println(game.toString(prevHand));
                    System.out.println("\n" + game.getRoundMetadata());
                } else {
                    // Round just ended
                    System.out.println(game);
                    System.out.println(evaluateBets());
                }
            } else {
                String[] command = input.nextLine().split(" ");

                switch (command[0]) {
                    case "deal":
                        if (bankroll.canPayBet(game.getNumOfHands())) {
                            prevHand = 0;
                            bankroll.payBet(game.getNumOfHands());
                            game.deal();
                            System.out.println(game);

                            if (!game.isRoundOngoing()) {
                                System.out.println(evaluateBets());
                            }
                        } else {
                            System.out.println(
                                    insufficientFundsMessage(bankroll.getCurrentBet()));
                        }
                        break;
                    case "set-bet":
                        if (command.length > 1) {
                            if (isNumber(command[1], false)) {
                                double bet = Double.parseDouble(command[1]);
                                if (bet * game.getNumOfHands() <= bankroll.getFunds()) {
                                    bankroll.setBet(Double.parseDouble(command[1]));
                                    System.out.println("Set.");
                                } else {
                                    System.out.println(insufficientFundsMessage(bet));
                                }
                            } else {
                                System.out.println("Must be a number.");
                            }
                        } else {
                            System.out.println("Include the dollar amount after 'set-bet'.");
                        }
                        break;
                    case "set-hands":
                        if (command.length > 1) {
                            if (isNumber(command[1], true)) {
                                int newNum = Integer.parseInt(command[1]);
                                if (newNum > 0) {
                                    game.setNumOfHands(newNum);
                                    System.out.println("Set.");
                                } else {
                                    System.out.println("The number of hands must be at least 1.");
                                }
                            } else {
                                System.out.println("Must be a number.");
                            }
                        } else {
                            System.out.println("Include the number of hands after 'set-hands'.");
                        }
                        break;
                    case "set-decks":
                        if (command.length > 1) {
                            if (isNumber(command[1], true)) {
                                int newNum = Integer.parseInt(command[1]);
                                System.out.println("Set.");
                                if (newNum > 0) {
                                    game.setNumOfDecks(newNum);
                                } else {
                                    System.out.println("The number of decks must be at least 1");
                                }
                            }
                        }
                        break;
                    case "help":
                        System.out.println("You can 'set-bet ($)', 'deal', 'set-hands (#)', or 'set-decks (#)'.");
                        break;
                    default:
                        System.out.println("Not an option.");
                        break;
                }
            }
        }
    }

    /**
     * Generates a message informing the user of insufficient funds
     *
     * @param  bet  bet to format and include
     * @return      String to be printed
     */
    public static String insufficientFundsMessage(double bet)
    {
        int numOfHands = game.getNumOfHands();

        return "You don't have enough for " + Bankroll.formatMoney(bet) + " on "
                + (numOfHands == 1 ? "1 hand." : ("each of " + numOfHands + " hands."))
                + " You have " + bankroll;
    }

    /**
     * Evaluates the hands and receive any winnings, then
     * returns a String describing the net change and balance.
     *
     * @return status of bankroll
     */
    public static String evaluateBets()
    {
        String report = "";

        for (Hand hand : game.getPlayerHands()) {
            int roundResult = hand.determineHandResult(game.getDealerHand());
            if (roundResult > 0) {
                bankroll.receiveBet(roundResult, hand.getBetMultiplier());
            }
        }

        report += "Net change: " + Bankroll.formatMoney(bankroll.getFunds() - prevBal);
        report += "\nYou have " + bankroll;
        prevBal = bankroll.getFunds();

        return report;
    }

    /**
     * Repeatedly prompts user for a valid number
     *
     * @param  prompt     the kind of input requested
     * @param  isInteger  true if number should be an integer, false if a double
     * @return            valid number as a String
     */
    public static String valuePrompt(String prompt, boolean isInteger)
    {
        do {
            System.out.println(prompt);
            String ans = input.nextLine();
            if (isNumber(ans, isInteger)) {
                return ans;
            } else {
                System.out.println("Must be a number.");
            }
        } while (true);
    }

    /**
     * Determines if the String is a valid integer or double
     *
     * @param  num        String to check
     * @param  isInteger  true if num should be an integer, false if a double
     * @return            whether it is valid
     */
    public static boolean isNumber(String num, boolean isInteger)
    {
        try {
            if (isInteger) {
                Integer.parseInt(num);
            } else {
                Double.parseDouble(num);
            }
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
}
