public class StudentAgent {
  public static TreasureHunt.TrialResult run(TreasureHunt game) {
    // Your agent's logic here, using game.jumpTo, move..., search, etc.
    // Must call game.submit() at the end.
    // DO NOT create a TrialResult manually — they'll get a compiler error.

    // Example: Dumb scan
    int edgeLength = game.edgeLength;
    for (int z = 0; z < edgeLength; z++) {
      for (int y = 0; y < edgeLength; y++) {
        for (int x = 0; x < edgeLength; x++) {
          game.jumpTo(x, y, z);
          if (game.search() != null && !game.search().isHint()) {
            return game.submit();
          }
        }
      }
    }
    return game.submit(); // fallback (goal always exists)
  }
}
