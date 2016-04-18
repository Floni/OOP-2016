package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.vector.IntVector;

/**
 * Created by timo on 4/14/16.
 */
public class SelectedPositionExpression implements PositionExpression {
    @Override
    public IntVector getValue(Task task) {
        return task.getSelectedPosition();
    }
}
