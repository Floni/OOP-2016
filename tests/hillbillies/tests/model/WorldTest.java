package hillbillies.tests.model;

import hillbillies.model.Vector.IntVector;
import hillbillies.model.World;
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

    @Test
    public void testAdvanceTime() throws Exception {

    }

    @Test
    public void testIsValidPosition() throws Exception {

    }

    @Test
    public void testIsSolid() throws Exception {

    }

    @Test
    public void testIsCubeConnected() throws Exception {

    }

    @Test
    public void testGetCubeType() throws Exception {

    }

    @Test
    public void testGetCubeType1() throws Exception {

    }

    @Test
    public void testSetCubeType() throws Exception {

    }

    @Test
    public void testSetCubeType1() throws Exception {

    }

    @Test
    public void testBreakCube() throws Exception {

    }

    @Test
    public void testGetLogs() throws Exception {

    }

    @Test
    public void testGetBoulders() throws Exception {

    }

    @Test
    public void testGetLogs1() throws Exception {

    }

    @Test
    public void testGetBoulders1() throws Exception {

    }

    @Test
    public void testAddLog() throws Exception {

    }

    @Test
    public void testAddBoulder() throws Exception {

    }

    @Test
    public void testConsumeLog() throws Exception {

    }

    @Test
    public void testConsumeBoulder() throws Exception {

    }

    @Test
    public void testRemoveGameObject() throws Exception {

    }

    @Test
    public void testRemoveCubeObject() throws Exception {

    }

    @Test
    public void testAddCubeObject() throws Exception {

    }

    @Test
    public void testGetFactions() throws Exception {

    }

    @Test
    public void testSpawnUnit() throws Exception {

    }

    @Test
    public void testAddUnit() throws Exception {

    }

    @Test
    public void testGetUnits() throws Exception {

    }

    @Test
    public void testRemoveUnit() throws Exception {

    }

    @Test
    public void testGetNeighbours() throws Exception {

    }

    @Test
    public void testGetDirectlyAdjacent1() throws Exception {

    }
}