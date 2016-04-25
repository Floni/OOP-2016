package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

/**
 * The activity for following another unit.
 */
class FollowActivity extends MoveActivity{

    private Unit other;
    private IntVector pos;

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
    void advanceTime(double dt) throws InvalidPositionException, UnreachableTargetException {
        if (unit.getPosition().isNextTo(other.getPosition()) || other.getPosition() == unit.getPosition() || !other.isAlive()) {
            if (this.hasTracker())
                this.getTracker().setDone();
            unit.finishCurrentActivity();
        } else {
            if ((!getOtherPos().equals(this.target) || this.target == null) && ((unit.getPosition().toIntVector().equals(pos)) || pos == null)) {
                pos = getPos();
                this.updateTarget(getOtherPos());
                super.advanceTime(dt);
            } else {
                super.advanceTime(dt);
            }
        }
    }

    public void setOther(Unit other) throws InvalidPositionException, UnreachableTargetException {
        this.other = other;
        updateTarget(other.getPosition());
    }

    private Vector getOtherPos() {
        return other.getPosition();
    }

    private IntVector getPos(){
        return unit.getPosition().toIntVector();
    }

    @Override @Basic
    boolean canSwitch(){
        return other != null;
    }

    @Override
    void reset() {
        other = null;
        if (this.hasTracker())
            this.getTracker().setInterrupt();
        this.resetTracker();
        super.reset();
    }
}
