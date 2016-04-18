package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.vector.IntVector;

/**
 * Created by florian on 14/04/2016.
 */
public class HerePositionExpression implements PositionExpression {
    @Override
    public IntVector getValue(Task task) {
        return task.getAssignedUnit().getPosition().toIntVector();
    }
}
