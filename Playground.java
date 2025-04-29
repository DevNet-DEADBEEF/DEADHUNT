class Playground {
    public static void main(String[] args) {
        double bestScore = 0;
        boolean opt = false;

        boolean[] ab = new boolean[]{true, false};

        for (boolean test : ab) {
            int trials = 10_000;
            int totalSteps = 0;
            int maxSteps = Integer.MIN_VALUE;
            int successCount = 0;

            for (int i = 0; i < trials; i++) {
                TreasureHunt game = new TreasureHunt();
                CordsGen.ab = test;
                TreasureHunt.TrialResult result = StudentAgent.run(game);

                if (result.foundGoal) {
                    totalSteps += result.steps;
                    successCount++;
                    if (result.steps > maxSteps)
                        maxSteps = result.steps;
                }

                if (i % 100 == 0)
                    System.out.print(
                            "Trial " + i +
                            ": " + (result.foundGoal ? "X" : "O") +
                            ", S=" + result.steps +
                            "<" + maxSteps +
                            " ~: " + ((int) (((double) successCount / i) * 100_000.0d / totalSteps * i)) +
                            "            \r"
                    );
            }

            double successRate = (double) successCount / trials;
            System.out.println("Max Steps: " + maxSteps + "                 ");
            System.out.println("Successes: " + successCount + "/" + trials + " (" + ((int) (successRate * 100)) + "%)");
            System.out.println("Average steps: " + (successCount > 0 ? ((double) totalSteps / successCount) : "N/A"));

            double avgSteps = (successCount > 0) ? ((double) totalSteps / successCount) : Double.MAX_VALUE;
            double score = (successRate * 100_000.0) / (avgSteps + 1);

            System.out.printf("Score: %.3f!\n\n", score);

            if (score > bestScore) {
                bestScore = score;
                opt = test;
            }
        }

        System.out.println("\nBest score: " + bestScore);
        System.out.println("Best option: " + opt);
    }
}