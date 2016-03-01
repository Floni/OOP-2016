package hillbillies.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static hillbillies.tests.util.PositionAsserts.assertDoublePositionEquals;
import static hillbillies.tests.util.PositionAsserts.assertIntegerPositionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import static org.junit.Assert.*;

/**
 * Created by timo on 24/02/2016.
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

    @Test
    public void testAdvanceTime() throws Exception {

    }

    @Test
    public void testIsValidPosition() throws Exception {

    }

    @Test
    public void testIsValidPosition1() throws Exception {

    }

    @Test
    public void testIsEffectivePosition() throws Exception {

    }

    @Test
    public void testSetPosition2() throws Exception {

    }

    @Test
    public void testSetPosition3() throws Exception {

    }

    @Test
    public void testSetPosition4() throws Exception {

    }

    @Test
    public void testGetPosition() throws Exception {

    }

    @Test
    public void testGetCubePosition() throws Exception {

    }

    @Test
    public void testIsValidName() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {

    }

    @Test
    public void testSetName() throws Exception {

    }

    @Test
    public void testIsValidWeight() throws Exception {

    }

    @Test
    public void testGetWeight() throws Exception {

    }

    @Test
    public void testSetWeight() throws Exception {

    }

    @Test
    public void testIsValidStrength() throws Exception {

    }

    @Test
    public void testGetStrength() throws Exception {

    }

    @Test
    public void testSetStrength() throws Exception {

    }

    @Test
    public void testIsValidAgility() throws Exception {

    }

    @Test
    public void testGetAgility() throws Exception {

    }

    @Test
    public void testSetAgility() throws Exception {

    }

    @Test
    public void testIsValidToughness() throws Exception {

    }

    @Test
    public void testGetToughness() throws Exception {

    }

    @Test
    public void testSetToughness() throws Exception {

    }

    @Test
    public void testIsValidHitPoints() throws Exception {

    }

    @Test
    public void testGetHitPoints() throws Exception {

    }

    @Test
    public void testSetHitPoints() throws Exception {

    }

    @Test
    public void testIsValidStamina() throws Exception {

    }

    @Test
    public void testGetStamina() throws Exception {

    }

    @Test
    public void testSetStamina() throws Exception {

    }

    @Test
    public void testGetMaxPoints() throws Exception {

    }

    @Test
    public void testIsValidOrientation() throws Exception {

    }

    @Test
    public void testGetOrientation() throws Exception {

    }

    @Test
    public void testSetOrientation() throws Exception {

    }

    @Test
    public void testIsMoving() throws Exception {

    }

    @Test
    public void testMoveToAdjacent() throws Exception {

    }

    @Test
    public void testMoveTo() throws Exception {

    }

    @Test
    public void testGetSpeedScalar() throws Exception {

    }

    @Test
    public void testSetSprint() throws Exception {

    }

    @Test
    public void testIsSprinting() throws Exception {

    }

    @Test
    public void testIsWorking() throws Exception {

    }

    @Test
    public void testWork() throws Exception {

    }

    @Test
    public void testIsAttacking() throws Exception {

    }

    @Test
    public void testAttack() throws Exception {

    }

    @Test
    public void testDefend() throws Exception {

    }

    @Test
    public void testIsResting() throws Exception {

    }

    @Test
    public void testRest() throws Exception {

    }

    @Test
    public void testStartDefaultBehaviour() throws Exception {

    }

    @Test
    public void testStopDefaultBehaviour() throws Exception {

    }

    @Test
    public void testIsDefaultEnabled() throws Exception {

    }
}