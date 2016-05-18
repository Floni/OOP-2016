package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.Terrain;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for is_passable expression
 */
public class IsPassableBooleanExpression implements BooleanExpression{

    private final PositionExpression pos;


    public IsPassableBooleanExpression(PositionExpression pos) {
        this.pos = pos;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        try {
            return !Terrain.isSolid(task.getAssignedUnit().getWorld().getTerrain().getCubeType(pos.getValue(task)));
        } catch (InvalidPositionException err) {
            throw new TaskErrorException(err.getMessage());
        }
    }
}
