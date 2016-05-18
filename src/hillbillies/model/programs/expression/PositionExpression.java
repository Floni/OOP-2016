package hillbillies.model.programs.expression;

import hillbillies.model.vector.IntVector;

/**
 * Class representing an expression returning a Position (IntVector).
 */
public interface PositionExpression extends Expression<IntVector> {
    @Override
    default PositionExpression getRead(String variable) {
        return t -> (IntVector)t.getVariable(variable);
    }

    @Override
    default Expression<IntVector> castExpr(Expression<?> value) {
        return (PositionExpression)value;
    }
}
