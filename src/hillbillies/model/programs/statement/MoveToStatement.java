package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.vector.IntVector;
import hillbillies.model.programs.expression.PositionExpression;

/**
 * Created by timo on 4/13/16.
 *
 */
public class MoveToStatement implements Statement, ActivityTracker {
    private final PositionExpression expression;
    private boolean done;
    private boolean interrupted;

    public MoveToStatement(PositionExpression position) {
        this.expression = position;
    }

    @Override
    public void reset() {
        this.done = false;
        this.interrupted = false;
    }

    @Override
    public boolean isDone(Task task) {
        return this.done;
    }

    @Override
    public void execute(Task task) {
        if (this.interrupted)
            throw new TaskInterruptException("moveTo was interrupted");

        IntVector target = getExpression().getValue(task);
        try {
            task.getAssignedUnit().moveTo(target);
            task.getAssignedUnit().setActivityTracker(this);
        } catch (UnreachableTargetException | InvalidActionException | InvalidPositionException err) {
            if (!task.getAssignedUnit().getWorld().isValidPosition(target))
                throw new TaskErrorException(err.getMessage());
            else
                throw new TaskInterruptException(err.getMessage());
        }

        task.await();
    }

    @Override
    public void isValid(BreakChecker breakChecker) {
        // NOP
    }

    private PositionExpression getExpression() {
        return expression;
    }

    @Override
    public void setDone() {
        this.done = true;
    }

    @Override
    public void setInterrupt() {
        this.interrupted = true;
    }
}
