package hillbillies.tests.model;

import hillbillies.model.*;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;
import ogp.framework.util.ModelException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for GameObject.
 */
public class GameObjectTest {

    private GameObject object;
    private World world;

    @Before
    public void setUp() throws Exception {
        world = new World(new int[5][5][5], (x, y, z) -> {});
        object = new Boulder(world, new IntVector(2, 2, 2));
    }

    @Test
    public void testPosition() throws Exception {
        assertEquals(object.getPosition(), new Vector(2.5, 2.5, 2.5));
        object.setPosition(new Vector(1.5, 2.5, 3.5));
        assertEquals(object.getPosition(), new Vector(1.5, 2.5, 3.5));
    }

    @Test
    public void testAdvanceTime() throws Exception {
        world.getTerrain().setCubeType(new IntVector(2, 2, 0), Terrain.Type.ROCK);
        object.setPosition(new Vector(2.5, 2.5, 4.5));

        world.getTerrain().setCubeType(new IntVector(2, 2, 3), Terrain.Type.ROCK);
        advanceTimeFor(object, 2, 0.1);
        assertEquals(new Vector(2.5, 2.5, 4.5), object.getPosition());

        world.getTerrain().setCubeType(new IntVector(2, 2, 3), Terrain.Type.AIR);
        advanceTimeFor(object, 2, 0.1);
        assertEquals(new Vector(2.5, 2.5, 1.5), object.getPosition());
    }

    @Test
    public void testGetWeight() throws Exception {
        assertTrue(object.getWeight() >= 10 && object.getWeight() <= 50);
        for (int i = 0; i < 100; i++) {
            GameObject object2 = new Log(world, new IntVector(1, 2, 3));
            assertTrue(String.format("%d", object2.getWeight()), object2.getWeight() >= 10 && object2.getWeight() <= 50);
        }
    }

    private static void advanceTimeFor(GameObject object, double time, double step) throws ModelException {
        int n = (int) (time / step);
        for (int i = 0; i < n; i++)
            object.advanceTime(step);
        object.advanceTime(time - n * step);
    }
}