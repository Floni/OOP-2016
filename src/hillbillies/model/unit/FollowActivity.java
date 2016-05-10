package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.Terrain;
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
     * Updates the follow activity for the given time step.
     *
     * @param   dt
     *          The given time step.
     *
     * @post    If the other unit exists and the unit is standing in the centre of a cube
     * @post    then if the unit is standing on a position next to the other unit or the other unit is dead,
     *          the unit will finish following the other unit.
     * @post    Else if the the unit is not standing on the same cube as the other unit,
     *          the unit will update his target to the cube of the other unit.
     *
     * @effect  Advances the parent move activity of this follow activity
     *          | super.advanceTime(dt)
     */
    @Override
    void advanceTime(double dt) {
        if (getOther() != null &&
                getUnit().getPosition().subtract(getUnit().getPosition().toIntVector().toVector())
                        .isEqualTo(new Vector(Terrain.Lc / 2, Terrain.Lc / 2, Terrain.Lc / 2), World.POS_EPS)) {
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
     * Sets the unit to follow and starts the following
     *
     * @param   other
     *          The unit to follow
     *
     * @effect  Updates the target to follow.
     *          | updateTarget(ot
     *
     * @throws  UnreachableTargetException
     *          Throws if the other unit is null or not alive.
     *          | other == null || !other.isAlive()
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