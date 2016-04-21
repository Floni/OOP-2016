package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for not boolean expression
 */
public class NotBooleanExpression implements BooleanExpression {


    private final BooleanExpression bool;

    public NotBooleanExpression(BooleanExpression bool) {
        this.bool = bool;
    }


    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return !bool.getValue(task);
    }
}
