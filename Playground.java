import java.util.Random;

class Playground {
    public static void main(String[] args) {
        int trials = 100_000;

        long sum = 0;
        int failed = 0;

        System.out.println("==== Balloon ====");
        for (int i = 0; i < trials; i++) {
            DeadHunt game = new DeadHunt(0);

            DeadHunt.TrialResult res = balloon(game);

            if (res != null) {
                sum += res.steps;
                if (i % 100 == 0)
                    System.out.print("Trial " + i + ": " + res.steps + "         \r");
            } else {
                failed++;
                System.out.println("Trial: " + trials + " failed: " + failed);
            }
        }
        double avg = (double) sum / trials;
        if (failed > 0)
            System.out.println("Failed runs: " + failed);
        System.out.println("Avg steps: " + avg + " (" + (4000 - avg) + ")");

        sum = 0;
        failed = 0;

        System.out.println("==== Dumb ====");
        for (int i = 0; i < trials; i++) {
            DeadHunt game = new DeadHunt(0);

            DeadHunt.TrialResult res = dumb(game);

            if (res != null) {
                sum += res.steps;
                if (i % 100 == 0)
                    System.out.print("Trial " + i + ": " + res.steps + "         \r");
            } else {
                failed++;
                System.out.println("Trial: " + trials + " failed: " + failed);
            }
        }
        avg = (double) sum / trials;
        if (failed > 0)
            System.out.println("Failed runs: " + failed);
        System.out.println("Avg steps: " + avg + " (" + (4000 - avg) + ")");

        sum = 0;
        failed = 0;

        System.out.println("==== Dumb Rand ====");
        for (int i = 0; i < trials; i++) {
            DeadHunt game = new DeadHunt(0);

            DeadHunt.TrialResult res = dumbRand(game);

            if (res != null) {
                sum += res.steps;
                if (i % 100 == 0)
                    System.out.print("Trial " + i + ": " + res.steps + "         \r");
            } else {
                failed++;
                System.out.println("Trial: " + trials + " failed: " + failed);
            }
        }
        avg = (double) sum / trials;
        if (failed > 0)
            System.out.println("Failed runs: " + failed);
        System.out.println("Avg steps: " + avg + " (" + (4000 - avg) + ")");
    }

    public static DeadHunt.TrialResult balloon(DeadHunt game) {
        CordsGen cg = new CordsGen(game.edgeLength);

        // Random start
        int[] pos = cg.randCord();
        int dist = 1;
        DeadHunt.Collectible cl = null;
        while (cl == null && dist <= game.edgeLength * 3) {
            cg.addHint(pos, dist, new int[]{1, game.edgeLength});

            while (cg.hasHint() && cl == null) {
                int[] cur = cg.nextHint();
                game.jumpTo(cur[0], cur[1], cur[2]);
                cl = game.search();
                if (cl == null)
                    cg.remCord(cur);
                else if (cl.isHint()) {
                    cg.addHint(cur, (int) cl.getMessage()[0], (int[]) cl.getMessage()[1]);
                } else {
                    return game.submit();
                }
            }

            dist++;
        }
        return game.submit();
    }

    public static DeadHunt.TrialResult dumb(DeadHunt game) {
        for (int x = 0; x < game.edgeLength; x++)
            for (int y = 0; y < game.edgeLength; y++)
                for (int z = 0; z < game.edgeLength; z++) {
                    game.jumpTo(x, y, z);
                    if (game.search() != null && !game.search().isHint())
                        return game.submit();
                }
        return game.submit();
    }

    public static DeadHunt.TrialResult dumbRand(DeadHunt game) {
        Random gen = new Random();
        int xo = gen.nextInt(game.edgeLength);
        int yo = gen.nextInt(game.edgeLength);
        int zo = gen.nextInt(game.edgeLength);

        for (int x = 0; x < game.edgeLength; x++)
            for (int y = 0; y < game.edgeLength; y++)
                for (int z = 0; z < game.edgeLength; z++) {
                    int x1 = (x + xo >= game.edgeLength) ? x + xo - game.edgeLength : x + xo;
                    int y1 = (y + yo >= game.edgeLength) ? y + yo - game.edgeLength : y + yo;
                    int z1 = (z + zo >= game.edgeLength) ? z + zo - game.edgeLength : z + zo;
                    game.jumpTo(x1, y1, z1);
                    if (game.search() != null && !game.search().isHint())
                        return game.submit();
                }
        return game.submit();
    }
}