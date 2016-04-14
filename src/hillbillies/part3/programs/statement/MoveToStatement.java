package hillbillies.part3.programs.statement;

import hillbillies.model.Vector.IntVector;
import hillbillies.part3.programs.expression.PositionExpression;

/**
 * Created by timo on 4/13/16.
 */
public class MoveToStatement implements Statement {

    private PositionExpression expression;

    public MoveToStatement(PositionExpression position) {
        this.expression = position;
    }

    @Override
    public void execute() {
        IntVector pos = this.getExpression().getValue();
        // TODO:
    }

    public PositionExpression getExpression() {
        return expression;
    }
}