package hillbillies.model;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by timo on 3/17/16.
 */
public class PathFinder {
    public class PriorityVector implements Comparable<PriorityVector> {

        private int priority;
        private Vector vector;

        public PriorityVector(int priority, Vector vector) {
            this.vector = vector;
            this.priority = priority;
        }

        public Vector getVector() {
            return vector;
        }

        @Override
        public int compareTo(PriorityVector o) {
            return Integer.valueOf(priority).compareTo(o.priority);
        }
    }

    public interface PathGlue {
        Stream<Vector> getNeighbours(Vector pos);
        double getCost(Vector a, Vector b);
        int getHeuristic(Vector a, Vector b);
    }

    private PathGlue glue;

    public PathFinder(PathGlue glue) {
        this.glue = glue;
    }

    public List<Vector> getPath(Vector start, Vector target) {
        PriorityQueue<PriorityVector> frontier = new PriorityQueue<>();
        frontier.add(new PriorityVector(0, start)); //TODO: check ordering
        Map<Vector, Vector> cameFrom = new HashMap<>();
        Map<Vector, Double> costSoFar = new HashMap<>();

        cameFrom.put(start, null);
        costSoFar.put(start, 0.0);

        while (!frontier.isEmpty()) {
            Vector current = frontier.remove().getVector();
            if (current.equals(target))
                break;

            glue.getNeighbours(current).forEach(next -> {
                double newCost = costSoFar.get(current) + glue.getCost(current, next);
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    int priority = (int)newCost + glue.getHeuristic(target, next);
                    frontier.add(new PriorityVector(priority, next));
                    cameFrom.put(next, current);
                }
            });
        }

        List<Vector> path = new ArrayList<>();
        Vector current = target;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
            if (current == null)
                return null;
        }
        Collections.reverse(path);
        return path;
    }
}
