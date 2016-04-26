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
     */
    FollowActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
        this.reset();
    }

    @Override
    void advanceTime(double dt) throws InvalidPositionException, UnreachableTargetException {
        super.advanceTime(dt);
        if (unit.getPosition().toIntVector().isNextTo(this.target) || !other.isAlive()) {
            this.updateTarget(unit.getPosition().toIntVector());
        } else if (!other.getPosition().toIntVector().equals(this.target)) {
            this.updateTarget(other.getPosition().toIntVector());
        }
    }

    public void setOther(Unit other) throws InvalidPositionException, UnreachableTargetException {
        this.other = other;
        updateTarget(other.getPosition().toIntVector());
    }

    @Override @Basic
    boolean canSwitch(){
        return this.other != null; // TODO: why ?
    }

    @Override
    void reset() {
        super.reset();
        other = null;
    }
}