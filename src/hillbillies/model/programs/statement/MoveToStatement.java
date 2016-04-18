package hillbillies.model.programs.statement;

import hillbillies.model.Task;
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
        } catch (IllegalArgumentException err) {
            task.getAssignedUnit().stopTask();
        }
        task.await();
    }

    public PositionExpression getExpression() {
        return expression;
    }
}
