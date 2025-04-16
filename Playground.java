class Playground {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        HiddenClass hc = new HiddenClass();
        System.out.println("- Before: " + startTime);

        System.out.println(hc.guessSecretKey(-1));
    }
}