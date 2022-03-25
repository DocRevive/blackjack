import java.util.ArrayList;
import java.util.Scanner;

/**
 * The game of Blackjack in Java
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

    public void play(Scanner player)
    {

    }

    public void fillShoe()
    {
        String[] ranks = new String[]
                {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String rank : ranks) {
            for (int i = 0; i < numOfDecks; i++) {
                for (int j = 0; j < 4; j++) {
                    SHOE.add(new Card(rank, j));
                }
            }
        }
    }
}
