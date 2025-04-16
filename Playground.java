import org.apache.commons.lang3.reflect.FieldUtils;

public class Playground {
    public static void main(String[] args) {
        HiddenClass hc = new HiddenClass();
        long key = -1;
        try {
            key = (long) FieldUtils.readField(hc, "secretKey", true);
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(hc.guessSecretKey(key));
    }
}
