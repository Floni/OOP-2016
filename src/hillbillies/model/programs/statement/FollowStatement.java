package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.expression.UnitExpression;

/**
 * Class for the follow statement.
 */
public class FollowStatement implements Statement, StateTracker {

    private final UnitExpression unitExpr;

    private boolean done;
    private boolean interrupted;

    public FollowStatement (UnitExpression other) {
        this.unitExpr = other;
    }

    @Override
    public void reset() {
        done = false;
        interrupted = false;
    }

    @Override
    public boolean isDone(Task task) {
        return this.done;
    }

    @Override
    public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
        if (this.interrupted)
            throw new TaskInterruptException("Following interrupted");
        try {
            task.getAssignedUnit().follow(unitExpr.getValue(task));
            task.getAssignedUnit().setTracker(this);
        } catch (InvalidActionException | InvalidUnitException | UnreachableTargetException err) {
            throw new TaskInterruptException(err.getMessage());
        }
        task.await();
    }

    @Override
    public void setDone() {
        this.done = true;
    }
}
