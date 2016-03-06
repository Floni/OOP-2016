package hillbillies.tests.model;

import hillbillies.model.Unit;
import ogp.framework.util.ModelException;
import ogp.framework.util.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static hillbillies.tests.util.PositionAsserts.assertDoublePositionEquals;
import static hillbillies.tests.util.PositionAsserts.assertIntegerPositionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import static org.junit.Assert.*;

/**
 * The test for the class Unit.
 *
 */
public class UnitTest {

    private Unit unit;

    @Before
    public void setUp() throws Exception {
        this.unit = new Unit("Timothy", 0, 0, 0, 50, 50, 50, 50);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSetPosition() throws Exception {
        assertDoublePositionEquals("Position in bounds",0.5,0.5,0.5,unit.getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPositionNegativePosition() throws Exception {
        unit.setPosition(-0.5, 0.5, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPositionLargePosition() throws Exception {
        unit.setPosition(50.5, 0.5, 0.5);
    }

    @Test
    public void testSetPosition1() throws Exception {
        double[] legalPosition = new double[] {0.5, 0.5, 0.5};
        unit.setPosition(legalPosition);
        assertArrayEquals("Position in bounds", legalPosition, unit.getPosition(),0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPosition1NegativePosition() throws Exception {
        unit.setPosition(new double[] {-0.5, 0.5, 0.5});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPosition1LargePosition() throws Exception {
        unit.setPosition(new double[] {50.5, 0.5, 0.5});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPosition1Null() throws Exception {
        unit.setPosition(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPosition1WrongSize() throws Exception {
        unit.setPosition(new double[] {0.5, 0.5, 0.5, 0.5});
    }

    @Test
    public void testAdvanceTime() throws Exception {
        // can we test AdvanceTime seperatly, we could check for the resting after three minutes?

    }

    @Test
    public void testGetCubePosition() throws Exception {
        unit.setPosition(0.5, 0.5, 0.5);
        int[] result = new int[3];
        result = Unit.getCubePosition(unit.getPosition());
        assertTrue(
                result[0] == Math.floor(unit.getPosition()[0]) &&
                result[1] == Math.floor(unit.getPosition()[1]) &&
                result[2] == Math.floor(unit.getPosition()[2])
                );
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
    public void testSetHitPoints() throws Exception {
        unit.setHitPoints(25);
        assertEquals(25, unit.getHitPoints());
    }


    @Test (expected = AssertionError.class)
    public void testSetHitPointsSmall() throws Exception {
        unit.setHitPoints(-1);
    }

    @Test (expected = AssertionError.class)
    public void testSetHitPointsLarge() throws Exception {
        unit.setHitPoints(201);
    }

    @Test
    public void testSetStamina() throws Exception {
        unit.setStamina(25);
        assertEquals(25,unit.getStamina());
    }

    @Test (expected = AssertionError.class)
    public void testSetStaminaSmall() throws Exception {
        unit.setStamina(-1);
    }

    @Test (expected = AssertionError.class)
    public void testSetStaminaLarge() throws Exception {
        unit.setStamina(201);
    }

    @Test
    public void testGetMaxPoints() throws Exception {
        unit.setWeight(124);
        unit.setToughness(167);
        assertEquals(unit.getMaxPoints(),(int) Math.ceil(200*((((double) unit.getWeight()) / 100)
                    *  (((double) unit.getToughness()) / 100))));
    }

    @Test
    public void testSetOrientation() throws Exception {
        unit.setOrientation(Math.PI);
        assertEquals(Math.PI,unit.getOrientation(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testSetOrientationNegative() throws Exception {
        unit.setOrientation(-1*Math.PI);
        assertEquals(Math.PI,unit.getOrientation(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testSetOrientationLarge() throws Exception {
        unit.setOrientation(3*Math.PI);
        assertEquals(Math.PI,unit.getOrientation(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testIsMoving() throws Exception {
        unit.moveTo(new int[] {0,0,5});
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
        unit.setPosition(0, 0, 0);
        unit.moveToAdjacent(1, 0, 0);
        advanceTimeFor(unit, 5, 0.1);
        assertIntegerPositionEquals(1, 0, 0, Unit.getCubePosition(unit.getPosition()));
    }

    @Test
    public void testMoveTo() throws Exception {
        unit.setPosition(0, 0, 0);
        unit.moveTo(new int[] {5, 5, 3});
        advanceTimeFor(unit, 30, 0.1);
        assertIntegerPositionEquals(5, 5, 3, Unit.getCubePosition(unit.getPosition()));
    }

    @Test
    public void testGetSpeedScalar() throws Exception {
        // can we test this, getSpeed is private?
        // We could moveToAdjacent on the same plane and then check if speed == 1.5
        // then move up, speed == 0.75
        // and move down, speed == 1.75?
        unit.setPosition(25.5, 25.5, 25.5);
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
        unit.setPosition(25, 25, 25);
        unit.moveToAdjacent(1, 0, 0);
        double oldSpeed = unit.getSpeedScalar();
        unit.setSprint(true);
        assertTrue(unit.isSprinting());
        assertEquals(2*oldSpeed, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testWork() throws Exception {
        unit.work();
        assertTrue(unit.isWorking());
        advanceTimeFor(unit, 5, 0.1);
        assertTrue(unit.isWorking());
        advanceTimeFor(unit, 5.01, 0.1);
        assertFalse(unit.isWorking());
    }

    @Test
    public void testAttack() throws Exception {
        Unit other = new Unit("Florian",1 , 1, 0, 50, 50, 50, 50);
        unit.attack(other);
        assertTrue(unit.isAttacking());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAttackOutOfRange() throws Exception {
        Unit other = new Unit("Florian",5 , 5, 0, 50, 50, 50, 50);
        unit.attack(other);
    }

    @Test
    public void testDefend() throws Exception {
        Unit other = new Unit("Florian",1 , 1, 0, 50, 50, 50, 50);
        other.attack(unit);
        assertTrue(unit.isDefending());
    }

    @Test
    public void testRest() throws Exception {
        unit.rest();
        assertTrue(unit.isResting());
    }


    @Test
    public void testRestRegenStamina() throws Exception {
        unit.setHitPoints(unit.getMaxPoints());
        unit.setStamina(0);
        double seconds = 8;
        int extra_points = (int)((seconds / 0.2) * ((double)unit.getToughness() / 100.0));
        unit.rest();
        advanceTimeFor(unit, seconds, 0.1);
        assertEquals(extra_points, unit.getStamina());
    }

    @Test
    public void testRestRegenHP() throws Exception {
        unit.setHitPoints(0);
        unit.setStamina(unit.getMaxPoints() / 2);
        double seconds = 8;
        int extra_points = (int)((seconds / 0.2) * ((double)unit.getToughness() / 200.0));
        unit.rest();
        advanceTimeFor(unit, seconds, 0.1);
        assertEquals(extra_points, unit.getHitPoints());
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
}