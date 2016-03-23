package hillbillies.tests.model;

import hillbillies.model.Unit.Unit;
import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;
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
 *
 */
public class UnitTest {

    private Unit unit;
    private World world;

    @Before
    public void setUp() throws Exception {
        // TODO: provide terrain & .. to world
        this.world = new World(null, null);
        this.unit = new Unit(world, "Timothy", 0, 0, 0, 50, 50, 50, 50);
    }

    @After
    public void tearDown() throws Exception {

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
    public void testAdvanceTime() throws Exception {
        // can we test AdvanceTime seperatly, we could check for the resting after three minutes?

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
        unit.moveTo(new IntVector(0,0,5));
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
        unit.moveTo(new IntVector(5, 5, 3));
        advanceTimeFor(unit, 30, 0.1);
        assertIntegerPositionEquals(5, 5, 3, unit.getPosition().toIntVector().toIntArray());
    }

    @Test
    public void testGetSpeedScalar() throws Exception {
        // can we test this, getSpeed is private?
        // We could moveToAdjacent on the same plane and then check if speed == 1.5
        // then move up, speed == 0.75
        // and move down, speed == 1.75?
        unit.setPosition(new Vector(25.5, 25.5, 25.5));
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
        unit.setPosition(new Vector(25, 25, 25));
        unit.moveToAdjacent(1, 0, 0);
        double oldSpeed = unit.getSpeedScalar();
        unit.setSprint(true);
        assertTrue(unit.isSprinting());
        assertEquals(2*oldSpeed, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testWork() throws Exception {
        //unit.work();
        assertTrue(unit.isWorking());
        advanceTimeFor(unit, 5, 0.1);
        assertTrue(unit.isWorking());
        advanceTimeFor(unit, 5.01, 0.1);
        assertFalse(unit.isWorking());
    }

    @Test
    public void testAttack() throws Exception {
        Unit other = new Unit(world, "Florian",1 , 1, 0, 50, 50, 50, 50);
        unit.attack(other);
        assertTrue(unit.isAttacking());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAttackOutOfRange() throws Exception {
        Unit other = new Unit(world, "Florian",5 , 5, 0, 50, 50, 50, 50);
        unit.attack(other);
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
        double seconds = 8;
        int extra_points = (int)((seconds / 0.2) * ((double)unit.getToughness() / 100.0));
        unit.rest();
        advanceTimeFor(unit, seconds, 0.1);
        assertEquals(extra_points, unit.getStamina());
    }

//    @Test
//    public void testRestRegenHP() throws Exception {
//        unit.setHitPoints(0);
//        unit.setStamina(unit.getMaxPoints() / 2);
//        double seconds = 8;
//        int extra_points = (int)((seconds / 0.2) * ((double)unit.getToughness() / 200.0));
//        unit.rest();
//        advanceTimeFor(unit, seconds, 0.1);
//        assertEquals(extra_points, unit.getHitPoints());
//    }

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
}