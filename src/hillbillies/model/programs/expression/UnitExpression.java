package hillbillies.model.programs.expression;

import hillbillies.model.unit.Unit;

/**
 * Class representing an expression returning an Unit.
 */
public interface UnitExpression extends Expression<Unit> {
    @Override
    default UnitExpression getRead(String variable) {
        return t -> (Unit)t.getVariable(variable);
    }
}
