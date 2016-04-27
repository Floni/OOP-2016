package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.statement.BreakChecker;
import hillbillies.model.vector.IntVector;
import hillbillies.model.unit.Unit;
import hillbillies.model.programs.statement.Statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing a task that a unit can execute.
 */
public class Task implements Comparable<Task> {

    //<editor-fold desc="Variables">
    private Unit assignedUnit;
    private int priority;

    private final String name;
    private final Statement mainStatement;
    private final IntVector selected;

    /**
     * The set of all schedulers this task belongs to.
     *
     * @invar   Each scheduler must be effective.
     *          | ..
     * @invar   Each scheduler must contain this task.
     */
    private final Set<Scheduler> schedulers;

    private boolean running;

    /**
     * A map from string to Object (IntVector, Boolean or Unit).
     *
     * @invar   Each Object must be effective
     *          | ..
     * @invar   Each Object must be either a Boolean, IntVector or Unit.
     *          | ..
     */
    private Map<String, Object> variableTable;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Created a new task.
     *
     * @param   name
     *          The name of the new task.
     * @param   priority
     *          The priority of the new task.
     * @param   main
     *          The main statement of the task.
     * @param   selected
     *          The position of the selected cube or null if no cube was selected.
     *
     * @post    The name will be set.
     *          | new.getName() == name
     * @post    The priority will be set.
     *          | new.getPriority() == priority
     * @post    The selected cube will be set
     *          | new.getSelectedPosition() == selected
     * @post    The schedulers will be empty
     *          | new.getSchedulers().isEmpty()
     * @post    The task won't be running
     *          | !new.isRunning()
     */
    public Task(String name, int priority, Statement main, IntVector selected) {
        this.name = name;
        this.mainStatement = main;
        this.selected = selected;
        this.setPriority(priority);

        this.running = false;

        this.schedulers = new HashSet<>();
        this.variableTable = new HashMap<>();
    }
    //</editor-fold>

    //<editor-fold desc="Properties">
    /**
     * Returns the selected position or null.
     */
    @Basic @Immutable
    public IntVector getSelectedPosition() {
        return selected;
    }

    /**
     * Returns the name of the task.
     */
    @Basic @Immutable
    public String getName() {
        return name;
    }

    @Basic @Immutable @Model
    private Statement getMainStatement() {
        return this.mainStatement;
    }
    //</editor-fold>

    /**
     * Returns true when the tasks statement is well formed.
     * Only checks break statement as type correctness is checked at construction of statements.
     *
     * @return  True if the task is well formed.
     *          | ... TODO
     */
    public boolean isWellFormed() {
        BreakChecker breakChecker = new BreakChecker();
        getMainStatement().isValid(breakChecker);
        return breakChecker.isValid();
    }

    //<editor-fold desc="Schedulers">
    /**
     * Adds a scheduler to the list of schedulers that can assign this task.
     *
     * @param   scheduler
     *          The scheduler.
     *
     * @post    The scheduler will be in the list of schedulers.
     *          | new.getSchedulers().contains(scheduler)
     */
    public void addScheduler(Scheduler scheduler) {
        this.schedulers.add(scheduler);
    }

    /**
     * Returns the list of schedulers controlling this task.
     */
    @Basic
    public Set<Scheduler> getSchedulers() {
        return new HashSet<>(schedulers);
    }
    //</editor-fold>

    //<editor-fold desc="Unit">
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
    //</editor-fold>

    //<editor-fold desc="Priority">
    /**
     * Returns the priority of the task.
     */
    @Basic
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     *
     * @param   priority
     *          The new priority.
     *
     * @post    The priority will be set.
     *          | new.getPriority() == priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Compares the priority of two tasks.
     *
     * @param   o
     *          The other task.
     *
     * @return  An integer that is positive if this unit has a lower priority than the other unit
     *          | result == o.getPriority() - this.getPriority()
     */
    @Override
    public int compareTo(Task o) {
        return o.getPriority() - this.getPriority();
    }
    //</editor-fold>

    //<editor-fold desc="Running">
    /**
     * Runs the task for a certain amount of cycles.
     *  TODO
     * @param   time
     *          | The time the task may run for.
     */
    public void runFor(double time) {
        this.running = true;
        try {
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

        } catch (TaskInterruptException err) {
            this.interrupt();
            System.out.println(err.getMessage());
        } catch (TaskErrorException | BreakException err) {
            this.finish();
            System.out.println(err.getMessage());
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
     *
     * @post    The task will stop running
     *          | !new.isRunning()
     */
    public void await() {
        this.running = false;
    }

    /**
     * Resets the task to the start state.
     *
     * @effect  Resets the main statement.
     *          | this.getMainStatement().reset()
     */
    public void reset() {
        this.getMainStatement().reset();
    }

    /**
     * Finish the current task, clearing the assigned unit and removing it from all schedulers.
     *
     * @post    This task will not be assigned.
     *          | !new.isAssigned() && !this.getAssignedUnit().hasAssignedTask()
     * @post    This task won't have any schedulers
     *          | new.getSchedulers().isEmpty()
     *
     * @effect  The execution will be stopped and reset.
     * @effect  All schedulers will have this task finished.
     *          | for (Scheduler s : this.getSchedulers())
     *          |   s.finishTask(this)
     */
    public void finish() {
        if (this.isAssigned()) {
            this.await();
            this.reset();
            this.getAssignedUnit().assignTask(null);
        }
        this.setAssignedUnit(null);

        schedulers.forEach(s -> s.finishTask(this));
        this.schedulers.clear();
    }

    /**
     * Interrupt this task, making it available for execution by another unit.
     *
     * @post    This task will not be assigned.
     *          | !new.isAssigned() && !this.getAssignedUnit().hasAssignedTask()
     * @post    This tasks priority will be decreased by one.
     *          | new.getPriority() == this.getPriority() - 1
     *
     * @effect  The execution will be stopped and reset.
     */
    public void interrupt() {
        if (this.isAssigned()) {
            this.await();
            this.reset();
            this.getAssignedUnit().assignTask(null);
        }

        setAssignedUnit(null);
        this.setPriority(this.getPriority() - 1);
        schedulers.forEach(s -> s.rebuildTask(this));
    }
    //</editor-fold>

    //<editor-fold desc="Variables">
    /**
     * Returns the Object associated with the variable;
     *
     * @param   variable
     *          The name of the variable.
     */
    @Basic
    public Object getVariable(String variable) {
        return variableTable.get(variable);
    }

    /**
     * Sets the variable to the given value.
     *
     * @param   variable
     *          The name of the variable.
     * @param   value
     *          The new value of the variable.
     *
     * @post    The value will be set.
     *          | new.getVariable(variable) == value
     */
    public void setVariable(String variable, Object value) {
        this.variableTable.put(variable, value);
    }
    //</editor-fold>
}
