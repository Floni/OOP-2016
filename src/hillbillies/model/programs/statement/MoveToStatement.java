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
 * Class for moveTo
 */
public class MoveToStatement implements Statement {
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
        } catch (UnreachableTargetException | InvalidActionException | InvalidPositionException err) {
            if (!task.getAssignedUnit().getWorld().getTerrain().isValidPosition(target))
                throw new TaskErrorException(err.getMessage());
            else
                throw new TaskInterruptException(err.getMessage());
        }

        task.await();
    }

    private PositionExpression getExpression() {
        return expression;
    }
}
