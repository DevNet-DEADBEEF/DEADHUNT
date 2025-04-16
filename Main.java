public class Main {

    public static void main(String[] args) {
        int trials = 1;
        int totalSteps = 0;
        int successCount = 0;

        for (int i = 0; i < trials; i++) {
            TreasureHunt game = new TreasureHunt();
            TreasureHunt.TrialResult result = StudentAgent.run(game);

            if (result.foundGoal) {
                totalSteps += result.steps;
                successCount++;
            }

            System.out.println("Trial " + (i + 1) + ": Found=" + result.foundGoal + ", Steps=" + result.steps);
        }

        System.out.println("\nSuccesses: " + successCount + "/" + trials);
        System.out.println("Average steps: " + (successCount > 0 ? ((double) totalSteps / successCount) : "N/A"));

        double successRate = (double) successCount / trials;
        double avgSteps = (successCount > 0) ? ((double) totalSteps / successCount) : Double.MAX_VALUE;
        double score = (successRate * 100000.0) / (avgSteps + 1);

        System.out.printf("Score: %.3f!\n", score);
    }
}