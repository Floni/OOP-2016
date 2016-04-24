package hillbillies.model.unit;

import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

/**
 * The activity for following another unit.
 */
public class FollowActivity extends MoveActivity{

    private Unit other;
    private Vector otherPos;

    /**
     * Initializes the move activity for the given unit.
     *
     * @param   unit
     *          The unit that starts following.
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     */
    FollowActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
        this.reset();

    }

    @Override
    void advanceTime(double dt) {
        if (isAtNeighbour(other.getPosition()) || other.getPosition() == unit.getPosition() || !other.isAlive()) {
            unit.finishCurrentActivity();
        } else {
            if (getOtherPos() != otherPos || otherPos == null) {
                otherPos = getOtherPos();
                this.updateTarget(otherPos);
                super.advanceTime(dt);
            } else {
                super.advanceTime(dt);
            }
        }
    }

    public void setOther(Unit other) {
        this.other = other;
    }

    private Vector getOtherPos() {
        return other.getPosition();
    }
}
