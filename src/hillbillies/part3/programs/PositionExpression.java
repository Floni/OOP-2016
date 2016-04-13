package hillbillies.part3.programs;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.vector.IntVector;

/**
 * Created by timo on 4/13/16.
 */
public class PositionExpression implements Expression<IntVector> {

    private IntVector value;

    public PositionExpression(int x, int y, int z) {
        this.value = new IntVector(x, y, z);
    }

    @Override @Basic
    public IntVector getValue() {
        return value;
    }
}
