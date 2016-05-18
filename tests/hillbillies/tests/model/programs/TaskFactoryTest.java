package hillbillies.tests.model.programs;

import hillbillies.model.Task;
import hillbillies.model.programs.TaskFactory;
import hillbillies.part3.programs.TaskParser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for factory
 */
public class TaskFactoryTest {
    // TODO: more tests
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void invalidProgramTest() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: w := (1, 2, 3); attack w;",
                new TaskFactory(), new ArrayList<>());
        assertNull(tasks);

    }

    @Test
    public void invalidBreakTest() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: break;",
                new TaskFactory(), new ArrayList<>());
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertFalse(tasks.get(0).isWellFormed());

    }

    @Test
    public void validBreakTest() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: while true do break; done",
                new TaskFactory(), new ArrayList<>());
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isWellFormed());

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

    @Test
    public void invalidProgramTestVar() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: w := true; w := workshop;",
                new TaskFactory(), new ArrayList<>());
        assertNull(tasks);

    }

    @Test
    public void invalidProgramTestAssign() {
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: moveTo w; w := workshop;",
                new TaskFactory(), new ArrayList<>());
        assertNull(tasks);

    }

    @Test
    public void invalidProgramTestMulti() {
        TaskFactory factory = new TaskFactory();
        List<Task> tasks = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: w := workshop; w := here;",
                factory, new ArrayList<>());
        List<Task> tasks2 = TaskParser.parseTasksFromString(
                "name: \"work task\"\npriority: 1\nactivities: w := enemy; w := friend;",
                factory, new ArrayList<>());
        assertNotNull(tasks);
        assertNotNull(tasks2);

    }


}