package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.World;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.vector.Vector;

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
     *          | super(unit)
     */
    FollowActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
    }

    /**
     * TODO
     *
     * @param dt The given time step.
     */
    @Override
    void advanceTime(double dt) {
        if (getOther() != null && getUnit().getPosition().subtract(getUnit().getPosition().toIntVector().toVector()).isEqualTo(new Vector(World.Lc / 2, World.Lc / 2, World.Lc / 2), Unit.POS_EPS)) {
            if (this.getUnit().getPosition().isNextTo(getOther().getPosition()) || !other.isAlive()) {
                this.finishActivity();
                return;
            } else if (!other.getPosition().toIntVector().equals(this.target)) {
                this.updateTarget(other.getPosition().toIntVector());
            }
        }
        super.advanceTime(dt);
    }

    /**
     * TODO
     * @param other
     *
     * @throws UnreachableTargetException
     */
    public void setOther(Unit other) throws UnreachableTargetException, InvalidUnitException {
        if (other == null || !other.isAlive())
            throw new InvalidUnitException("The other unit is not valid");
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

    public Unit getOther() {
        return other;
    }
}