package hillbillies.tests.model;

import hillbillies.model.*;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.statement.SequenceStatement;
import hillbillies.model.programs.statement.Statement;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;
import ogp.framework.util.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

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

    @Test(expected = InvalidPositionException.class)
    public void testSetPositionNegativePosition() throws Exception {
        unit.setPosition(new Vector(-0.5, 0.5, 0.5));
    }

    @Test(expected = InvalidPositionException.class)
    public void testSetPositionLargePosition() throws Exception {
        unit.setPosition(new Vector(50.5, 0.5, 0.5));
    }

    @Test(expected = InvalidPositionException.class)
    public void testSetPositionSolid() {
        int[][][] terrain = new int[][][] { {{Terrain.Type.AIR.getId(), Terrain.Type.ROCK.getId()}}};
        World world = new World(terrain, (x, y, z) -> {});
        Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        world.addUnit(unit);
        unit.setPosition(new IntVector(0, 0, 1).toVector());
    }

    @Test
    public void testSetPosition1() throws Exception {
        double[] legalPosition = new double[] {0.5, 0.5, 0.5};
        unit.setPosition(new Vector(legalPosition));
        assertArrayEquals("Position in bounds", legalPosition, unit.getPosition().toDoubleArray(), 0.0);
    }

    @Test(expected = InvalidPositionException.class)
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

    @Test(expected = InvalidActionException.class)
    public void testSetSprintFail() throws Exception {
        unit.rest();
        unit.setSprinting(true);
    }

    @Test
    public void testIsSprinting() throws Exception {
        unit.setPosition(new Vector(25, 25, 0));
        unit.moveToAdjacent(1, 0, 0);
        double oldSpeed = unit.getSpeedScalar();
        unit.setSprinting(true);
        assertTrue(unit.isSprinting());
        assertEquals(2*oldSpeed, unit.getSpeedScalar(), Util.DEFAULT_EPSILON);
    }

    @Test
    public void testAttack() throws Exception {
        Unit other = world.spawnUnit(false);
        other.setPosition(unit.getPosition().add(1, 1, 0));
        unit.attack(other);
        assertTrue(unit.isAttacking());
        assertEquals(unit.getOrientation() % Math.PI, other.getOrientation() % Math.PI, Util.DEFAULT_EPSILON);
    }

    @Test (expected = InvalidUnitException.class)
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
        unit.setSprinting(true);
        advanceTimeFor(unit, 20, 0.1);
        unit.rest();
        advanceTimeFor(unit, 60, 0.1);
        assertEquals(unit.getMaxPoints(), unit.getStamina());
    }

    @Test
    public void testRestMinuteTimer() throws Exception {
        unit.setPosition(Vector.IDENTITY);
        unit.moveTo(new IntVector(15, 15, 0));
        unit.setSprinting(true);
        advanceTimeFor(unit, 3*60+1, 0.1);
        assertTrue(unit.isResting());
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

    private static void advanceTimeFor( Unit unit, double time, double step) {
        int n = (int) (time / step);
        for (int i = 0; i < n; i++)
            unit.advanceTime(step);
        unit.advanceTime(time - n * step);
    }

    @Test(expected = InvalidPositionException.class)
    public void setWorld() throws Exception {
        Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        unit.setWorld(world);
        unit.setPosition(new Vector(world.getTerrain().getMaxX(), 0, 0));
        unit.terminate();
    }

    @Test
    public void isAlive() throws Exception {
        Unit test = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        world.addUnit(test);
        Boulder boulder = new Boulder(world, IntVector.ZERO);
        world.addGameObject(boulder);
        test.workAt(IntVector.ZERO);
        advanceTimeFor(test, 15, 0.1);
        assertTrue(test.isCarryingBoulder());
        assertTrue(world.getTerrain().getBoulders(IntVector.ZERO).isEmpty());
        test.terminate();
        assertFalse(world.getTerrain().getBoulders(IntVector.ZERO).isEmpty());
        assertFalse(test.isAlive());
        assertFalse(world.getUnits().contains(test));
    }

    @Test
    public void testWorkAtLog() throws Exception {
        world.getTerrain().setCubeType(new IntVector(0, 0, 0), Terrain.Type.TREE);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        int oldXp = unit.getXp();
        unit.workAt(new IntVector(0, 0, 0));
        advanceTimeFor(unit, 500.0 / unit.getStrength() + 1.0, 0.1);
        assertFalse(unit.isWorking());
        assertEquals(Terrain.Type.AIR, world.getTerrain().getCubeType(new IntVector(0, 0, 0)));
        assertEquals(10, unit.getXp() - oldXp);
    }


    @Test
    public void isCarryingBoulder() throws Exception {
        Boulder boulder = new Boulder(world, new IntVector(0, 0, 0));
        world.addGameObject(boulder);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        unit.workAt(boulder.getPosition().toIntVector());
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertTrue(unit.isCarryingBoulder());
    }

    @Test
    public void isCarryingLog() throws Exception {
        Log log = new Log(world, new IntVector(0, 0, 0));
        world.addGameObject(log);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        unit.workAt(log.getPosition().toIntVector());
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertTrue(unit.isCarryingLog());
        unit.workAt(IntVector.ZERO);
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertFalse(unit.isCarryingBoulder());
    }

    @Test
    public void CarryingWeight() throws Exception {
        Log log = new Log(world, new IntVector(0, 0, 0));
        world.addGameObject(log);
        unit.setPosition(new Vector(1.5, 1.5, 0.5));
        int oldWeight = unit.getWeight();
        unit.workAt(log.getPosition().toIntVector());
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertTrue(unit.isCarryingLog());
        assertTrue(oldWeight + log.getWeight() <= unit.getWeight());
    }

    @Test
    public void CarryingSpeed() throws Exception {
        Log log = new Log(world, new IntVector(0, 0, 0));
        world.addGameObject(log);
        unit.setPosition(new Vector(2.5, 2.5, 0.5));
        unit.moveTo(new IntVector(1, 1, 0));
        advanceTimeFor(unit, 0.1, 0.1);
        double oldSpeed = unit.getSpeedScalar();
        advanceTimeFor(unit, 5, 0.1);
        unit.workAt(log.getPosition().toIntVector());
        advanceTimeFor(unit,  500.0 / unit.getStrength() + 1.0, 0.1);
        assertTrue(unit.isCarryingLog());
        unit.moveTo(new IntVector(2, 2, 0));
        advanceTimeFor(unit, 0.1, 0.1);
        assertTrue(oldSpeed >= unit.getSpeedScalar() );

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

    @Test
    public void testFalling() throws Exception {
        World world = new World(new int[5][5][5], ((x, y, z) -> {}));
        Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        world.addUnit(unit);
        unit.setPosition(new Vector(2.5, 2.5, 2.5));
        int oldHp = unit.getHitPoints();
        advanceTimeFor(unit, 0.2, 0.1);
        assertTrue(unit.isMoving()); // we are falling
        assertTrue(unit.isFalling());
        assertEquals(3.0, unit.getSpeedScalar(), 1e-6);
        advanceTimeFor(unit, 10, 0.1);
        assertEquals(new Vector(2.5, 2.5, 0.5), unit.getPosition());
        assertEquals(oldHp - (2 * 10), unit.getHitPoints());
    }

    @Test
    public void testPathBlocked() throws Exception {
        World world = new World(new int[5][5][1], (x, y, z) -> {});
        Unit unit = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        world.addUnit(unit);
        unit.moveTo(new IntVector(4, 0, 0));
        advanceTimeFor(unit, 0.2, 0.1);
        world.getTerrain().setCubeType(new IntVector(3, 0, 0), Terrain.Type.ROCK);
        advanceTimeFor(unit, 10, 0.1);
        assertEquals(new Vector(4.5, 0.5, 0.5), unit.getPosition());
    }

    @Test
    public void testUnitInit() throws Exception {
        Unit unit1 = new Unit("Test", 0, 0, 0, 0, 0, 0, 0);
        assertEquals(25, unit1.getAgility());
        assertEquals(25, unit1.getToughness());
        assertEquals(25, unit1.getStrength());
        assertEquals(25, unit1.getWeight());

        Unit unit2 = new Unit("Test", 0, 0, 0, 1000, 1000, 1000, 1000);
        assertEquals(100, unit2.getAgility());
        assertEquals(100, unit2.getToughness());
        assertEquals(100, unit2.getStrength());
        assertEquals(100, unit2.getWeight());

        Unit unit3 = new Unit("Test", 0, 0, 0, 50, 50, 50, 50);
        assertEquals(50, unit3.getAgility());
        assertEquals(50, unit3.getToughness());
        assertEquals(50, unit3.getStrength());
        assertEquals(50, unit3.getWeight());
    }
    // TODO: test change activity

    @Test(expected = InvalidActionException.class)
    public void testChangeActivity() throws Exception {
        Unit test = new Unit("Test", 0, 0, 0, 0, 0, 0, 0);
        unit.attack(test);
        assertTrue(unit.isAttacking());
        advanceTimeFor(unit, 0.5, 0.1);
        unit.rest();
    }

    @Test(expected = InvalidActionException.class)
    public void testInitRest() throws Exception {
        unit.rest();
        advanceTimeFor(unit, 0.05, 0.01);
        unit.workAt(IntVector.ZERO);
    }

    @Test
    public void testMovePending() throws Exception {
        unit.moveTo(new IntVector(25, 25, 0));
        unit.setSprinting(true); // drain stamina
        advanceTimeFor(unit, 4, 0.1);
        assertTrue(unit.isMoving());

        unit.rest(); // start resting
        advanceTimeFor(unit, 1, 0.01);
        assertTrue(unit.isResting());
        advanceTimeFor(unit, 30, 0.1); // finish resting

        assertTrue(unit.isMoving()); // continue moving
        advanceTimeFor(unit, 60, 0.1);
        assertFalse(unit.isMoving());
        assertEquals(new IntVector(25, 25, 0), unit.getPosition().toIntVector()); // finish moving
    }

    @Test
    public void testTasks() throws Exception {
        assertFalse(unit.hasAssignedTask());
        Task task = new Task("Test", 10, new SequenceStatement(Collections.emptyList()), null);
        unit.assignTask(task);
        assertTrue(unit.hasAssignedTask());
        assertEquals(task, unit.getAssignedTask());
    }

    @Test
    public void testTaskExecute() throws Exception {
        final boolean[] ran = new boolean[]{false};
        assertFalse(unit.hasAssignedTask());
        Task task = new Task("Test", 10, new Statement() {
            @Override
            public void reset() {

            }

            @Override
            public boolean isDone(Task task) {
                return false;
            }

            @Override
            public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
                ran[0] = true;
            }
        }, null);
        unit.assignTask(task);
        unit.startDefaultBehaviour();
        advanceTimeFor(unit, 5, 0.1);
        assertTrue(ran[0]);
    }

    // TODO: test levelUp
    // TODO: test follow
}