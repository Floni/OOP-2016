package hillbillies.tests.model.list;

import hillbillies.model.list.SortedLinkedList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by timo on 4/17/16.
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
    }

}