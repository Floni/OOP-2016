package hillbillies.tests.model.util;

import hillbillies.model.util.Util;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for util class.
 */
public class UtilTest {
    @Test
    public void clamp() throws Exception {
        assertEquals(10, Util.clamp(10, 1, 100));
        assertEquals(1, Util.clamp(-100, 1, 100));
        assertEquals(100, Util.clamp(101, 1, 100));
    }

    @Test
    public void randomExclusive() throws Exception {
        for (int i = 0; i < 1000; i++) {
            int rand = Util.randomExclusive(5, 20);
            assertTrue(5 <= rand && rand < 20);
        }
    }

    @Test
    public void randomInt() throws Exception {
        for (int i = 0; i < 1000; i++) {
            int rand = Util.randomInt(10);
            assertTrue(rand < 10);
        }
    }

}