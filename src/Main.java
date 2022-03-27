import java.util.Scanner;

/**
 * Provides terminal user interface for Blackjack
 *
 * @author Daniel Kim
 * @version 3-25-22
 */
public class Main {
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        Bankroll bankroll = null;
        Blackjack game = null;
        int prevHand = 0;

        // Initialize number of decks
        do {
            System.out.println("Number of decks:");
            String ans = input.nextLine();
            if (isNumber(ans, true)) {
                game = new Blackjack(Integer.parseInt(ans));
            } else {
                System.out.println("Must be a number.");
            }
        } while (game == null);

        // Initialize bankroll
        do {
            System.out.println("Initial bankroll (amount of money):");
            String ans = input.nextLine();
            if (isNumber(ans, false)) {
                bankroll = new Bankroll(Double.parseDouble(ans));
            } else {
                System.out.println("Must be a number.");
            }
        } while (bankroll == null);

        game.fillShoe();

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
                        if (canDouble) {
                            game.doubleDown();
                            break;
                        }
                        // If not, fall through to default
                    case "split":
                        if (canSplit) {
                            game.split();
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
                    System.out.println(game);
                }
            } else {
                int roundResult = game.getPlayerHands().get(0).determineHandResult(game.getDealerHand());
                if (bankroll.getCurrentBet() != -1 && roundResult > 0) {
                    bankroll.receiveBet(roundResult);
                }

                System.out.println("Type a number for your next bet. You have " + Bankroll.formatMoney(bankroll.getFunds()));
                String number = input.nextLine();
                if (isNumber(number, false)) {
                    double bet = Double.parseDouble(number);
                    if (bankroll.getFunds() < bet) {
                        System.out.println("You don't have enough money for that bet.");
                    } else {
                        // Start round
                        prevHand = 0;
                        bankroll.setBet(bet);
                        game.deal();
                        System.out.println(game);
                    }
                } else {
                    System.out.println("Must be a number.");
                }
            }
        }
    }

    /**
     * Validates a String as an integer or a double.
     *
     * @param  num        possible number
     * @param  isInteger  true if it should be an integer; false if a double
     * @return            whether the number is parse-able as the type
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
