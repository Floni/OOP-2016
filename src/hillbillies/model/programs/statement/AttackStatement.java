package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.expression.UnitExpression;

/**
 * Class for attack
 */
public class AttackStatement implements Statement, ActivityTracker {

    private final UnitExpression unitExpr;

    private boolean done;
    private boolean interrupted;

    public AttackStatement(UnitExpression target) {
        this.unitExpr = target;
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
    public void execute(Task task) {
        if (interrupted)
            throw new TaskInterruptException("attack was interrupted, shouldn't happen");
        try {
            task.getAssignedUnit().attack(this.unitExpr.getValue(task));
            task.getAssignedUnit().setActivityTracker(this);
        } catch (InvalidActionException | InvalidUnitException err) {
            throw new TaskInterruptException(err.getMessage());
        }
        task.await();
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
