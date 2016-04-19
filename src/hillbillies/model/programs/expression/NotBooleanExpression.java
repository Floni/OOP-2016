package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Created by florian on 14/04/2016.
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
