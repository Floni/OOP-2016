package hillbillies.model;

import hillbillies.model.Vector.IntVector;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by timo on 3/18/16.
 *
 */
public class WorldTest {

    private World world;

    @Before
    public void setUp() throws Exception {

       // world = new World(null, null);

    }

    @Test
    public void testGetDirectlyAdjacent() {
        List<int[]> neighbours = World.getDirectlyAdjacent(IntVector.ZERO).map(IntVector::toIntArray).collect(Collectors.toList());
        assertArrayEquals(neighbours.toArray(), new int[][] {

                { -1, 0, 0 },
                { +1, 0, 0 },
                { 0, -1, 0 },
                { 0, +1, 0 },
                { 0, 0, -1 },
                { 0, 0, +1 }});
    }
}