import java.util.Random;

public class HiddenClass {
    private final long before = System.nanoTime();
    private final DebugRand rand = new DebugRand();
    private final long after = System.nanoTime();
    private final long secretKey;
    private final int secret2;

    public HiddenClass() {
        secretKey = rand.nextLong();
        secret2 = rand.nextInt(20);
        System.out.println("[HiddenClass] Shuffling...");
        shuffleRand();

        System.out.println("Key1: " + secretKey);
        System.out.println("Key2: " + secret2);
        System.out.println("Before: " + before);
        System.out.println("During: " + rand.getInitialSeed());
        System.out.println("After: " + after);
        System.out.printf(
                "Diff: ->R: %s, R->: %s",
                rand.getInitialSeed() - before,
                after - rand.getInitialSeed()
        );
    }

    private void shuffleRand() {
        for (int i = 0; i < 10; i++)
            rand.nextLong();
    }

    public boolean guessSecretKey(long key) {
        return secretKey == key || key == secret2;
    }
}
