package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.World;

/**
 * Created by florian on 14/04/2016.
 */
public class IsSolidBooleanExpression implements BooleanExpression {

    private final PositionExpression pos;

    public IsSolidBooleanExpression(PositionExpression pos) {
        this.pos = pos;
    }

    @Override
    public Boolean getValue(Task task) {
        return World.isSolid(task.getAssignedUnit().getWorld().getCubeType(pos.getValue(task)));
    }
}
