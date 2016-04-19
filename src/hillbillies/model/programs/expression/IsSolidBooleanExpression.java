package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Created by florian on 14/04/2016.
 */
public class IsSolidBooleanExpression implements BooleanExpression {

    private final PositionExpression pos;

    public IsSolidBooleanExpression(PositionExpression pos) {
        this.pos = pos;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return World.isSolid(task.getAssignedUnit().getWorld().getCubeType(pos.getValue(task)));
    }
}
