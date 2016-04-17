package hillbillies.model.list;

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
        test.add(1);
        test.add(2);
        test.add(-2);
        test.add(100);
        System.out.println(test);
    }

}