public class StudentAgent {
    public static TreasureHunt.TrialResult run(TreasureHunt game) {
        int edgeLength = game.edgeLength;
        CordsGen cg = new CordsGen(edgeLength);
        // Random start
        int[] pos = cg.randCord();
        int dist = 1;
        TreasureHunt.Collectible cl = null;
        TreasureHunt.TrialResult res = null;

        while (cl == null && dist <= 60) {
            cg.addHint(pos, dist, new int[]{1, 60});

            while (cg.hasHint() && cl == null) {
                int[] cur = cg.nextHint();
                game.jumpTo(cur[0], cur[1], cur[2]);
                cl = game.search();
                if (cl == null)
                    cg.remCord(cur);
                else if (cl.isHint()) {
                    cg.addHint(cur, (int) cl.getMessage()[0], (int[]) cl.getMessage()[1]);
                } else {
                    res = game.submit();
                }
            }

            dist++;
        }

        while (cg.numGoals() > cg.numHints() && res == null) {
            int[] cur = cg.nextHint();
            if (cur == null) {
                System.out.println("Out of hints!");
                break;
            }
            game.jumpTo(cur[0], cur[1], cur[2]);
            cl = game.search();
            if (cl == null)
                cg.remCord(cur);
            else if (cl.isHint()) {
                cg.addHint(cur, (int) cl.getMessage()[0], (int[]) cl.getMessage()[1]);
            } else {
                res = game.submit();
            }
        }

        while (res == null) {
            int[] cur = cg.nextGoal();
            if (cur == null)
                res = game.submit();
            else {
                game.jumpTo(cur[0], cur[1], cur[2]);
                cl = game.search();
                if (cl != null && !cl.isHint()) {
                    res = game.submit();
                } else if (cl != null && cl.isHint()) {
                    cg.addHint(cur, (int) cl.getMessage()[0], (int[]) cl.getMessage()[1]);
                }
            }
        }
        return game.submit();
    }
}
