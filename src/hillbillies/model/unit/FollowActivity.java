package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
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
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     *
     * @effect  Reset the activity.
     *          | this.reset()
     */
    FollowActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
    }

    /**
     * TODO
     * @param   dt
     *          The given time step.
     */
    @Override
    void advanceTime(double dt) {
        super.advanceTime(dt);
        //if (/* TODO position exactly on half*/) {
            if (this.isAtTarget() || !other.isAlive()) {
                this.updateTarget(getUnit().getPosition().toIntVector());
            } else if (!other.getPosition().toIntVector().equals(this.target)) {
                this.updateTarget(other.getPosition().toIntVector());
            }
        //}
    }

    /**
     * TODO
     * @param other
     *
     * @throws UnreachableTargetException
     */
    public void setOther(Unit other) throws UnreachableTargetException {
        // TODO: other must be effective & alive otherwise throw InvalidUnitException
        this.other = other;
        updateTarget(other.getPosition().toIntVector());
    }

    /**
     * The activity is only controllable by default behaviour so the default behaviour can always switch activities.
     * @return  Always returns true
     *          | result == true
     */
    @Override
    boolean canSwitch(){
        return true;
    }

    @Override
    void reset() {
        super.reset();
        other = null;
    }
}