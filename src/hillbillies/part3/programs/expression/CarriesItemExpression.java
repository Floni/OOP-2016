package hillbillies.part3.programs.expression;

import hillbillies.model.Task;

/**
 * Created by florian on 14/04/2016.
 */
public class CarriesItemExpression implements BooleanExpression {

    private final UnitExpression unit;

    public CarriesItemExpression(UnitExpression unit) {
        this.unit = unit;
    }

    @Override
    public Boolean getValue(Task task) {
        return unit.getValue(task).isCarryingLog() || unit.getValue(task).isCarryingBoulder();
    }
}
