import java.util.concurrent.atomic.AtomicLong;
import java.util.random.RandomGenerator;

public class DebugRand implements RandomGenerator, java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 3905348978240129619L;

    private final AtomicLong seed;
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
    private final long initialSeed;
    private static boolean quiet = false;

    public DebugRand() {
        this(false);
    }

    public DebugRand(boolean logs) {
        long time = System.nanoTime();
        initialSeed = time;
        long qualifier = seedUniquifier();
        System.out.println("[DebugRand] Initial time: " + time);
        System.out.printf(
                "[DebugRand...] %s ^ %s -> %s -> %s\n",
                time, qualifier, time ^ qualifier, initialScramble(time ^ qualifier)
        );
        time = qualifier ^ time;
        seed = new AtomicLong(initialScramble(time));
        quiet = logs;
    }

    private static long initialScramble(long seed) {
        return (seed ^ multiplier) & mask;
    }

    public synchronized void setSeedUnscrambled(long seed) {
        if (!quiet)
            System.out.println("[setSeedUnscrambled] seed=" + seed);
        this.setSeed(seed, false);
    }

    public synchronized void setSeed(long seed) {
        this.setSeed(seed, true);
    }

    public synchronized void setSeed(long seed, boolean scramble) {
        if (!quiet)
            System.out.println("[setSeed] seed=" + seed);
        if (scramble) {
            if (!quiet)
                System.out.println("[setSeed...] seed->" + initialScramble(seed));
            this.seed.set(initialScramble(seed));
        } else
            this.seed.set(seed);
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        long old = seedUniquifier.get();
        for (; ; ) {
            long current = seedUniquifier.get();
            long next = current * 1181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next)) {
                if (!quiet)
                    System.out.println("[seedUniquifier] " + old + " -> " + next);
                return next;
            }
        }
    }

    private static final AtomicLong seedUniquifier
            = new AtomicLong(8682522807148012L);

    protected int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seed = this.seed;
        long old = seed.get();

        // Looping is done to deal with race conditions
        // see: https://stackoverflow.com/questions/32634280/how-does-compare-and-set-in-atomicinteger-works
        do {
            oldseed = seed.get();
            nextseed = (oldseed * multiplier + addend) & mask;
        } while (!seed.compareAndSet(oldseed, nextseed));

        if (!quiet)
            System.out.println("[next(" + bits + ")] " + old + " -> " + nextseed);
        return (int) (nextseed >>> (48 - bits));
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");
        int r = this.next(31);
        int m = bound - 1;
        if ((bound & m) == 0)  // i.e., bound is a power of 2
        {
            if (!quiet)
                System.out.printf(
                    "[nextInt(%s)] (%s * %s) >> 31 = %s\n",
                    bound, bound,
                    r, (int) ((bound * (long) r) >> 31)
            );
            r = (int) ((bound * (long) r) >> 31);
        } else {
            if (!quiet)
                System.out.printf(
                    "[nextInt(%s)] %s %% %s = %s\n",
                    bound, r, bound, r % bound
            );
            if (!quiet)
                System.out.printf(
                    "[nextInt(%s)...] %s - (%s %% %s) + %s < 0 = %s\n",
                    bound,
                    r, r, bound, m, (r - (r % bound) + m < 0)
            );
            long c = 0;
            // reject over-represented candidates
            for (
                    int u = r;
                    u - (r = u % bound) + m < 0;
                    u = this.next(31)
            )
                c++;

            if (!quiet)
                System.out.printf(
                    "[nextInt(%s)...] c=%s, r=%s\n",
                    bound, c, r
            );
        }
        return r;
    }

    @Override
    public long nextLong() {
        // it's okay that the bottom word remains signed.
        int r1 = this.next(32);
        int r2 = this.next(32);

        if (!quiet)
            System.out.printf(
                "[nextLong] (%s << 32) + %s = %s\n",
                r1, r2, ((long) (r1) << 32) + r2
        );
        return ((long) (r1) << 32) + r2;
    }

    public long getInitialSeed() {
        return initialSeed;
    }

    /**
     * Checks to see if the provided seed will produce the following bits
     *
     * @param seed Initial seed (System.nanoTime())
     * @param bound Upper bound for generation
     * @param first First n bits produced
     * @return `seed.next(len(bits)) == bits`
     */
    public boolean checkSeed(long seed, int bound, int first) {
        long unseed = this.seed.get();
        DebugRand tmp = new DebugRand();
        tmp.setSeedUnscrambled(seed);
        int actual = tmp.nextInt(bound);
        this.seed.set(unseed);
        return actual == first;
    }
}
