package hillbillies.model;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by timo on 3/17/16.
 */
public class PathFinder<T> {
    public class PriorityData implements Comparable<PriorityData> {

        private int priority;
        private T vector;

        public PriorityData(int priority, T vector) {
            this.vector = vector;
            this.priority = priority;
        }

        public T getData() {
            return vector;
        }

        @Override
        public int compareTo(PriorityData o) {
            return Integer.valueOf(priority).compareTo(o.priority);
        }
    }

    public interface PathGlue<T> {
        Stream<T> getNeighbours(T pos);
        double getCost(T a, T b);

        default int getHeuristic(T a, T b) {
            return (int)getCost(a, b);
        }
    }

    private PathGlue<T> glue;

    public PathFinder(PathGlue<T> glue) {
        this.glue = glue;
    }

    public List<T> getPath(T start, T target) {
        PriorityQueue<PriorityData> frontier = new PriorityQueue<>();
        frontier.add(new PriorityData(0, start));
        Map<T, T> cameFrom = new HashMap<>();
        Map<T, Double> costSoFar = new HashMap<>();

        cameFrom.put(start, null);
        costSoFar.put(start, 0.0);

        while (!frontier.isEmpty()) {
            T current = frontier.remove().getData();
            if (current.equals(target))
                break;

            glue.getNeighbours(current).forEach(next -> {
                double newCost = costSoFar.get(current) + glue.getCost(current, next);
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    int priority = (int)newCost + glue.getHeuristic(target, next);
                    frontier.add(new PriorityData(priority, next));
                    cameFrom.put(next, current);
                }
            });
        }

        List<T> path = new ArrayList<>();
        T current = target;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);

            if (current == null)
                return null;
        }
        //Collections.reverse(path);
        return path;
    }
}
