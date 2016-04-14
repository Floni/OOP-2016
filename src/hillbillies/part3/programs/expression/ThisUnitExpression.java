package hillbillies.part3.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.unit.Unit;

/**
 * Created by florian on 14/04/2016.
 */
public class ThisUnitExpression implements UnitExpression {


    @Override
    public Unit getValue(Task task) {
        return task.getAssignedUnit();
    }
}
