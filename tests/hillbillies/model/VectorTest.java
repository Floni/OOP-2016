package hillbillies.model;

import hillbillies.model.Vector.Vector;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by timo on 3/17/16.
 */
public class VectorTest {

    @Test
    public void testHashCode() throws Exception {
        Vector a = new Vector(1, 2, 3);
        Vector b = new Vector(4, 5, 6);
        Map<Vector, Integer> testMap = new HashMap<>();
        testMap.put(a, 12);
        testMap.put(b, 13);

        System.out.println(a.hashCode());
        System.out.println(b.hashCode());

        assertEquals(12, (long)testMap.get(new Vector(1, 2, 3)));
        assertEquals(12, (long)testMap.get(a));
        assertEquals(13, (long)testMap.get(new Vector(4, 5, 6)));
        assertFalse(testMap.containsKey(new Vector(1, 2, 4)));
    }

    @Test
    public void testEquals() throws Exception {
        Vector a = new Vector(1, 2, 3);
        Vector c = new Vector(1, 2, 3);
        Vector b = new Vector(4, 5, 6);
        assertTrue(a.equals(c));
        assertFalse(a.equals(null));
        assertFalse(a.equals(b));
    }
}