package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Created by florian on 14/04/2016.
 */
public class CarriesItemExpression implements BooleanExpression {

    private final UnitExpression unit;

    public CarriesItemExpression(UnitExpression unit) {
        this.unit = unit;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return unit.getValue(task).isCarryingLog() || unit.getValue(task).isCarryingBoulder();
    }
}
