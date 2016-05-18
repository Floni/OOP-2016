package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.Terrain;
import hillbillies.model.World;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;

/**
 * Class for next_to expression
 */
public class NextToPositionExpression implements PositionExpression {

    private final PositionExpression pos;

    public NextToPositionExpression(PositionExpression pos) {
        this.pos = pos;
    }

    @Override
    public IntVector getValue(Task task) throws TaskInterruptException, TaskErrorException {
        Unit unit = task.getAssignedUnit();
        World world = unit.getWorld();
        IntVector unitPos = unit.getPosition().toIntVector();

        return Terrain.getNeighbours(pos.getValue(task))
                .filter(p -> unit.isValidPosition(p)
                        && unit.isStablePosition(p)
                        && world.getPathFinder().isReachable(unitPos, p))
                .findAny().orElseThrow(() ->new TaskInterruptException("nextTo has no possible cubes"));
    }
}
