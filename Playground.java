import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

class Playground {
    private static final int[] firstn = new int[3];
    private static final DebugRand rand = new DebugRand();
    private static final AtomicLong seed = new AtomicLong(-1);
    private static int[][] hints = new int[9][3];


    public static void main(String[] args) {
        long now = System.nanoTime();
        int bound = 20;
        System.out.println("Generating...");
        for (int i = 0; i < 3; i++)
            firstn[i] = rand.nextInt(bound);
        for (int i = 0; i < 9; i++) {
            hints[i][0] = rand.nextInt(bound);
            hints[i][1] = rand.nextInt(bound);
            hints[i][2] = rand.nextInt(bound);
        }
        System.out.println("First few: " + Arrays.toString(firstn));

        long initial = rand.getInitialSeed();

        System.out.println("Performing sanity check...");
        boolean failsafe = fastCheckSeed(initial, bound, true);
        if (!failsafe) {
            System.out.println("Failsafe failed");
            return;
        }
        long delta = (long) Math.pow(10, 15);
        System.out.println(initial + " -> " + now);
        checkN(now, delta * 10, bound);
        System.out.println(seed.get());
        System.out.println("Check " +
                (fastCheckSeed(seed.get(), bound, false) ? "passed" : "failed")
        );
        long guess = seed.get();

        Random gen = new Random();
        gen.setSeed(guess ^ 3447679086515839964L);
        System.out.println("First 3: " + Arrays.toString(firstn));
        for (int i = 0; i < 3; i++)
            System.out.print(gen.nextInt(bound) + ", ");
        System.out.println();

        // catch up to rand
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 3; j++)
                gen.nextInt(bound);

        for (int i = 0; i < 5; i++) {
            System.out.printf(
                    "%s==\n%s\n",
                    gen.nextInt(bound), rand.nextInt(bound)
            );
        }
    }


    /**
     * Checks the next `next` seeds before `initial`
     *
     * @param initial Starting seed
     * @param next    Lower bound of initial
     * @param bound   Bound of generated ints
     */
    private static void checkN(long initial, long next, int bound) {
        long test = initial;
        long bottom = Math.max(
                initial - next,
                0
        );
        System.out.printf("Checking from %s to %s\n", test, bottom);
        while (
                test > bottom &&
                !fastCheckSeed(test, bound, false) &&
                seed.get() == -1
        )
            test--;

        // set the seed
        if (fastCheckSeed(test, bound, false))
            seed.set(test);
        else
            System.out.println("No seed found: " + test);
    }

    /**
     * Checks if a seed produces the numbers in `first3`
     *
     * @param seed  The nanoTime seed
     * @param bound upper bound of numbers
     * @return Whether the seed is valid
     */
    private static boolean fastCheckSeed(long seed, int bound, boolean debug) {
        // Causes long overflow
        // seed ^ unique ^ multiplier & mask
        long unique = (
                (seed ^ 3447679086515839964L) ^ 0x5DEECE66DL)
                & ((1L << 48) - 1);
        if (debug)
            System.out.printf(
                    "[fcs] %s ^ qul ^ mul & mask = %s\n",
                    seed, unique
            );

        // first int
        unique = (unique * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int first = ((int) (unique >>> (48 - 31))) % bound;
        if (debug)
            System.out.printf(
                    "[fcs] %s %% %s = %s\n",
                    (int) (unique >>> (48 - 31)), bound, first
            );
        if (first != firstn[0])
            return false;

        unique = (unique * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int second = ((int) (unique >>> (48 - 31))) % bound;
        if (debug)
            System.out.printf(
                    "[fcs] %s %% %s = %s\n",
                    (int) (unique >>> (48 - 31)), bound, second
            );
        if (second != firstn[1])
            return false;

        unique = (unique * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int third = ((int) (unique >>> (48 - 31))) % bound;
        if (debug)
            System.out.printf(
                    "[fcs] %s %% %s = %s\n",
                    (int) (unique >>> (48 - 31)), bound, third
            );
        if (third != firstn[2])
            return false;

        for (int i = 0; i < hints.length; i++) {
            unique = (unique * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int x = ((int) (unique >>> (48 - 31))) % bound;
            if (debug)
                System.out.printf(
                        "[fcs] %s %% %s = %s\n",
                        (int) (unique >>> (48 - 31)), bound, x
                );

            unique = (unique * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int y = ((int) (unique >>> (48 - 31))) % bound;
            if (debug)
                System.out.printf(
                        "[fcs] %s %% %s = %s\n",
                        (int) (unique >>> (48 - 31)), bound, y
                );

            unique = (unique * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int z = ((int) (unique >>> (48 - 31))) % bound;
            if (debug)
                System.out.printf(
                        "[fcs] %s %% %s = %s\n",
                        (int) (unique >>> (48 - 31)), bound, z
                );

            boolean match = false;
            for (int[] cord : hints) {
                if (cord[0] == x && cord[1] == y && cord[2] == z) {
                    match = true;
                    break;
                }
            }
            if (!match)
                return false;
        }

        return true;
    }
}