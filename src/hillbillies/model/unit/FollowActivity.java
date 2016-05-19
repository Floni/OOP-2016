package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
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
     *          then if the unit is standing on a position next to the other unit or the other unit is dead,
     *          the unit will finish following the other unit.
     * @post    Else if the the unit is not standing on the same cube as the other unit,
     *          the unit will update his target to the cube of the other unit.
     *
     * @effect  Advances the parent move activity of this follow activity
     *          | super.advanceTime(dt)
     */
    @Override @Model
    void advanceTime(double dt) {
        if (getOther() != null &&
                getUnit().getPosition().subtract(getUnit().getPosition().toIntVector().toVector())
                        .isEqualTo(new Vector(Terrain.Lc / 2, Terrain.Lc / 2, Terrain.Lc / 2), World.POS_EPS)) {
            if (this.getUnit().getPosition().isNextTo(getOther().getPosition()) || !other.isAlive()) {
                getUnit().finishCurrentActivity();
                return;
            } else if (!other.getPosition().toIntVector().equals(this.getTarget())) {
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
     * @post    Sets the other unit.
     *          | new.getOther() == other
     *
     * @effect  Updates the target to follow.
     *          | this.updateTarget(other.getPosition().toIntVector())
     *
     * @throws  InvalidUnitException
     *          Throws if the other unit is null or not alive.
     *          | other == null || !other.isAlive()
     */
    @Model
    void setOther(Unit other) throws InvalidUnitException, UnreachableTargetException, InvalidPositionException {
        if (other == null || !other.isAlive())
            throw new InvalidUnitException("The other unit is not valid");
        this.other = other;
        updateTarget(other.getPosition().toIntVector());
    }

    /**
     * The activity is only controllable by default behaviour so the default behaviour can always switch activities.
     *
     * @return  Always returns true
     *          | result == true
     */
    @Override
    boolean canSwitch(){
        return true;
    }

    /**
     * Resets the current activity.
     *
     * @post    Clear the other unit.
     *          | new.getOther() == null
     *
     * @effect  Call the MoveActivity's reset.
     *          | super.reset()
     */
    @Override @Raw
    void reset() {
        super.reset();
        other = null;
    }

    /**
     * Returns the other unit the unit is following.
     */
    @Basic @Model
    private Unit getOther() {
        return other;
    }
}