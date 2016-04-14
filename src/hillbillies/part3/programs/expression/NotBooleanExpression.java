package hillbillies.part3.programs.expression;

import hillbillies.model.Task;

/**
 * Created by florian on 14/04/2016.
 */
public class NotBooleanExpression implements BooleanExpression {


    private final BooleanExpression bool;

    public NotBooleanExpression(BooleanExpression bool) {
        this.bool = bool;
    }


    @Override
    public Boolean getValue(Task task) {
        return !bool.getValue(task);
    }
}
