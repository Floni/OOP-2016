package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for is_alive expression
 */
public class IsAliveBooleanExpression implements BooleanExpression {


    private final UnitExpression unit;


    public IsAliveBooleanExpression(UnitExpression unit) {
        this.unit = unit;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return unit.getValue(task).isAlive();
    }
}
