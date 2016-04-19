package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.vector.IntVector;
import hillbillies.model.unit.Unit;
import hillbillies.model.programs.statement.Statement;

import java.util.HashSet;
import java.util.Set;

/**
 * Class implementing a task that a unit can execute.
 */
public class Task implements Comparable<Task> {

    private Unit assignedUnit;
    private int priority;

    private final String name;
    private final Statement mainStatement;
    private final IntVector selected;

    private final Set<Scheduler> schedulers;

    private boolean running;
    /**
     * Created a new task.
     * @param   name
     *          The name of the new task.
     * @param   priority
     *          The priority of the new task.
     * @param   main
     *          The main statement of the task.
     * @param   selected
     *          The position of the selected cube or null if no cube was selected.
     */
    public Task(String name, int priority, Statement main, IntVector selected) {
        this.name = name;
        this.mainStatement = main;
        this.selected = selected;
        this.setPriority(priority);
        this.running = false;
        this.schedulers = new HashSet<>();
    }

    /**
     * Adds a scheduler to the list of schedulers that can assign this task.
     *
     * @param   scheduler
     *          The scheduler.
     */
    public void addScheduler(Scheduler scheduler) {
        this.schedulers.add(scheduler);
    }

    public Set<Scheduler> getSchedulers() {
        return new HashSet<>(schedulers);
    }

    /**
     * Returns true when the task is assigned to an unit.
     *
     * @return  True if the task is assigned.
     *          | this.getAssignedUnit() != null
     */
    public boolean isAssigned() {
        return assignedUnit != null;
    }

    /**
     * Returns the assigned unit.
     */
    @Basic
    public Unit getAssignedUnit() {
        return assignedUnit;
    }

    /**
     * Sets the assigned unit.
     * @param   assignedUnit
     *          The new assigned unit.
     *
     * @post    The unit ...
     *          | new.getAssignedUnit() == assignedUnit
     */
    public void setAssignedUnit(Unit assignedUnit) {
        this.assignedUnit = assignedUnit;
    }

    /**
     * Returns the priority of the task.
     */
    @Basic
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     * @param   priority
     *          The new priority.
     * @post    ...
     *          | new.getPriority() == priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Compares the priority of two tasks.
     * @param   o
     *          The other task.
     * @return  ...
     */
    @Override
    public int compareTo(Task o) {
        return o.getPriority() - this.getPriority();
    }

    /**
     * Returns the selected position or null.
     */
    @Basic
    public IntVector getSelectedPosition() {
        return selected;
    }

    /**
     * Returns the name of the task.
     */
    @Basic
    public String getName() {
        return name;
    }

    /**
     * Runs the task for a certain amount of cycles.
     *
     * @param   time
     *          | The time the task may run for.
     */
    public void runFor(double time) {
        this.running = true;
        if (!mainStatement.isDone(this)) {
            do {
                mainStatement.execute(this);
                time -= 0.001;
            } while (!mainStatement.isDone(this) && this.isRunning() && time >= 0.001);
        }

        if (mainStatement.isDone(this)) {
            this.mainStatement.reset();
            this.finish();
        }

        this.running = false;
    }

    /**
     * Returns true if the task is currently running.
     */
    @Basic
    public boolean isRunning() {
        return running;
    }

    /**
     * Stops the runFor loop and waits for the task to be run again.
     */
    public void await() {
        this.running = false;
    }

    public void reset() {
        this.mainStatement.reset();
    }


    public void finish() {
        if (this.isAssigned())
            this.getAssignedUnit().assignTask(null);
        this.setAssignedUnit(null);

        schedulers.forEach(s -> s.finishTask(this));
        this.schedulers.clear();
    }

    public void interrupt() {
        if (isAssigned()) {
            await();
            reset();
            getAssignedUnit().assignTask(null);
        }

        setAssignedUnit(null);
        this.setPriority(this.getPriority() - 1);
        schedulers.forEach(s -> s.rebuildTask(this));
    }
}
