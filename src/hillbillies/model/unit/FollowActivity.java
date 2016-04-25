package hillbillies.model.unit;

import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.UnreachableTargetException;

/**
 * The activity for following another unit.
 */
class FollowActivity extends MoveActivity{

    private Unit other;

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
        super.advanceTime(dt);
        if (unit.getPosition().toIntVector().isNextTo(this.target.toIntVector()) || !other.isAlive()) {
            if (this.hasTracker())
                getTracker().setDone();
            unit.finishCurrentActivity(); // TODO: if nextTo move to centre of cube
        } else if (!other.getPosition().toIntVector().equals(this.target.toIntVector())) {
            this.updateTarget(other.getPosition());
        }
    }

    public void setOther(Unit other) throws InvalidPositionException, UnreachableTargetException {
        this.other = other;
        updateTarget(other.getPosition());
    }

    @Override
    boolean canSwitch(){
        return other != null; // TODO: why ?
    }

    @Override
    void reset() {
        other = null;
        super.reset();
    }
}