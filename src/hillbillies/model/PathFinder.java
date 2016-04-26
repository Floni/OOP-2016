package hillbillies.model;

import java.util.*;
import java.util.stream.Stream;

/**
 * Simple generic A* pathfinder.
 *
 * @param   <T>
 *          The type representing positions.
 *
 * @invar   The glue must be effective.
 *
 */
public class PathFinder<T> {
    /**
     * Class used for Priority queue.
     */
    private class PriorityData implements Comparable<PriorityData> {

        private int priority;
        private T vector;

        PriorityData(int priority, T vector) {
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
        int getHeuristic(T a, T b);
    }

    private final PathGlue<T> glue;

    /**
     *  Create a new pathfinder with the given glue.
     * @param   glue
     *          An interface providing methods to get the needed information.
     */
    public PathFinder(PathGlue<T> glue) throws IllegalArgumentException {
        if (glue == null)
            throw new IllegalArgumentException("glue isn't effective");
        this.glue = glue;
    }

    /**
     * Calculate a path from start to target.
     * @param   start
     *          The start position.
     * @param   target
     *          The end position.
     * @return  Returns a list of position from start to target.
     *          The list is in reverse order, the first element will be target and the last will be start.
     *          If no path can be found getPath returns null.
     */
    public Deque<T> getPath(T start, T target) {
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

        /*
        List<T> path = new ArrayList<>();
        T current = target;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);

            if (current == null)
                return null;
        }
        //Collections.reverse(path);
        */
        Deque<T> path = new ArrayDeque<>();
        T current = target;
        while(!current.equals(start)) {
            path.push(current);
            current = cameFrom.get(current);

            if (current == null)
                return null;
        }
        return path;
    }

    public boolean isReachable(T start, T target) {
        PriorityQueue<PriorityData> frontier = new PriorityQueue<>();
        frontier.add(new PriorityData(0, start));
        Map<T, Double> costSoFar = new HashMap<>();
        costSoFar.put(start, 0.0);

        while (!frontier.isEmpty()) {
            T current = frontier.remove().getData();
            if (current.equals(target))
                return true;

            glue.getNeighbours(current).forEach(next -> {
                double newCost = costSoFar.get(current) + glue.getCost(current, next);
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    int priority = (int)newCost + glue.getHeuristic(target, next);
                    frontier.add(new PriorityData(priority, next));
                }
            });
        }
        return false;
    }
}
