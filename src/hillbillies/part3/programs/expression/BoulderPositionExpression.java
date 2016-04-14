package hillbillies.part3.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.vector.IntVector;

/**
 * Created by timo on 4/14/16.
 */
public class BoulderPositionExpression implements PositionExpression {
    @Override
    public IntVector getValue(Task task) {
        return task.getAssignedUnit().getWorld().getBoulders().iterator().next().getPosition().toIntVector();
    }
}
