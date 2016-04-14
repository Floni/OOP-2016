package hillbillies.part3.programs.expression;

import hillbillies.model.Task;

/**
 * Created by florian on 14/04/2016.
 */
public class FalseBooleanExpression implements BooleanExpression {
    @Override
    public Boolean getValue(Task task) {
        return false;
    }
}
