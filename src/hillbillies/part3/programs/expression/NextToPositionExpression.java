package hillbillies.part3.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import javafx.geometry.Pos;

/**
 * Created by timo on 4/14/16.
 */
public class NextToPositionExpression implements PositionExpression {

    private final PositionExpression pos;

    public NextToPositionExpression(PositionExpression pos) {
        this.pos = pos;
    }

    @Override
    public IntVector getValue(Task task) {
        Unit unit = task.getAssignedUnit();
        // TODO: check
        return World.getNeighbours(pos.getValue(task)).filter(p -> unit.isValidPosition(p)
                && unit.isStablePosition(p)).findAny().orElse(IntVector.ZERO);
    }
}
