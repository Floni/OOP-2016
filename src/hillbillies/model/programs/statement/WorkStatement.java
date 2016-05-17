package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.expression.PositionExpression;

/**
 * Class for work.
 */
public class WorkStatement implements Statement, StateTracker {

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

        try {
            task.getAssignedUnit().workAt(this.position.getValue(task));
            task.getAssignedUnit().setTracker(this);
        } catch (InvalidActionException | InvalidPositionException err) {
            throw new TaskInterruptException(err.getMessage());
        }
        task.await();
    }

    @Override
    public void setDone() {
        this.done = true;
    }
}
