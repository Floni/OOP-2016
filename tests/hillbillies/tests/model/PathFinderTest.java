package hillbillies.tests.model;

import hillbillies.model.Terrain;
import hillbillies.model.util.PathFinder;
import hillbillies.model.vector.IntVector;
import hillbillies.model.World;
import org.junit.Before;
import org.junit.Test;

import java.util.Deque;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by timo on 3/24/16.
 *
 */
public class PathFinderTest {

    private PathFinder<IntVector> pathFinder;

    @Before
    public void setUp() throws Exception {
        pathFinder = new PathFinder<>(new PathFinder.PathGlue<IntVector>() {
            @Override
            public Stream<IntVector> getNeighbours(IntVector pos) {
                return Terrain.getNeighbours(pos);
            }

            @Override
            public double getCost(IntVector a, IntVector b) {
                return a.subtract(b).norm();
            }

            @Override
            public int getHeuristic(IntVector a, IntVector b) {
                IntVector diff = a.subtract(b);
                return Math.abs(diff.getX()) + Math.abs(diff.getY()) + Math.abs(diff.getZ());
            }
        });
    }

    @Test
    public void testGetPath() throws Exception {
        Deque<IntVector> path = pathFinder.getPath(new IntVector(0, 0, 0), new IntVector(2, 1, 1));
        IntVector last = new IntVector(0, 0, 0); // start
        for (IntVector pos : path) {
            IntVector diff = pos.subtract(last);
            assertTrue(Math.abs(diff.getX()) <= 1 && Math.abs(diff.getY()) <= 1 && Math.abs(diff.getZ()) <= 1);
            last = pos;
        }
        assertEquals(path.getLast(), new IntVector(2, 1, 1));
    }

    @Test
    public void testIsReachable() throws Exception {
        assertTrue(pathFinder.isReachable(new IntVector(0, 0, 0), new IntVector(2, 1, 1)));
    }
}