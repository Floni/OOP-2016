package hillbillies.tests.model;

import hillbillies.model.Scheduler;
import hillbillies.model.Task;
import hillbillies.model.programs.statement.SequenceStatement;
import hillbillies.model.unit.Unit;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for Scheduler.
 */
public class SchedulerTest {
    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        this.scheduler = new Scheduler();
    }

    @Test
    public void schedule() throws Exception {
        Task task1 = new Task("Test1", 10, null, null);
        Task task2 = new Task("test2", 100, null, null);
        scheduler.schedule(task2);
        scheduler.schedule(task1);
        assertTrue(scheduler.getAllTasks().contains(task1));
        assertTrue(scheduler.getAllTasks().contains(task2));
        assertTrue(task1.getSchedulers().contains(scheduler));
        assertTrue(task2.getSchedulers().contains(scheduler));
    }

    @Test
    public void isTaskAvailable() throws Exception {
        assertFalse(scheduler.isTaskAvailable());
        Task task1 = new Task("Test1", 10, null, null);
        scheduler.schedule(task1);
        assertTrue(scheduler.isTaskAvailable());
    }

    @Test
    public void getTask() throws Exception {
        SequenceStatement stmt = new SequenceStatement(Collections.emptyList());
        Unit dummy1 = new Unit("Test", 0, 0, 0, 10, 10, 10, 10);
        Unit dummy2 = new Unit("Test", 0, 0, 0, 10, 10, 10, 10);

        Task task1 = new Task("Test1", 10, stmt, null);
        Task task2 = new Task("test2", 100, stmt, null);
        scheduler.schedule(task2);
        scheduler.schedule(task1);

        assertTrue(scheduler.isTaskAvailable());
        assertEquals(task2, scheduler.getTask(dummy1));
        assertEquals(task1, scheduler.getTask(dummy2));
        assertFalse(scheduler.isTaskAvailable());
    }

    @Test(expected = NoSuchElementException.class)
    public void getTaskError() throws Exception {
        scheduler.getTask(null);
    }

    @Test
    public void removeTask() throws Exception {
        Task task1 = new Task("Test1", 10, null, null);
        Task task2 = new Task("test2", 100, null, null);
        scheduler.schedule(task2);
        scheduler.schedule(task1);
        assertTrue(scheduler.getAllTasks().contains(task1));
        assertTrue(scheduler.getAllTasks().contains(task2));
        scheduler.removeTask(task1);
        assertFalse(scheduler.getAllTasks().contains(task1));
        assertFalse(task1.getSchedulers().contains(scheduler));
        assertTrue(scheduler.getAllTasks().contains(task2));
        scheduler.removeTask(task2);
        assertFalse(scheduler.getAllTasks().contains(task2));
        assertFalse(task2.getSchedulers().contains(scheduler));

    }

    @Test
    public void rebuildTask() throws Exception {
        Task task1 = new Task("Test1", 10, null, null);
        Task task2 = new Task("test2", 100, null, null);
        scheduler.schedule(task1);
        scheduler.schedule(task2);

        task1.setPriority(1000);
        scheduler.rebuildTask(task1);

        assertEquals(task1, scheduler.getAllTasks().get(0));
        assertEquals(task2, scheduler.getAllTasks().get(1));

    }

    @Test
    public void getAllTasksIterator() throws Exception {
        assertFalse(scheduler.getAllTasksIterator().hasNext());
        Task task1 = new Task("Test1", 10, null, null);
        scheduler.schedule(task1);
        assertTrue(scheduler.getAllTasksIterator().hasNext());
        assertEquals(task1, scheduler.getAllTasksIterator().next());
    }

    @Test
    public void getAllTasksStream() throws Exception {
        assertEquals(0, scheduler.getAllTasksStream().count());
        Task task1 = new Task("Test1", 10, null, null);
        scheduler.schedule(task1);
        assertEquals(1, scheduler.getAllTasksStream().count());
        assertEquals(task1, scheduler.getAllTasksStream().findAny().orElse(null));
    }

    @Test
    public void getAllTasks() throws Exception {
        assertEquals(0, scheduler.getAllTasksStream().count());
        Task task1 = new Task("Test1", 10, null, null);
        scheduler.schedule(task1);
        assertEquals(1, scheduler.getAllTasks().size());
        assertEquals(task1, scheduler.getAllTasks().get(0));
    }

    @Test
    public void containsAllTasks() throws Exception {
        assertTrue(scheduler.containsAllTasks(Collections.emptyList()));
        Task task1 = new Task("Test1", 10, null, null);
        Task task2 = new Task("test2", 100, null, null);
        scheduler.schedule(task2);
        scheduler.schedule(task1);

        assertTrue(scheduler.containsAllTasks(Collections.singleton(task1)));
        assertTrue(scheduler.containsAllTasks(Collections.singleton(task2)));
        Set<Task> all = new HashSet<>();
        all.add(task1);
        all.add(task2);
        assertTrue(scheduler.containsAllTasks(all));

    }

    @Test
    public void replace() throws Exception {
        Task task1 = new Task("Test1", 10, null, null);
        Task task2 = new Task("test2", 100, null, null);
        scheduler.schedule(task2);
        assertTrue(scheduler.containsAllTasks(Collections.singleton(task2)));
        scheduler.replace(task2, task1);
        assertTrue(scheduler.containsAllTasks(Collections.singleton(task1)));
    }

    @Test
    public void terminate() throws Exception {
        Task task1 = new Task("Test1", 10, null, null);
        Task task2 = new Task("test2", 100, null, null);
        scheduler.schedule(task2);
        scheduler.schedule(task1);

        scheduler.terminate();

        assertTrue(scheduler.getAllTasks().isEmpty());
        assertFalse(task1.getSchedulers().contains(scheduler));
        assertFalse(task2.getSchedulers().contains(scheduler));
    }

}