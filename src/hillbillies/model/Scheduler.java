package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.util.SortedLinkedList;
import hillbillies.model.unit.Unit;

import java.util.*;
import java.util.stream.Stream;

/**
 * Class to manage Tasks for a faction.
 */
public class Scheduler {

    /**
     * A Sorted List of all tasks this scheduler controls.
     *
     * @invar   Each task must be effective.
     *          | for (Task task : tasks) task != null;
     */
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
     *          | new.getAllTasks().contains(task)
     * @post    The task will contain this scheduler in it's schedulers.
     *          | (new task).getSchedulers().contains(this)
     */
    public void schedule(Task task) {
        this.allTasks.add(task);
        task.addScheduler(this);
    }

    /**
     * Checks whether a task is available for execution.
     *
     * @return  True if a task is available which hasn't been assigned yet.
     *          | result == (for some task in this.getAllTasks():
     *          |               !task.isAssigned())
     */
    public boolean isTaskAvailable() {
        return this.getAllTasksStream().anyMatch(t -> !t.isAssigned());
    }

    /**
     * Gets the task with the highest priority for execution.
     *
     * @return  The task with highest priority that is not yet assigned.
     *          | for (Task task : this.getAllTasks())
     *          |   if (!task.isAssigned())
     *          |       result.getPriority() > task.getPriority()
     *
     * @effect The task will have the unit as assigned unit.
     *          | result.setAssignedUnit(unit)
     *
     * @throws  NoSuchElementException
     *          | If there are no tasks for execution
     *          | !this.isTaskAvailable()
     */
    public Task getTask(Unit unit) throws NoSuchElementException {
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
     *          | !new.getAllTasks().contains(task)
     */
    public void finishTask(Task task) {
        allTasks.remove(task);
    }

    /**
     * Rebuild the task list, used when a task changes priority.
     *
     * @param   task
     *          | The task that changed priority.
     *
     * @post    The task will be in allTasks
     *          | this.getAllTasks().contains(task)
     *
     * @post    The allTasks list will be sorted again.
     *          | getAllTasks.isSorted()
     */
    public void rebuildTask(Task task) {
        allTasks.remove(task);
        allTasks.add(task);
    }

    /**
     *  Returns an iterator over all tasks this scheduler controls.
     */
    @Basic
    public Iterator<Task> getAllTasksIterator() {
        return allTasks.iterator();
    }

    /**
     *  Returns a stream over all tasks this scheduler controls.
     */
    @Basic
    public Stream<Task> getAllTasksStream() {
        return allTasks.stream();
    }

    /**
     * Returns a list of all tasks this scheduler controls.
     */
    @Basic
    public SortedLinkedList<Task> getAllTasks() {
        return new SortedLinkedList<>(this.allTasks);
    }

    /**
     * Returns whether or not this schedulers controls all given tasks.
     *
     * @param   tasks
     *          The tasks to check.
     *
     * @return  True if this scheduler contains all given tasks, false otherwise.
     *          | result == this.getAllTasks().containsAll(tasks)
     */
    public boolean containsAllTasks(Collection<Task> tasks) {
        return allTasks.containsAll(tasks);
    }

    /**
     * Replace a task with the given replacement.
     *
     * @param   original
     *          The task to replace.
     * @param   replacement
     *          The replacement task.
     *
     * @effect  If the original is assigned, it's interrupted.
     *          | if (original.isAssigned()) original.interrupt()
     * @effect  The original is removed.
     *          | this.finishTask(original)
     * @effect  The replacement is added.
     *          | this.schedule(replacement)
     */
    public void replace(Task original, Task replacement) {
        if (original.isAssigned())
            original.interrupt();

        this.finishTask(original);
        this.schedule(replacement);
    }
}
