package hillbillies.model.programs.expression;

import hillbillies.model.vector.IntVector;
import javafx.geometry.Pos;

/**
 * Created by timo on 4/13/16.
 */
public interface PositionExpression extends Expression<IntVector> {
    @Override
    default PositionExpression getRead(String variable) {
        return t -> t.getPositionVariable(variable);
    }
}
