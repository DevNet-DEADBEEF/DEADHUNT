import java.util.Random;

public class HiddenClass {
    private final Random rand = new Random();
    private final long secretKey;

    public HiddenClass() {
        secretKey = rand.nextLong();
        shuffleRand();
    }

    private void shuffleRand() {
        for (int i = 0; i < 10; i++)
            rand.nextLong();
    }

    public boolean guessSecretKey(long key) {
        return secretKey == key;
    }
}
