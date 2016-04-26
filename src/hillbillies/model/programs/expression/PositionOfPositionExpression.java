package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.vector.IntVector;

/**
 * Class for position_of expression.
 */
public class PositionOfPositionExpression implements PositionExpression {
    private final UnitExpression other;


    public PositionOfPositionExpression(UnitExpression other) {
        this.other = other;
    }

    @Override
    public IntVector getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return other.getValue(task).getPosition().toIntVector();
    }
}
