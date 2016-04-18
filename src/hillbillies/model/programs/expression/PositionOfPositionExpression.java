package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.vector.IntVector;

/**
 * Created by timo on 4/14/16.
 */
public class PositionOfPositionExpression implements PositionExpression {
    private final UnitExpression other;


    public PositionOfPositionExpression(UnitExpression other) {
        this.other = other;
    }

    @Override
    public IntVector getValue(Task task) {
        return other.getValue(task).getPosition().toIntVector();
    }
}
