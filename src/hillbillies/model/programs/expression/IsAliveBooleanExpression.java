package hillbillies.model.programs.expression;

import hillbillies.model.Task;

/**
 * Created by florian on 14/04/2016.
 */
public class IsAliveBooleanExpression implements BooleanExpression {


    private final UnitExpression unit;


    public IsAliveBooleanExpression(UnitExpression unit) {
        this.unit = unit;
    }

    @Override
    public Boolean getValue(Task task) {
        return unit.getValue(task).isAlive();
    }
}
