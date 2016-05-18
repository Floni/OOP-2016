package hillbillies.model.programs.expression;

import hillbillies.model.Task;

/**
 * Class for carries item expression
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
