package hillbillies.tests.model;

import hillbillies.model.Scheduler;
import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.statement.BreakStatement;
import hillbillies.model.programs.statement.SequenceStatement;
import hillbillies.model.programs.statement.Statement;
import hillbillies.model.programs.statement.WhileStatement;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import org.junit.Test;

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
        Task task1 = new Task("Test1", 12, new SequenceStatement(Collections.emptyList()), null);
        assertTrue(task1.isWellFormed());
        Task task2 = new Task("Test2", 12, new WhileStatement(t -> true, new BreakStatement()), null);
        assertTrue(task2.isWellFormed());
        Task task3 = new Task("Test3", 12, new BreakStatement(), null);
        assertFalse(task3.isWellFormed());
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
        // test get & set priority & check if scheduler lists are still sorted.
        Task task1 = new Task("Test1", 12, null, null);
        Task task2 = new Task("Test2", 1, null, null);
        assertEquals(1, task2.getPriority());
        assertEquals(12, task1.getPriority());

        Scheduler scheduler = new Scheduler();
        scheduler.schedule(task1);
        scheduler.schedule(task2);
        assertEquals(task1, scheduler.getAllTasksIterator().next());
        task2.setPriority(100);
        assertEquals(task2, scheduler.getAllTasksIterator().next());
    }


    @Test
    public void compareTo() throws Exception {
        // test neg & pos & 0 for equal, less and bigger priority between 2 tasks.
        Task task1 = new Task("Test1", 12, null, null);
        Task task2 = new Task("Test2", 12, null, null);
        assertEquals(0, task1.compareTo(task2));
        task1.setPriority(11);
        assertTrue( task1.compareTo(task2) > 0);
        task1.setPriority(13);
        assertTrue(task1.compareTo(task2) < 0);
    }

    @Test
    public void advanceTime() throws Exception {
        // test amount of times execute is called? see isRunning() and await()
        final int[] n = new int[]{0};
        Task task = new Task("Test", 10, new Statement() {

            @Override
            public void reset() {

            }

            @Override
            public boolean isDone(Task task) {
                return false;
            }

            @Override
            public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
                n[0] += 1;
            }
        }, null);
        task.advanceTime(0.000001);
        assertEquals(1, n[0]);
        n[0] = 0;
        task.advanceTime(0.002);
        assertEquals(2, n[0]);
        n[0] = 0;
        task.advanceTime(100 * 0.001 + 0.0005);
        assertEquals(100, n[0]);
    }

    @Test
    public void isRunning() throws Exception {
        // test that a task is running while advanceTime? custom statement?
        Task task = new Task("Test", 10, new Statement() {
            boolean done = false;

            @Override
            public void reset() {

            }

            @Override
            public boolean isDone(Task task) {
                return done;
            }

            @Override
            public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
                done = true;
                assertTrue(task.isRunning());
            }
        }, null);
        assertFalse(task.isRunning());
        task.advanceTime(1.0);
        assertFalse(task.isRunning());
    }

    @Test
    public void await() throws Exception {
        // test that a task stops running? custom statement?
        // test that a task is running while advanceTime? custom statement?
        Task task = new Task("Test", 10, new Statement() {
            boolean ran = false;
            @Override
            public void reset() {

            }

            @Override
            public boolean isDone(Task task) {
                return false; // should run forever but: await.
            }

            @Override
            public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
                assertFalse(ran); // task can only run 1 time.
                ran = true;

                assertTrue(task.isRunning());
                task.await();
                assertFalse(task.isRunning());
            }
        }, null);
        assertFalse(task.isRunning());
        task.advanceTime(1.0);
        assertFalse(task.isRunning());
    }

    @Test
    public void reset() throws Exception {
        // test reset.
        final boolean[] reset = {false};
        Task task = new Task("Test", 10, new Statement() {
            @Override
            public void reset() {
                reset[0] = true;
            }

            @Override
            public boolean isDone(Task task) {
                return false;
            }

            @Override
            public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
            }
        }, null);
        task.reset();
        assertTrue(reset[0]);
    }

    @Test
    public void interrupt() throws Exception {
        // test priority down, ....
        int priority = 10;
        Task task = new Task("Test", priority, new SequenceStatement(Collections.emptyList()), null);
        Unit unit = new Unit("Test", 0, 0, 0, 0, 0, 0, 0);
        task.setAssignedUnit(unit);

        assertTrue(task.isAssigned());

        task.interrupt();

        assertTrue(task.getPriority() < priority);
        assertFalse(task.isAssigned());
    }

    @Test
    public void getVariable() throws Exception {
        // test get & set Variable.
        Task task = new Task("Test", 10, null, null);
        task.setVariable("test", 12);
        assertEquals(12, task.getVariable("test"));
    }
}