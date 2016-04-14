package hillbillies.part3.programs.expression;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.Task;
import hillbillies.model.Vector.IntVector;

/**
 * Created by florian on 14/04/2016.
 */
public class LiteralPositionExpression implements PositionExpression {

    private IntVector value;

    public LiteralPositionExpression(int x, int y, int z) {
        this.value = new IntVector(x, y, z);
    }

    @Override @Basic
    public IntVector getValue(Task task) {
        return value;
    }
}
