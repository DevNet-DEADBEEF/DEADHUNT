import java.util.Random;

public class TreasureHunt {

  // ---------------------------
  // Inner class: Coordinate
  // ---------------------------

  public class Coordinate {
    private int x, y, z;
    private final boolean readonly;

    private Coordinate(int x, int y, int z) {
      this(x, y, z, false);
    }

    private Coordinate(int x, int y, int z, boolean readonly) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.readonly = readonly;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }

    private void moveBy(int dx, int dy, int dz) {
      if (readonly)
        throw new UnsupportedOperationException("Cannot modify read-only coordinate");
      this.x += dx;
      this.y += dy;
      this.z += dz;
    }

    private void set(int x, int y, int z) {
      if (readonly)
        throw new UnsupportedOperationException("Cannot modify read-only coordinate");
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public int manhattanTo(Coordinate other) {
      return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof Coordinate)) return false;
      Coordinate o = (Coordinate) obj;
      return x == o.x && y == o.y && z == o.z;
    }

    @Override
    public String toString() {
      return "(" + x + "," + y + "," + z + ")";
    }
  }

  // ---------------------------
  // Inner Class: TrialResult
  // ---------------------------

  public class TrialResult {
    public final boolean foundGoal;
    public final int steps;

    private TrialResult(boolean foundGoal, int steps) {
      this.foundGoal = foundGoal;
      this.steps = steps;
    }
  }

  // ---------------------------
  // Inner Class: Collectible
  // ---------------------------

  public static class Collectible {
    private final boolean isHint;
    private final Object message;

    private Collectible() {
      this.isHint = false;
      this.message = (String) "goal";
    }

    private Collectible(int hintDist, int[] goalDistRange) {
      this.isHint = true;
      this.message = new Object[]{hintDist, goalDistRange};
    }

    private static Collectible goal() {
      return new Collectible();
    }
    
    private static Collectible hint(int distToPrev, int[] goalRange) {
      return new Collectible(distToPrev, goalRange);
    }

    public Object[] getMessage() {
      if (!isHint) return null;
      return (Object[]) this.message;
    }

    public boolean isHint() {
      return this.isHint;
    }
  }

  // ---------------------------
  // Fields
  // ---------------------------

  public final int edgeLength = 20;
  private final Collectible[][][] matrix;
  private final Coordinate[] hintCoords;
  private final Coordinate goalCoord;
  private final Coordinate currentCoord;
  private final Random rand = new Random();
  private int steps = 0;

  // ---------------------------
  // Constructor
  // ---------------------------

  public TreasureHunt() {
    this.matrix = new Collectible[edgeLength][edgeLength][edgeLength];
    this.hintCoords = new Coordinate[9];
    this.currentCoord = new Coordinate(0, 0, 0);

    this.goalCoord = createRandomCoord(true);
    setHintCoords();
    populateMatrix();
  }

  // ---------------------------
  // Public API (for students)
  // ---------------------------

  public Coordinate position() {
    return currentCoord;
  }

  public void jumpTo(int x, int y, int z) throws IllegalArgumentException {
    if (x < 0 || x >= edgeLength)
      throw new IllegalArgumentException("Error: Cannot set x to " + x + ". Out of bounds");
    if (y < 0 || y >= edgeLength)
      throw new IllegalArgumentException("Error: Cannot set y to " + y + ". Out of bounds");
    if (z < 0 || z >= edgeLength)
      throw new IllegalArgumentException("Error: Cannot set z to " + z + ". Out of bounds");

    currentCoord.set(x, y, z);
    steps++;
  }

  public Collectible search() {
    return matrix[currentCoord.x()][currentCoord.y()][currentCoord.z()];
  }

  public TrialResult submit() {
    boolean found = currentCoord.equals(goalCoord);
    return new TrialResult(found, steps);
  }

  public void moveLeft()   { if (canMoveLeft())  { currentCoord.moveBy(-1, 0, 0); steps++; } }
  public void moveRight()  { if (canMoveRight()) { currentCoord.moveBy( 1, 0, 0); steps++; } }
  public void moveUp()     { if (canMoveUp())    { currentCoord.moveBy( 0, 1, 0); steps++; } }
  public void moveDown()   { if (canMoveDown())  { currentCoord.moveBy( 0,-1, 0); steps++; } }
  public void moveFront()  { if (canMoveFront()) { currentCoord.moveBy( 0, 0, 1); steps++; } }
  public void moveBack()   { if (canMoveBack())  { currentCoord.moveBy( 0, 0,-1); steps++; } }  

  public boolean canMoveLeft()   { return currentCoord.x() > 0; }
  public boolean canMoveRight()  { return currentCoord.x() < edgeLength - 1; }
  public boolean canMoveUp()     { return currentCoord.y() < edgeLength - 1; }
  public boolean canMoveDown()   { return currentCoord.y() > 0; }
  public boolean canMoveFront()  { return currentCoord.z() < edgeLength - 1; }
  public boolean canMoveBack()   { return currentCoord.z() > 0; }

  // ---------------------------
  // Internal logic
  // ---------------------------

  private int rand_in_range() {
    return rand.nextInt(this.edgeLength);
  }

  private Coordinate createRandomCoord(boolean readonly) {
    return new Coordinate(rand_in_range(), rand_in_range(), rand_in_range(), readonly);
  }

  private void setHintCoords() {
    int count = 0;
    while (count < 9) {
      Coordinate newCoord = createRandomCoord(true);
      boolean duplicate = false;
      for (int i = 0; i < count; i++) {
        if (hintCoords[i].equals(newCoord)) {
          duplicate = true;
          break;
        }
      }
      if (!duplicate && !newCoord.equals(goalCoord)) {
        hintCoords[count++] = newCoord;
      }
    }
  }

  private void populateMatrix() {
    populateMatrixGoal();
    populateMatrixHints();
  }
  
  private void populateMatrixGoal() {
    this.matrix[this.goalCoord.x()][this.goalCoord.y()][this.goalCoord.z()] = Collectible.goal();
  }
  
  private void populateMatrixHints() {
    final int RANGE_SIZE = 5;

    for (int i = 0; i < hintCoords.length; i++) {
      Coordinate current = hintCoords[i];
      Coordinate prev = hintCoords[(i - 1 + hintCoords.length) % hintCoords.length];

      int distToPrev = current.manhattanTo(prev);
      int distToGoal = current.manhattanTo(goalCoord);

      int span = RANGE_SIZE;
      int halfSpan = rand.nextInt(span + 1);
      int low = Math.max(0, distToGoal - halfSpan);
      int high = Math.min(edgeLength * 3, distToGoal + (span - halfSpan));

      int[] rangeToGoal = new int[] { low, high };

      matrix[current.x()][current.y()][current.z()] = Collectible.hint(distToPrev, rangeToGoal);
    }
  }
}
