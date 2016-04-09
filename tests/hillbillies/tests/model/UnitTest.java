package hillbillies.tests.model;

import hillbillies.model.Boulder;
import hillbillies.model.Faction;
import hillbillies.model.Log;
import hillbillies.model.Unit.Unit;
import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;
import hillbillies.part2.listener.TerrainChangeListener;
import ogp.framework.util.ModelException;
import ogp.framework.util.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static hillbillies.tests.util.PositionAsserts.assertDoublePositionEquals;
import static hillbillies.tests.util.PositionAsserts.assertIntegerPositionEquals;
import static org.junit.Assert.*;

/**
 * The test for the class Unit.
 * TODO: add new tests for new methods.
 *
 */
public class UnitTest {

    private Unit unit;
    private World world;

    @Before
    public void setUp() throws Exception {
        this.world = new World(new int[50][50][2], (x, y, z) -> {});

        this.unit = new Unit("Timothy", 0, 0, 0, 50, 50, 50, 50);

        world.addUnit(unit);
    }

    @After
    public void tearDown() throws Exception {
        unit.terminate();
    }

    @Test
    public void testSetPosition() throws Exception {
        assertDoublePositionEquals("Position in bounds",0.5,0.5,0.5,unit.getPosition().toDoubleArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPositionNegativePosition() throws Exception {
        unit.setPosition(new Vector(-0.5, 0.5, 0.5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPositionLargePosition() throws Exception {
        unit.setPosition(new Vector(50.5, 0.5, 0.5));
    }

    @Test
    public void testSetPosition1() throws Exception {
        double[] legalPosition = new double[] {0.5, 0.5, 0.5};
        unit.setPosition(new Vector(legalPosition));
        assertArrayEquals("Position in bounds", legalPosition, unit.getPosition().toDoubleArray(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPosition1Null() throws Exception {
        unit.setPosition(null);
    }

    @Test
    public void testSetName() throws Exception {
        unit.setName("Jacky");
        assertEquals("Jacky",unit.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameNoCapital() throws Exception {
        unit.setName("timothy");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetNameSpaceStart() throws Exception {
        unit.setName(" Timothy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameIllegalChar() throws Exception {
        unit.setName("Timothy @");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameShort() throws Exception {
        unit.setName("T");
    }

    @Test
    public void testSetWeight() throws Exception {
        unit.setWeight(100);
        assertEquals(100,unit.getWeight());
    }

    @Test
    public void testSetWeightSmall() throws Exception {
        unit.setWeight(((unit.getAgility()+unit.getStrength())/2 - 1));
        assertEquals((unit.getAgility()+unit.getStrength())/2, unit.getWeight());
    }

    @Test
    public void testSetWeightNegative() throws Exception {
        unit.setWeight(-1);
        assertEquals((unit.getAgility()+unit.getStrength())/2, unit.getWeight());
    }

    @Test
    public void testSetWeightLarge() throws Exception {
        unit.setWeight(201);
        assertEquals(200,unit.getWeight());
    }

    @Test
    public void testSetStrength() throws Exception {
        unit.setStrength(100);
        assertEquals(100,unit.getStrength());
    }

    @Test
    public void testSetStrengthLarge() throws Exception {
        unit.setStrength(201);
        assertEquals(200,unit.getStrength());
    }

    @Test
    public void testSetStrengthSmall() throws Exception {
        unit.setStrength(-1);
        assertEquals(1,unit.getStrength());
    }

    @Test
    public void testSetAgility() throws Exception {
        unit.setAgility(100);
        assertEquals(100,unit.getAgility());
    }

    @Test
    public void testSetAgilityLarge() throws Exception {
        unit.setAgility(201);
        assertEquals(200,unit.getAgility());
    }

    @Test
    public void testSetAgilitySmall() throws Exception {
        unit.setAgility(-1);
        assertEquals(1,unit.getAgility());
    }

    @Test
    public void testSetToughness() throws Exception {
        unit.setToughness(100);
        assertEquals(100,unit.getToughness());
    }

    @Test
    public void testSetToughnessSmall() throws Exception {
        unit.setToughness(-1);
        assertEquals(1,unit.getToughness());
    }

    @Test
    public void testSetToughnessLarge() throws Exception {
        unit.setToughness(201);
        assertEquals(200,unit.getToughness());
    }

    @Test
    public void testGetMaxPoints() throws Exception {
        unit.setWeight(124);
        unit.setToughness(167);
        assertEquals(unit.getMaxPoints(),(int) Math.ceil(200*((((double) unit.getWeight()) / 100)
                    *  (((double) unit.getToughness()) / 100))));
    }

    @Test
    public void testIsMoving() throws Exception {
        unit.moveTo(new IntVector(0,5,0));
        assertTrue(unit.isMoving());
    }

    @Test
    public void testIsMovingFalse() throws Exception {
        unit.moveToAdjacent(1, 0, 0);
        advanceTimeFor(unit, 5, 0.1);
        assertFalse(unit.isMoving());
    }

    @Test
    public void testMoveToAdjacent() throws Exception {
        unit.setPosition(Vector.ZERO);
        unit.moveToAdjacent(1, 0, 0);
        advanceTimeFor(unit, 5, 0.1);
        assertIntegerPositionEquals(1, 0, 0, unit.getPosition().toIntVector().toIntArray());
    }

    @Test
    public void testMoveTo() throws Exception {
        unit.setPosition(Vector.ZERO);
        unit.moveTo(new IntVector(5, 5, 1));
        advanceTimeFor(unit, 30, 0.1);
        assertIntegerPositionEquals(5, 5, 1, unit.getPosition().toIntVector().toIntArray());
    }

    @Test
    public void testGetSpeedScalar() throws Exception {
        // can we test this, getSpeed is private?
        // We could moveToAdjacent on the same plane and then check if speed == 1.5
        // then move up, speed == 0.75
        // and move down, speed == 1.75?
        unit.setPosition(new Vector(25.5, 25.5, 0.5));
        unit.moveToAdjacent(1, 1, 0);
        assertEquals(1.5, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
        advanceTimeFor(unit, 3, 0.1);
        unit.moveToAdjacent(0, 0, 1);
        assertEquals(0.75, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
        advanceTimeFor(unit, 3, 0.1);
        unit.moveToAdjacent(0, 0, -1);
        assertEquals(1.2 * 1.5, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
        advanceTimeFor(unit, 3, 0.1);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetSprintFail() throws Exception {
        unit.rest();
        unit.setSprint(true);
    }

    @Test
    public void testIsSprinting() throws Exception {
        unit.setPosition(new Vector(25, 25, 0));
        unit.moveToAdjacent(1, 0, 0);
        double oldSpeed = unit.getSpeedScalar();
        unit.setSprint(true);
        assertTrue(unit.isSprinting());
        assertEquals(2*oldSpeed, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testAttack() throws Exception {
        Unit other = world.spawnUnit(false);
        other.setPosition(unit.getPosition().add(0, 1, 0));
        unit.attack(other);
        assertTrue(unit.isAttacking());
        assertEquals(Math.PI/2, unit.getOrientation(), 1e-6);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAttackOutOfRange() throws Exception {
        Unit other = new Unit("Florian", 5 , 5, 0, 50, 50, 50, 50);
        world.addUnit(other);
        unit.attack(other);
        other.terminate();
    }

    @Test
    public void testRest() throws Exception {
        unit.rest();
        assertTrue(unit.isResting());
    }


    @Test
    public void testRestRegenStamina() throws Exception {
        unit.setPosition(Vector.ZERO);
        unit.moveTo(new IntVector(15, 15, 0));
        unit.setSprint(true);
        advanceTimeFor(unit, 20, 0.1);
        unit.rest();
        advanceTimeFor(unit, 60, 0.1);
        assertEquals(unit.getMaxPoints(), unit.getStamina());
    }

    @Test
    public void testStartDefaultBehaviour() throws Exception {
        unit.startDefaultBehaviour();
        assertTrue(unit.isDefaultEnabled());
    }

    @Test
    public void testStopDefaultBehaviour() throws Exception {
        unit.startDefaultBehaviour();
        unit.stopDefaultBehaviour();
        assertFalse(unit.isDefaultEnabled());
    }

    private static void advanceTimeFor( Unit unit, double time, double step) throws ModelException {
        int n = (int) (time / step);
        for (int i = 0; i < n; i++)
            unit.advanceTime(step);
        unit.advanceTime(time - n * step);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWorld() throws Exception {
        Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        unit.setWorld(world);
        unit.setPosition(new Vector(world.X_MAX, 0, 0));
        unit.terminate();
    }

    @Test
    public void isAlive() throws Exception {
        Unit test = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        world.addUnit(test);
        test.terminate();
        assertFalse(test.isAlive());
        assertFalse(world.getUnits().contains(test));
    }

    @Test
    public void testWorkAtLog() throws Exception {
        world.setCubeType(new IntVector(0, 0, 0), World.TREE);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        unit.workAt(new IntVector(0, 0, 0));
        advanceTimeFor(unit, 500.0 / unit.getStrength() + 1.0, 0.1);
        assertFalse(unit.isWorking());
        assertEquals(World.AIR, world.getCubeType(new IntVector(0, 0, 0)));
    }


    @Test
    public void isCarryingBoulder() throws Exception {
        Boulder boulder = new Boulder(world, new IntVector(0, 0, 0));
        world.addGameObject(boulder.getPosition().toIntVector(), boulder);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        unit.workAt(boulder.getPosition().toIntVector());
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertTrue(unit.isCarryingBoulder());
    }

    @Test
    public void isCarryingLog() throws Exception {
        Log log = new Log(world, new IntVector(0, 0, 0));
        world.addGameObject(log.getPosition().toIntVector(), log);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        unit.workAt(log.getPosition().toIntVector());
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertTrue(unit.isCarryingLog());
    }

    @Test
    public void getXp() throws Exception {
        assertEquals(0, unit.getXp());
    }

    @Test
    public void getFaction() throws Exception {
        assertNotNull(unit.getFaction());
        assertTrue(unit.getFaction().getUnits().contains(unit));
    }

    @Test
    public void setFaction() throws Exception {
        Faction test = new Faction();
        unit.setFaction(test);
        assertEquals(test, unit.getFaction());
    }
}