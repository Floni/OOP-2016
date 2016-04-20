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
public class MoveToStatement implements Statement {
    private final PositionExpression expression;
    private IntVector target;

    public MoveToStatement(PositionExpression position) {
        this.expression = position;
    }

    @Override
    public void reset() {
        this.target = null;
    }

    @Override
    public boolean isDone(Task task) {
        return task.getAssignedUnit().getPosition().toIntVector().equals(this.target);
    }

    @Override
    public void execute(Task task) {
        if (this.target == null) {
            this.target = this.getExpression().getValue(task);
            if (this.target == null)
                return;
        }

        try {
            task.getAssignedUnit().moveTo(this.target);
        } catch (UnreachableTargetException | InvalidActionException | InvalidPositionException err) {
            if (!task.getAssignedUnit().getWorld().isValidPosition(this.target))
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
}
