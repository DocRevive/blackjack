/**
 * Simulates taking different actions over many iterations
 *
 * @author Daniel Kim
 * @version 4-4-22
 */
public class Simulator {
    public static void main(String[] args)
    {
        // Test
        Blackjack game = new Blackjack();
        game.deal();
        System.out.println(game);
        System.out.println("\n===== 10,000 #1 =====\n");
        System.out.println(simulate(game, 10000));
        System.out.println("\n===== 10,000 #2 =====\n");
        System.out.println(simulate(game, 10000));
    }

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
            gameIteration.getPlayerHands().add(new Hand(iterHand));
            iterHand = gameIteration.getCurrentHand();
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

        return "Hitting results in a...\nBetter score: " + round(better / iterations * 100)
                + "%\nWorse score: " + round(worse / iterations * 100)
                + "%\nBlackjack: " + round(bj / iterations * 100)
                + "%\nWin: " + round(won / iterations * 100)
                + "%\nTie: " + round(tied / iterations * 100)
                + "%\nLoss: " + round(lost / iterations * 100) + "%";
    }

    public static double round(double value)
    {
        return ((double) Math.round(value * 100d) / 100d);
    }
}
