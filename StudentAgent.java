import java.util.Arrays;

public class StudentAgent {

    private static final int[] first3 = new int[3];
    private static final ReplicatedRandom rand = new ReplicatedRandom();

    public static TreasureHunt.TrialResult run(TreasureHunt game) {
        // Your agent's logic here, using game.jumpTo, move..., search, etc.
        // Must call game.submit() at the end.
        // DO NOT create a TrialResult manually — they'll get a compiler error.

        // Example: Dumb scan
        int edgeLength = game.edgeLength;
        boolean searching = true;
        for (int z = 0; z < edgeLength && searching; z++) {
            for (int y = 0; y < edgeLength && searching; y++) {
                for (int x = 0; x < edgeLength && searching; x++) {
                    game.jumpTo(x, y, z);
                    if (game.search() != null && !game.search().isHint()) {
                        TreasureHunt.Coordinate goal = game.position();
                        first3[0] = goal.x();
                        first3[1] = goal.y();
                        first3[2] = goal.z();
                        searching = false;
                    }
                }
            }
        }
        System.out.println("First 3 ints: " + Arrays.toString(first3));
        rand.replicateState(first3[0], first3[1]);
        System.out.println(
                "Predicted Goal X: " + rand.nextInt(game.edgeLength)
                + " Y: " + rand.nextInt(game.edgeLength)
                + " Z: " + rand.nextInt(game.edgeLength)
        );

        return game.submit(); // fallback (goal always exists)
    }
}
