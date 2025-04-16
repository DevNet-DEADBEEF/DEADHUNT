import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DebugRand extends Random {
    private final AtomicLong seed;
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
    private final long initialSeed;

    public DebugRand() {
        long time = System.nanoTime();
        initialSeed = time;
        System.out.println("[DebugRand] Initial time: " + time);
        time ^= seedUniquifier();
        super(time ^ multiplier);
        seed = new AtomicLong(time);
    }

    public synchronized void setSeedUnscrambled(long seed) {
        super.setSeed(seed ^ multiplier);
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        long old = seedUniquifier.get();
        for (; ; ) {
            long current = seedUniquifier.get();
            long next = current * 1181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next)) {
                System.out.println("[seedUniquifier] " + old + " -> " + next);
                return next;
            }
        }
    }

    private static final AtomicLong seedUniquifier
            = new AtomicLong(8682522807148012L);

    @Override
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

        System.out.println("[next(" + bits + ")] " + old + " -> " + nextseed);
        return (int) (nextseed >>> (48 - bits));
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");
        int r = next(31);
        int m = bound - 1;
        if ((bound & m) == 0)  // i.e., bound is a power of 2
        {
            System.out.printf(
                    "[nextInt(%s)] (%s * %s) >> 31 = %s\n",
                    bound, bound,
                    r, (int) ((bound * (long) r) >> 31)
            );
            r = (int) ((bound * (long) r) >> 31);
        } else { // reject over-represented candidates
            System.out.printf(
                    "[nextInt(%s)] %s %% %s = %s\n",
                    bound, r, bound, r % bound
            );
            System.out.printf(
                    "[nextInt(%s)...] %s - (%s %% %s) + %s < 0 = %s\n",
                    bound,
                    r, r, bound, m, (r - (r % bound) + m < 0)
            );
            long c = 0;
            for (
                    int u = r;
                    u - (r = u % bound) + m < 0;
                    u = next(31)
            )
                c++;
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
        int r1 = next(32);
        int r2 = next(32);

        System.out.printf(
                "[nextLong] (%s << 32) + %s = %s\n",
                r1, r2, ((long) (r1) << 32) + r2
        );
        return ((long) (r1) << 32) + r2;
    }

    public long getInitialSeed() {
        return initialSeed;
    }
}
