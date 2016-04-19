package hillbillies.model;

import hillbillies.model.list.SortedLinkedList;
import hillbillies.model.unit.Unit;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.Stream;

/**
 * Class to manage Tasks for a faction.
 */
public class Scheduler {

    private final SortedLinkedList<Task> allTasks;

    /**
     * Creates a new empty scheduler.
     */
    public Scheduler() {
        this.allTasks = new SortedLinkedList<>();
    }

    /**
     * Adds a task to the scheduler.
     *
     * @param   task
     *          | The task that will await execution.
     *
     * @post    The scheduler will contain the task
     *          | this.getAllTasksStream()....
     * @post    If the task isn't assigned the task will be available for execution
     *          | this.getAvailableTasksStream()....
     *
     */
    public void schedule(Task task) {
        this.allTasks.add(task);
        task.addScheduler(this);
    }

    /**
     * Checks whether a task is available for execution.
     *
     * @return  True if a task is available.
     *          | result == ...
     */
    public boolean isTaskAvailable() {
        return allTasks.stream().anyMatch(t -> !t.isAssigned());
    }

    /**
     * Gets the task with the highest priority for execution.
     *
     * @return  The task with highest priority
     *          | for (task : this.getItterator()) {
     *          |       result.getPriority() > task.getPriority()
     *          | }
     *
     * @throws  NoSuchElementException
     *          | If there are no tasks for execution
     *          | !this.isTaskAvailable()
     */
    public Task getTask(Unit unit) {
        Task ret = this.allTasks.stream().filter(t -> !t.isAssigned()).findFirst().orElseThrow(NoSuchElementException::new);
        ret.setAssignedUnit(unit);
        return ret;
    }

    /**
     * Finishes a task.
     *
     * @param   task
     *          The task that must be removed from the scheduler.
     *
     * @post    The task will be removed from the scheduler.
     *          | ...
     *
     */
    public void finishTask(Task task) {
        allTasks.remove(task);
    }

    /**
     * Interrupt task.
     *
     * @param task
     *
     * @post    The task will be available for execution again.
     *          | ...
     */
    public void rebuildTask(Task task) {
        allTasks.remove(task);
        allTasks.add(task);
    }

    /**
     * ...
     * @return
     */
    public Iterator<Task> getAllTasksIterator() {
        return allTasks.iterator();
    }

    /**
     *
     * @return
     */
    public Stream<Task> getAllTasksStream() {
        return allTasks.stream();
    }

    /**
     *
     * @param tasks
     * @return
     */
    public boolean containsAllTasks(Collection<Task> tasks) {
        return allTasks.containsAll(tasks);
    }

    /**
     *
     * @param original
     * @param replacement
     */
    public void replace(Task original, Task replacement) {
        if (original.isAssigned())
            throw new NotImplementedException(); // TODO: stop running task

        this.allTasks.remove(original);

        this.allTasks.add(replacement);
    }
}
