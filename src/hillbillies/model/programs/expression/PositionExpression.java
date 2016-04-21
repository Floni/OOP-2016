package hillbillies.model.programs.expression;

import hillbillies.model.vector.IntVector;
import javafx.geometry.Pos;

/**
 * Class representing an expression returning a Position (IntVector).
 */
public interface PositionExpression extends Expression<IntVector> {
    @Override
    default PositionExpression getRead(String variable) {
        return t -> (IntVector)t.getVariable(variable);
    }
}
