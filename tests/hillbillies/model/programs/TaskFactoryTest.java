package hillbillies.model.programs;

import hillbillies.model.Task;
import hillbillies.part3.programs.TaskParser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for factory
 */
public class TaskFactoryTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void invalidProgramTest() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                    "name: \"work task\"\npriority: 1\nactivities: work this;",
                    new TaskFactory(), new ArrayList<>());
        assertNull(tasks);

    }

    @Test
    public void validProgramTest() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: attack this;",
                new TaskFactory(), new ArrayList<>());
        assertNotNull(tasks);
    }

    @Test
    public void validProgramTestIf() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: if (true) then print this; fi if (false) then print this; fi",
                new TaskFactory(), new ArrayList<>());
        assertNotNull(tasks);
    }

    @Test
    public void invalidProgramTestIf() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: if (this) then print this; fi if (boulder) then print this; fi",
                new TaskFactory(), new ArrayList<>());
        assertNull(tasks);

    }

}