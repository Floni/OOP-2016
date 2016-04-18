package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.vector.IntVector;

/**
 * Created by timo on 4/14/16.
 */
public class LogPositionExpression implements PositionExpression {
    @Override
    public IntVector getValue(Task task) {
        return task.getAssignedUnit().getWorld().getLogs().iterator().next().getPosition().toIntVector();
    }
}
