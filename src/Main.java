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

        // Initialize number of decks
        do {
            System.out.println("Number of decks:");
            String ans = input.nextLine();
            if (isInteger(ans)) {
                game = new Blackjack(Integer.parseInt(ans));
            } else {
                System.out.println("Must be a number.");
            }
        } while (game == null);

        // Initialize bankroll
        do {
            System.out.println("Initial bankroll (amount of money)");
            String ans = input.nextLine();
            if (isInteger(ans)) {
                bankroll = new Bankroll(Integer.parseInt(ans));
            } else {
                System.out.println("Must be a number.");
            }
        } while (bankroll == null);

        game.fillShoe();

        /* Blackjack rounds loop */
        while (true) {
            if (game.isInRound()) {
                System.out.println("You can 'hit' or 'stand'.");
                String command = input.nextLine();

                switch (command) {
                    case "hit":
                        game.hit();
                        break;
                    case "stand":
                        game.stand();
                        break;
                    case "exit":
                        System.exit(0);
                }

                System.out.println(game);
            } else {
                int roundResult = game.determineRoundResult();
                if (bankroll.getCurrentBet() != -1 && roundResult > 0) {
                    bankroll.receiveBet(roundResult);
                }

                System.out.println("Type a number for your next bet. You have $" + bankroll.getFunds());
                String number = input.nextLine();
                if (isInteger(number)) {
                    int bet = Integer.parseInt(number);
                    if (bankroll.getFunds() < bet) {
                        System.out.println("You don't have enough money for that bet.");
                    } else {
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

    public static boolean isInteger(String num)
    {
        try {
            Integer.parseInt(num);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
}
