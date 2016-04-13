package hillbillies.tests.model;

import hillbillies.model.PathFinder;
import hillbillies.model.vector.IntVector;
import hillbillies.model.World;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
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
                return World.getNeighbours(pos);
            }

            @Override
            public double getCost(IntVector a, IntVector b) {
                return a.substract(b).norm();
            }
        });
    }

    @Test
    public void testGetPath() throws Exception {
        List<IntVector> path = pathFinder.getPath(new IntVector(0, 0, 0), new IntVector(2, 1, 1));
        IntVector last = new IntVector(2, 1, 1); // end
        for (IntVector pos : path) {
            IntVector diff = pos.substract(last);
            assertTrue(Math.abs(diff.getX()) <= 1 && Math.abs(diff.getY()) <= 1 && Math.abs(diff.getZ()) <= 1);
            last = pos;
        }
        assertEquals(path.get(0), new IntVector(2, 1, 1));
    }
}