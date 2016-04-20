package hillbillies.model.programs.expression;

import hillbillies.model.unit.Unit;

/**
 * Created by florian on 14/04/2016.
 */
public interface UnitExpression extends Expression<Unit> {
    @Override
    default UnitExpression getRead(String variable) {
        return t -> t.getUnitVariable(variable);
    }
}
