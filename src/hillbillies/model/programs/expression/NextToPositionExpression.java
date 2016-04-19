package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by timo on 4/14/16.
 */
public class NextToPositionExpression implements PositionExpression {

    private final PositionExpression pos;

    public NextToPositionExpression(PositionExpression pos) {
        this.pos = pos;
    }

    @Override
    public IntVector getValue(Task task) throws TaskInterruptException, TaskErrorException {
        Unit unit = task.getAssignedUnit();
        // TODO: check
        List<IntVector> possible = World.getNeighbours(pos.getValue(task)).filter(p -> unit.isValidPosition(p)
                && unit.isStablePosition(p)).collect(Collectors.toList());

        if (possible.isEmpty())
            throw new TaskInterruptException("nextTo has no possible cubes");

        return possible.get((int)(Math.random()*possible.size()));
    }
}
