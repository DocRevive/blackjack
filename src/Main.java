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
        int numOfDecks = 0;

        do {
            System.out.println("Number of decks:");
            try {
                numOfDecks = Integer.parseInt(input.nextLine());
            } catch (final NumberFormatException e) {
                System.out.println("Must be a number.");
            }
        } while (numOfDecks == 0);

        Blackjack game = new Blackjack(numOfDecks);
        game.fillShoe();
    }
}
