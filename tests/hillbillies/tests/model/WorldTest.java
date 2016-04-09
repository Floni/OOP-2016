package hillbillies.tests.model;

import hillbillies.model.*;
import hillbillies.model.Unit.Unit;
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

        world = new World(new int[2][2][2], (x, y, z) -> {
        });

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdvanceTime() throws Exception {
        world.advanceTime(2.0);
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
        Log log = new Log(world, new IntVector(0, 0, 0));
        world.addGameObject(log.getPosition().toIntVector(), log);
        assertEquals(1, world.getLogs().size());
        assertEquals(1, world.getLogs(log.getPosition().toIntVector()).size());
        assertEquals(0, world.getBoulders().size());
        assertTrue(world.getLogs().contains(log));
        assertTrue(world.getLogs(log.getPosition().toIntVector()).contains(log));

    }

    @Test
    public void testGetBoulders() throws Exception {
        assertEquals(0, world.getBoulders().size());
        assertEquals(0, world.getBoulders(new IntVector(0, 0, 0)).size());
        Boulder boulder = new Boulder(world, new IntVector(0, 0, 0));
        world.addGameObject(boulder.getPosition().toIntVector(), boulder);
        assertEquals(1, world.getBoulders().size());
        assertEquals(1, world.getBoulders(new IntVector(0, 0, 0)).size());
        assertEquals(0, world.getLogs().size());
        assertTrue(world.getBoulders().contains(boulder));
        assertTrue(world.getBoulders(boulder.getPosition().toIntVector()).contains(boulder));
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
        assertEquals(0, world.getFactions().size());
        world.spawnUnit(false);
        assertEquals(1, world.getFactions().size());
        world.spawnUnit(false);
        assertEquals(2, world.getFactions().size());
        world.spawnUnit(false);
        assertEquals(3, world.getFactions().size());
        world.spawnUnit(false);
        assertEquals(4, world.getFactions().size());
        world.spawnUnit(false);
        assertEquals(5, world.getFactions().size());
        world.spawnUnit(false);
        assertEquals(5, world.getFactions().size());
    }

    @Test
    public void testSpawnUnit() throws Exception {
        Unit unit1 = world.spawnUnit(false);
        assertFalse(unit1.isDefaultEnabled());
        assertTrue(world.getFactions().contains(unit1.getFaction()));
        assertTrue(world.getUnits().contains(unit1));
        Unit unit2 = world.spawnUnit(true);
        assertTrue(unit2.isDefaultEnabled());
        assertTrue(world.getFactions().contains(unit2.getFaction()));
        assertTrue(world.getUnits().contains(unit2));

        assertEquals(2, world.getUnits().size());
    }

    @Test
    public void testSpawnUnit2() throws Exception {
        for(int i = 0; i < 110; i++) {
            Unit unit = world.spawnUnit(false);
            if (i > 100)
                assertFalse(unit.isAlive());
        }
        assertEquals(100, world.getUnits().size());
    }

    @Test
    public void testAddUnit() throws Exception {
        Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        world.addUnit(unit);
        assertTrue(world.getFactions().contains(unit.getFaction()));
        assertTrue(world.getUnits().contains(unit));
    }

    @Test
    public void testAddUnit2() throws Exception {
        for(int i = 0; i < 110; i++) {

            Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
            world.addUnit(unit);
            assertTrue(unit.isAlive());
        }
        assertEquals(100, world.getUnits().size());
    }

        @Test
    public void testRemoveUnit() throws Exception {
        Unit unit = world.spawnUnit(false);
        assertTrue(world.getUnits().contains(unit));
        Faction oldFaction = unit.getFaction();
        world.removeUnit(unit);
        assertFalse(world.getUnits().contains(unit));
        assertFalse(world.getFactions().contains(oldFaction));
        assertNull(unit.getFaction());

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