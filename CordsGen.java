import java.util.*;

public class CordsGen {
    private final Queue<int[]> hints = new LinkedList<>();
    private final Queue<int[]> goals = new LinkedList<>();
    private final HashSet<int[]> visited = new HashSet<>();
    private final Random random = new Random();
    private final int edge;

    public CordsGen(int edge) {
        this.edge = edge;
    }

    public void addHint(int[] pos, int dist, int[] gran) {
        remCord(pos);
        hints.clear();

        int glow = Math.max(1, gran[0]);
        int ghigh = gran[1];

        // pos.x - d -> pos.x + d
        for (int x = Math.max(0, pos[0] - dist); x < Math.min(edge, pos[0] + dist); x++)
            for (int y = Math.max(0, pos[1] - dist); y < Math.min(edge, pos[1] + dist); y++)
                for (int z = Math.max(0, pos[2] - dist); z < Math.min(edge, pos[2] + dist); z++) {
                    int[] other = new int[] {x, y, z};
                    if (!visited.contains(other) && Ndist(pos, other) == dist)
                        hints.add(other);
                }

        if (goals.isEmpty())
            for (int x = Math.max(0, pos[0] - ghigh); x < Math.min(edge, pos[0] + ghigh); x++)
                for (int y = Math.max(0, pos[1] - ghigh); y < Math.min(edge, pos[1] + ghigh); y++)
                    for (int z = Math.max(0, pos[2] - ghigh); z < Math.min(edge, pos[2] + ghigh); z++) {
                        int[] other = new int[]{x, y, z};
                        if (visited.contains(other))
                            continue;
                        int dist2 = Ndist(pos, other);
                        if (dist2 <= ghigh && dist2 >= glow)
                            goals.add(other);
                    }
        else
            goals.removeIf(
                    other ->
                            Ndist(pos, other) < glow ||
                            Ndist(pos, other) > ghigh ||
                            visited.contains(other)
            );
    }

    public int Ndist(int[] u, int[] v) {
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += Math.abs(u[i] - v[i]);
        }
        return sum;
    }

    public int numGoals() {
        return goals.size();
    }

    public int numHints() {
        return hints.size();
    }

    public void remCord(int[] pos) {
        visited.add(pos);
        hints.remove(pos);
        goals.remove(pos);
    }

    public int[] randCord() {
        return new int[]{
                random.nextInt(edge),
                random.nextInt(edge),
                random.nextInt(edge)
        };
    }

    public int[] nextHint() {
        return hints.poll();
    }
    public boolean hasHint() { return !hints.isEmpty(); }

    public int[] nextGoal() {
        return goals.poll();
    }
}
