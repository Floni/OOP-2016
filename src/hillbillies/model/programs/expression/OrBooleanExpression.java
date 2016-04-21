package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for or expression
 */
public class OrBooleanExpression implements BooleanExpression {

    private final BooleanExpression bool1;
    private final BooleanExpression bool2;


    public OrBooleanExpression(BooleanExpression bool1, BooleanExpression bool2) {
        this.bool1 = bool1;
        this.bool2 = bool2;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return bool1.getValue(task) || bool2.getValue(task);
    }
}
