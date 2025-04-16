import java.util.Random;

public class HiddenClass {
    private final long before = System.nanoTime();
    private final DebugRand rand = new DebugRand();
    private final long after = System.nanoTime();
    private final long secretKey;
    private final int secret2;

    public HiddenClass() {
        secret2 = rand.nextInt(20);
        secretKey = rand.nextLong();
        System.out.println("[HiddenClass] Shuffling...");
        shuffleRand();

        System.out.println("Key1: " + secretKey);
        System.out.println("Key2: " + secret2);
        System.out.println("Before: " + before);
        System.out.println("During: " + rand.getInitialSeed());
        System.out.println("After: " + after);
        System.out.printf(
                "Diff: ->R: %s, R->: %s\n",
                rand.getInitialSeed() - before,
                after - rand.getInitialSeed()
        );

        long guess =
                rand.getInitialSeed()
                        ^ (8682522807148012L * 1181783497276652981L)
                        ^ 0x5DEECE66DL;
        System.out.printf(
                "Guess transforms: %s -> %s -> %s\n",
                rand.getInitialSeed(),
                rand.getInitialSeed() ^ (8682522807148012L * 1181783497276652981L),
                guess
        );
        System.out.println("Guess: " + guess + " = " + rand.checkSeed(guess, secret2));
    }

    private void shuffleRand() {
        for (int i = 0; i < 10; i++)
            rand.nextLong();
    }

    public boolean guessSecretKey(long key) {
        return secretKey == key || key == secret2;
    }
}
