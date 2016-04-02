package hillbillies.tests.model;

import hillbillies.model.Boulder;
import hillbillies.model.GameObject;
import hillbillies.model.Log;
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

        world = new World(new int[2][2][2], (x, y, z) -> {
        });

    }

    @Test
    public void testAdvanceTime() throws Exception {

    }

    @Test
    public void testIsValidPosition() throws Exception {
        assertTrue(world.isValidPosition(new IntVector(1, 1, 1)));
        assertFalse(world.isValidPosition(new IntVector(1, 1, world.Z_MAX)));
    }

    @Test
    public void testIsSolid() throws Exception {
        assertFalse(World.isSolid(World.AIR));
        assertFalse(World.isSolid(World.WORKSHOP));
        assertTrue(World.isSolid(World.ROCK));
        assertTrue(World.isSolid(World.TREE));
    }

    @Test
    public void testIsCubeConnected() throws Exception {
        assertFalse(world.isCubeConnected(new IntVector(1, 1, 1)));
    }

    @Test
    public void testGetCubeType() throws Exception {
        assertEquals(World.AIR, world.getCubeType(new IntVector(1, 1, 1)));
    }

    @Test
    public void testSetCubeType() throws Exception {
        world.setCubeType(new IntVector(1, 1, 1), World.ROCK);
        assertEquals(World.ROCK, world.getCubeType(new IntVector(1, 1, 1)));
    }

    @Test
    public void testBreakCube() throws Exception {

        world.setCubeType(new IntVector(1, 1, 1), World.ROCK);
        world.breakCube(new IntVector(1, 1, 1));
        assertEquals(World.AIR, world.getCubeType(new IntVector(1, 1, 1)));
    }

    @Test
    public void testGetLogs() throws Exception {
        assertEquals(0, world.getLogs().size());

    }

    @Test
    public void testGetBoulders() throws Exception {
        assertEquals(0, world.getBoulders().size());
    }

    @Test
    public void testGetLogs1() throws Exception {
        assertEquals(0, world.getLogs(new IntVector(1, 1, 1)).size());
    }

    @Test
    public void testGetBoulders1() throws Exception {
        assertEquals(0, world.getBoulders(new IntVector(1, 1, 1)).size());
    }

    @Test
    public void testAddLog() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Log(world, pos);
        world.addGameObject(pos, object);
        assertEquals(1, world.getLogs().size());
        assertTrue(world.getLogs().contains(object));
    }

    @Test
    public void testAddBoulder() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Boulder(world, pos);
        world.addGameObject(pos, object);
        assertEquals(1, world.getBoulders().size());
        assertTrue(world.getBoulders().contains(object));
    }

    @Test
    public void testConsumeLog() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Log(world, pos);
        world.addGameObject(pos, object);
        world.consumeLog(pos);
        assertEquals(0, world.getLogs(pos).size());
        assertEquals(0, world.getLogs().size());
    }

    @Test
    public void testConsumeBoulder() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Boulder(world, pos);
        world.addGameObject(pos, object);
        world.consumeBoulder(pos);
        assertEquals(0, world.getBoulders(pos).size());
        assertEquals(0, world.getBoulders().size());
    }

    @Test
    public void testRemoveGameObject() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Log(world, pos);
        world.addGameObject(pos, object);
        world.removeGameObject(object);
        assertEquals(0, world.getLogs().size());
        assertEquals(0, world.getLogs(pos).size());
    }

    @Test
    public void testRemoveCubeObject() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Log(world, pos);
        world.addGameObject(pos, object);
        world.removeCubeObject(object);
        assertNotEquals(0, world.getLogs().size());
        assertEquals(0, world.getLogs(pos).size());
    }

    @Test
    public void testAddCubeObject() throws Exception {
        IntVector pos = new IntVector(1, 1, 1);
        GameObject object = new Log(world, pos);
        world.addCubeObject(object);
        assertEquals(0, world.getLogs().size());
        assertNotEquals(0, world.getLogs(pos).size());
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
        assertEquals(0, world.getUnits().size());
    }

    @Test
    public void testRemoveUnit() throws Exception {

    }

    @Test
    public void testGetNeighbours() throws Exception {

    }

    @Test
    public void testGetDirectlyAdjacent() {
        List<int[]> neighbours = World.getDirectlyAdjacent(IntVector.ZERO).map(IntVector::toIntArray).collect(Collectors.toList());
        assertArrayEquals(neighbours.toArray(), new int[][]{

                {-1, 0, 0},
                {+1, 0, 0},
                {0, -1, 0},
                {0, +1, 0},
                {0, 0, -1},
                {0, 0, +1}});
    }

}