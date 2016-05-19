package hillbillies.tests.model.util;

import hillbillies.model.util.SortedLinkedList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for SortedLinkedList.
 */
public class SortedLinkedListTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {
        SortedLinkedList<Integer> test = new SortedLinkedList<>();
        for (int i = 0; i < 2500; i++) {
            test.add(12);
        }
        assertEquals(2500, test.size());
        int last = test.get(0);
        for (int el : test) {
            assertTrue(el >= last);
            last = el;
        }
        assertTrue(test.isSorted());
    }

    @Test
    public void fail() {
        class Data implements Comparable<Data>{
            public int priority;

            @Override
            public int compareTo(Data o) {
                return Integer.valueOf(this.priority).compareTo(o.priority);
            }
        }

        SortedLinkedList<Data> test = new SortedLinkedList<>();
        for (int i = 0; i < 100; i++) {
            Data t = new Data();
            t.priority = (int)(Math.random()*20);
            test.add(t);
        }
        assertTrue(test.isSorted());
        assertEquals(100, test.size());
        test.get(25).priority = 10000;
        test.get(75).priority = -11000;
        assertFalse(test.isSorted());
        Data t = test.get(25);
        Data t2 = test.get(75);

        test.remove(t);
        test.remove(t2);
        test.add(t);
        test.add(t2);

        assertTrue(test.isSorted());
    }

}