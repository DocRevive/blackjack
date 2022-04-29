/**
 * Simulates taking different actions over many iterations
 *
 * @author Daniel Kim
 * @version 4-4-22
 */
public class Simulator {
    public static String simulate(Blackjack game, int iterations)
    {
        double better = 0;
        double worse = 0;
        double bj = 0;
        double won = 0;
        double tied = 0;
        double lost = 0;

        Hand currentHand = game.getCurrentHand();
        int currentScore = currentHand.handScore(true);

        for (int i = 0; i < iterations; i++) {
            Blackjack gameIteration = new Blackjack(game);  // Copy current game
            Hand iterHand = gameIteration.getCurrentHand();

            // Use only the current hand
            gameIteration.resetPlayerHands();
            gameIteration.getPlayerHands().add(new Hand(currentHand));
            gameIteration.hit();

            if (gameIteration.isRoundOngoing()) {
                if (iterHand.handScore(true) >= currentScore) {
                    better++;
                } else {
                    worse++;
                }
            } else {
                switch (iterHand.determineHandResult(gameIteration.getDealerHand())) {
                    case 0 -> lost++;
                    case 1 -> won++;
                    case 2 -> bj++;
                    case 3 -> tied++;
                }
            }
        }

        return "Better: " + round(better / iterations * 100)
                + "%\nWorse: " + round(worse / iterations * 100)
                + "%\nBlackjack: " + round(bj / iterations * 100)
                + "%\nWon: " + round(won / iterations * 100)
                + "%\nTied: " + round(tied / iterations * 100)
                + "%\nLost: " + round(lost / iterations * 100) + "%";
    }

    public static double round(double value)
    {
        return ((double) Math.round(value * 100d) / 100d);
    }
}
