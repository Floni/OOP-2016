package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.expression.PositionExpression;

/**
 * Created by timo on 4/14/16.
 */
public class WorkStatement implements Statement, ActivityTracker {

    private final PositionExpression position;
    private boolean done;
    private boolean interrupted;

    public WorkStatement(PositionExpression pos) {
        this.position = pos;
    }

    @Override
    public void reset() {
        done = false;
        interrupted = false;
    }

    @Override
    public boolean isDone(Task task) {
        return done;
    }

    @Override
    public void execute(Task task) throws TaskInterruptException {
        if (this.interrupted)
            throw new TaskInterruptException("work was interrupted");

        task.getAssignedUnit().workAt(this.position.getValue(task));
        task.getAssignedUnit().setActivityTracker(this);
        task.await();
    }

    @Override
    public void isValid(BreakChecker breakChecker) {
        // NOP
    }

    @Override
    public void setDone() {
        done = true;
    }

    @Override
    public void setInterrupt() {
        interrupted = true;
    }
}
