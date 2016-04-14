package hillbillies.part3.programs.expression;

import hillbillies.model.Task;

/**
 * Created by florian on 14/04/2016.
 */
public class OrBooleanExpression implements BooleanExpression {

    private final BooleanExpression bool1;
    private final BooleanExpression bool2;


    public OrBooleanExpression(BooleanExpression bool1, BooleanExpression bool2) {
        this.bool1 = bool1;
        this.bool2 = bool2;
    }

    @Override
    public Boolean getValue(Task task) {
        return bool1.getValue(task) || bool2.getValue(task);
    }
}
