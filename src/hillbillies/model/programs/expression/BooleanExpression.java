package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Created by florian on 14/04/2016.
 */
public interface BooleanExpression extends Expression<Boolean> {
    @Override
    default BooleanExpression getRead(String variable) {
        return t -> t.getBooleanVariable(variable);
    }
}
