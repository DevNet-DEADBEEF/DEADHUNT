import java.util.*;

public class CordsGen {
    private final Queue<Integer> hints = new LinkedList<>();
    private final Queue<Integer> goals = new LinkedList<>();
    private final HashSet<Integer> visited = new HashSet<>();
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
                    int iother = intify(other);
                    if (!visited.contains(iother) && Ndist(pos, other) == dist)
                        hints.add(iother);
                }

        if (goals.isEmpty())
            for (int x = Math.max(0, pos[0] - ghigh); x < Math.min(edge, pos[0] + ghigh); x++)
                for (int y = Math.max(0, pos[1] - ghigh); y < Math.min(edge, pos[1] + ghigh); y++)
                    for (int z = Math.max(0, pos[2] - ghigh); z < Math.min(edge, pos[2] + ghigh); z++) {
                        int[] other = new int[]{x, y, z};
                        int iother = intify(other);
                        if (visited.contains(iother))
                            continue;
                        int dist2 = Ndist(pos, other);
                        if (dist2 <= ghigh && dist2 >= glow)
                            goals.add(iother);
                    }
        else
            goals.removeIf(
                    other ->
                            Ndist(pos, deintify(other)) < glow ||
                            Ndist(pos, deintify(other)) > ghigh ||
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

    public int numVisited() { return visited.size(); }

    public void remCord(int[] pos) {
        int cord = intify(pos);
        visited.add(cord);
        hints.remove(cord);
        goals.remove(cord);
        failsafe();
    }

    public void failsafe() {
        if (hints.size() > 8000 || goals.size() > 8000 || visited.size() > 8000) {
            System.err.println("Something went wrong:");
            System.err.println("Hints: " + hints.size());
            System.err.println("Goals: " + goals.size());
            System.err.println("Visited: " + visited.size());
            System.exit(1);
        }
    }

    public int[] randCord() {
        return new int[]{
                random.nextInt(edge),
                random.nextInt(edge),
                random.nextInt(edge)
        };
    }

    public int[] nextHint() { return deintify(hints.poll()); }
    public boolean hasHint() { return !hints.isEmpty(); }

    public int[] nextGoal() { return deintify(goals.poll()); }

    public int intify(int[] pos) {
        // x y z
        // x * 10^2 + y * 10 + z
        return pos[0] * 10_000 + pos[1] * 100 + pos[2];
    }

    public int[] deintify(Integer pos) {
        if (pos == null)
            return null;
        int z = pos % 100;
        int y = (pos / 100) % 100;
        int x = (pos / 10_000) % 100;
        return new int[]{x, y, z};
    }
}
