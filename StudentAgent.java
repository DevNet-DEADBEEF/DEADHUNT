public class StudentAgent {
    public static int maxSteps = 8000;
    private static int steps = 0;

    public static TreasureHunt.TrialResult run(TreasureHunt game) {
        CordsGen cg = new CordsGen(game);
        steps = 0;
        TreasureHunt.Collectible cl;
        TreasureHunt.TrialResult res = null;

        // Find the first hint
        cg.firstHint();

        // Find hints until we have narrowed the goal
        while (
                cg.numGoals() > cg.numHints() &&
                res == null &&
                remSteps() > cg.numGoals()
        ) {
            int[] cur = cg.nextHint();
            if (cur == null)
                break;
            jump(game, cur);
            cl = game.search();
            if (cl == null)
                cg.remCord(cur);
            else if (cl.isHint()) {
                cg.addHint(cur, (int) cl.getMessage()[0], (int[]) cl.getMessage()[1]);
            } else {
                res = game.submit();
            }
        }

        // Brute force the goal
        while (res == null) {
            int[] cur = cg.nextGoal();

            if (cur == null) // out of goals
                res = game.submit();
            else {
                jump(game, cur);
                cl = game.search();

                if (isGoal(cl)) { // is goal
                    res = game.submit();
                } else if (isHint(cl)) { // is hinted
                    cg.addHint(cur, (int) cl.getMessage()[0], (int[]) cl.getMessage()[1]);
                }
            }
        }
        return game.submit();
    }

    private static boolean nbail() { return steps < maxSteps; }
    public static int remSteps() { return maxSteps - steps; }

    public static void jump(TreasureHunt game, int[] pos) {
        game.jumpTo(pos[0], pos[1], pos[2]);
        steps++;
    }

    public static boolean isGoal(TreasureHunt.Collectible c) { return c != null && !c.isHint(); }
    public static boolean isHint(TreasureHunt.Collectible c) { return c != null && c.isHint(); }
}
