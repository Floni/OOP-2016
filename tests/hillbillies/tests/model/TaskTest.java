package hillbillies.tests.model;

import hillbillies.model.Scheduler;
import hillbillies.model.Task;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Tests for Task.
 */
public class TaskTest {

    @Test
    public void getSelectedPosition() throws Exception {
        Task task = new Task("Test1", 12, null, new IntVector(12, 12, 12));
        assertEquals(task.getSelectedPosition().orElse(null), new IntVector(12, 12, 12));
        task = new Task("Test2", 12, null, null);
        assertFalse(task.getSelectedPosition().isPresent());
    }

    @Test
    public void getName() throws Exception {
        Task task = new Task("Test1", 12, null, null);
        assertEquals("Test1", task.getName());
        task = new Task("", 12, null, null);
        assertEquals("", task.getName());
    }

    @Test
    public void isWellFormed() throws Exception {
        // TODO: only test in Factory?
    }

    @Test
    public void addScheduler() throws Exception {
        Task task = new Task("Test1", 12, null, null);
        Scheduler scheduler1 = new Scheduler();
        Scheduler scheduler2 = new Scheduler();
        task.addScheduler(scheduler1);
        task.addScheduler(scheduler2);
        assertTrue(task.getSchedulers().contains(scheduler1));
        assertTrue(task.getSchedulers().contains(scheduler2));
    }

    @Test
    public void removeScheduler() throws Exception {
        Task task = new Task("Test1", 12, null, null);
        Scheduler scheduler1 = new Scheduler();
        Scheduler scheduler2 = new Scheduler();
        task.addScheduler(scheduler1);
        task.addScheduler(scheduler2);
        assertTrue(task.getSchedulers().contains(scheduler1));
        assertTrue(task.getSchedulers().contains(scheduler2));
        task.removeScheduler(scheduler1);
        assertFalse(task.getSchedulers().contains(scheduler1));
        assertTrue(task.getSchedulers().contains(scheduler2));

    }

    @Test
    public void getSchedulers() throws Exception {
        Task task1 = new Task("Test1", 12, null, null);
        Task task2 = new Task("Test2", 1, null, null);
        Scheduler scheduler1 = new Scheduler();
        Scheduler scheduler2 = new Scheduler();

        scheduler1.schedule(task1);
        scheduler1.schedule(task2);

        scheduler2.schedule(task1);

        assertTrue(task1.getSchedulers().contains(scheduler1));
        assertTrue(task1.getSchedulers().contains(scheduler2));

        assertTrue(task2.getSchedulers().contains(scheduler1));
        assertFalse(task2.getSchedulers().contains(scheduler2));
        scheduler2.schedule(task2);
        assertTrue(task2.getSchedulers().contains(scheduler2));
    }

    @Test
    public void isAssigned() throws Exception {
        Task task1 = new Task("Test1", 12, null, null);
        assertFalse(task1.isAssigned());
        task1.setAssignedUnit(new Unit("Test", 0, 0, 0, 0, 0, 0, 0));
        assertTrue(task1.isAssigned());
        task1.setAssignedUnit(null);
        assertFalse(task1.isAssigned());
    }

    @Test
    public void getAssignedUnit() throws Exception {
        Task task1 = new Task("Test1", 12, null, null);
        assertNull(task1.getAssignedUnit());
    }

    @Test
    public void getPriority() throws Exception {

    }

    @Test
    public void setPriority() throws Exception {

    }

    @Test
    public void compareTo() throws Exception {

    }

    @Test
    public void advanceTime() throws Exception {

    }

    @Test
    public void isRunning() throws Exception {

    }

    @Test
    public void await() throws Exception {

    }

    @Test
    public void reset() throws Exception {

    }

    @Test
    public void finish() throws Exception {

    }

    @Test
    public void interrupt() throws Exception {

    }

    @Test
    public void getVariable() throws Exception {

    }

    @Test
    public void setVariable() throws Exception {

    }

}