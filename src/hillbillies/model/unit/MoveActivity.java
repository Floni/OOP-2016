package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import hillbillies.model.Terrain;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;
import hillbillies.model.World;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Deque;
import java.util.List;

/**
 * The activity for moving to either a distant cube or a neighbour.
 */
class MoveActivity extends Activity {
    private static final double SPRINT_DELAY = 0.1;
    private static final double SPRINT_CHANCE = 0.001;


    protected IntVector target; // the final target

    private Vector targetNeighbour; // the next neighbour to reach

    private double sprintStaminaTimer; // timer for sprinting

    /**
     * A stack containing the current path, may be null.
     *
     * @invar   The path is either not effective, or all elements are effective, valid.
     *          | path == null ||
     *          | (for each position in path:
     *          |       this.getUnit().getWorld().isValidPosition(position))
     */
    private Deque<IntVector> path; // the path, may be null
    private Activity pendingActivity; // the activity that is pending to be executed when we reach the next centre.

    /**
     * Creates the move activity for the given unit.
     *
     * @param   unit
     *          The unit who is conducting the activity.
     *
     * @effect  Initialize the Activity with the given unit.
     *          | super(unit)
     */
    MoveActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
    }

    /**
     * Updates the move activity for the given time step.
     *
     * @param   dt
     *          The given time step.
     *
     * @post    If default behavior is enabled the unit has a small chance to start sprinting.
     * @post    If the unit is sprinting, his stamina will drain by 1 point every .1 seconds and his speed will be doubled.
     * @post    If the unit arrives at the centre of a new cube he will receive 1 xp.
     * @post    The new position is equal to the old position + the speed vector multiplied by the time step.
     * @post    If the new position is equal to the target the unit stops moving, otherwise he will continue moving.
     *
     * @effect  Sets the units new position
     *          | unit.setPosition(newPosition)
     */
    @Override @Model
    void advanceTime(double dt) {
        if (getUnit().isSprinting()) {
            sprintStaminaTimer -= dt;
            if(sprintStaminaTimer <= 0) {
                sprintStaminaTimer += SPRINT_DELAY;
                int newStamina = getUnit().getStamina()  - 1;
                if (newStamina >= 0)
                    getUnit().setStamina(newStamina);
                if (getUnit().getStamina() == 0)
                    getUnit().setSprinting(false);
            }
        } else if (getUnit().isDefaultEnabled()) {
            if (Math.random() <= SPRINT_CHANCE && getUnit().getStamina() != 0)
                getUnit().setSprinting(true);
        }
        Vector newPosition = getUnit().getPosition().add(this.getUnit().getSpeed().multiply(dt));
        if (isAtNeighbour(newPosition)) {
            getUnit().setPosition(this.getTargetNeighbour());
            getUnit().addXp(1);
            if (getPendingActivity() != null) {
                getUnit().switchActivity(getPendingActivity());
                setPendingActivity(null);
            } else if (this.getTarget() == null || isAtTarget()) {
                getUnit().finishCurrentActivity();
            } else {
                if (path == null)
                    updateTarget(this.target);
                goToNextNeighbour();
            }
        } else {
            getUnit().setPosition(newPosition);
        }
    }

    /**
     * Returns whether the unit can switch activities, this is possible when the target is not null.
     *
     * @return  True if we aren't just moving to a neighbour.
     *          | this.getTarget() != null
     */
    @Override
    boolean canSwitch() {
        return this.getTarget() != null;
    }

    @Override
    void pause() {
        this.getUnit().setSpeed(null);
        getUnit().setSprinting(false);
    }

    @Override
    void resume() {
        if (this.getTarget() != null)
            this.updateTarget(this.getTarget());

        Vector speed = this.calculateSpeed(this.targetNeighbour);
        this.getUnit().setSpeed(speed);
        this.getUnit().setOrientation(Math.atan2(speed.getY(), speed.getX()));
    }

    /**
     * Resumes the moving activity, which does nothing since the target would only be updated.
     *
     * @post    The target is cleared.
     *          | new.getTarget() == null
     * @post    The target neightbour is cleared.
     */
    @Override @Raw
    void reset() {
        this.target = null;
        this.targetNeighbour = null;
        sprintStaminaTimer = 0;
        this.path = null;
        this.pendingActivity = null;
    }

    /**
     * Checks whether the unit has arrived at the target
     *
     * @return  True if the getUnit()'s position equals the target position.
     *          | result == this.position.isEqualTo(this.target, POS_EPS)
     */
    private boolean isAtTarget() {
        return getUnit().getPosition().toIntVector().isEqualTo(this.getTarget());
    }

    /**
     * Checks whether we arrived at the target neighbour.
     *
     * @param   newPosition
     *          The position after advanceTime
     *
     * @return  True if we are going to be further from the target in the next step
     *          | result == dist(newPosition, targetNeighbour) > dist(position, targetNeighbour)
     */
    private boolean isAtNeighbour(Vector newPosition) {
        double dist_new = newPosition.subtract(this.getTargetNeighbour()).norm();
        double dist_cur = getUnit().getPosition().subtract(this.getTargetNeighbour()).norm();
        return dist_new > dist_cur || dist_new == 0;
    }

    /**
     * Move to the next neighbour in the path and move the index of the path.
     *
     * @effect  Move to the next neighbour.
     *          | moveToNeighbour(path.get(idx))
     */
    private void goToNextNeighbour() {
        IntVector next = path.getFirst(); // examine next position
        if (!getUnit().isStablePosition(next) || !getUnit().isValidPosition(next)) {
            this.updateTarget(this.getTarget()); // recalc path
            return;
        }
        moveToNeighbour(path.pop());
    }

    /**
     * Moves to specified neighbour cube (must be next to current position).
     *
     * @param   neighbour
     *          The neighbour to move to.
     *
     * @post    The speed is calculated to move to the next cube.
     *
     * @effect  Sets the orientation of the unit in the direction of the next cube.
     *          | setOrientation(TODO)
     *
     * @throws  InvalidPositionException
     *          Throws when the neighbour position is not a valid position.
     */
    private void moveToNeighbour(IntVector neighbour) throws InvalidPositionException {
        if (!getUnit().isStablePosition(neighbour) || !getUnit().isValidPosition(neighbour))
            throw new InvalidPositionException("Invalid neighbour: ", neighbour);

        this.targetNeighbour = neighbour.toVector().add(Terrain.Lc/2);
        this.getUnit().setSpeed(calculateSpeed(this.targetNeighbour));
        getUnit().setOrientation(Math.atan2(this.getUnit().getSpeed().getY(), this.getUnit().getSpeed().getX()));
    }

    /**
     * Moves to specified neighbour cube.
     *
     * @effect  Move to the next neighbour.
     *          | moveToNeighbour(getUnit().getPosition().toIntVector().add(dx, dy, dz))
     */
    private void moveToNeighbour(int dx, int dy, int dz) throws InvalidPositionException {
        IntVector curPos = getUnit().getPosition().toIntVector();
        moveToNeighbour(curPos.add(dx, dy, dz));
    }


    /**
     * Moves to the given adjacent cube and stop pathing.
     *
     * @param   dx
     *          The x direction to move to.
     * @param   dy
     *          The y direction to move to.
     * @param   dz
     *          The z direction to move to.
     *
     * @effect  Move the unit to the given neighbour cube.
     *          | moveToNeighbour(dx, dy, dz)
     */
    void updateAdjacent(int dx, int dy, int dz) throws InvalidPositionException {
        moveToNeighbour(dx, dy, dz);
        this.path = null;
    }


    /**
     * Updates the target to move to.
     *
     * @param   newTarget
     *          The new target to move to.
     *
     * @post    The route will be recalculated.
     *
     * @effect  Move to the new next neighbour.
     *          | goToNextNeighbour().
     *
     * @throws  InvalidPositionException
     *          The given target is invalid.
     *
     * @throws  UnreachableTargetException
     *          Throws if the target can't be reached.
     */
    void updateTarget(IntVector newTarget) throws InvalidPositionException, UnreachableTargetException {
        if (!getUnit().isValidPosition(newTarget))
            throw new InvalidPositionException(newTarget);

        this.setTarget(newTarget);

        // get path:
        this.path = getUnit().getPathFinder().getPath(getUnit().getPosition().toIntVector(), this.getTarget());
        if (path == null) {
            if (getUnit().getCurrentActivity() == this)
                getUnit().finishCurrentActivity();
            throw new UnreachableTargetException();
        }

        // check if we first need to center the unit:
        this.path.push(getUnit().getPosition().toIntVector());

        // TEST:
        /*
        IntVector next = path.getFirst();
        if (!getUnit().isStablePosition(next) || !getUnit().isValidPosition(next)) {
            throw new InvalidStateException("????");
        }
        */
        // END TEST;

        goToNextNeighbour();
    }

    /**
     * Calculates the speed of the unit.
     *
     * @param   target
     *          The target position which the unit is moving to.
     *
     * @return  The result is the speed vector which would move the unit to the target.
     *          | this.getPosition().add(result.multiply(getPosition().subtract(target).norm()
     *          | / getSpeedScalar())).isEqualTo(target)
     */
    private Vector calculateSpeed(Vector target) {
        Vector diff = target.subtract(getUnit().getPosition());
        double normDiff = diff.norm();
        if (normDiff == 0)
            return Vector.ZERO;
        diff = diff.divide(normDiff);

        double vw = 1.5*(getUnit().getStrength()+getUnit().getAgility())/(2*(getUnit().getWeight()));
        if (diff.getZ() > World.POS_EPS)
            vw *= 0.5;
        else if (diff.getZ() < -World.POS_EPS)
            vw *= 1.2;

        return diff.multiply(vw);
    }


    /**
     * Sets the pending activity.
     *
     * @param   pendingActivity
     *          The pending activity to be set.
     *
     * @post    The new pending activity will be set.
     *          | new.getPendingActivity() == pendingActivity
     */
    void setPendingActivity(Activity pendingActivity) {
        this.pendingActivity = pendingActivity;
    }

    /**
     * Returns the pending activity
     */
    @Basic
    Activity getPendingActivity() {
        return this.pendingActivity;
    }


    /**
     * Returns the target the unit is moving towards.
     */
    @Basic @Model
    private IntVector getTarget() {
        return this.target;
    }

    /**
     * Sets the target to move to.
     *
     * @param   target
     *          The target to move to.
     *
     * @post    The given target will be the new target.
     *          | new.getTarget() == target
     */
    private void setTarget(IntVector target) {
        this.target = target;
    }

    /**
     * Returns the neighbour target the unit is moving towards.
     */
    @Basic @Model
    private Vector getTargetNeighbour() {
        return targetNeighbour;
    }

    /**
     *  Sets the target neighbour to move to.
     *
     * @param   targetNeighbour
     *          The neighbour target to move to.
     *
     * @post    The new neighbour target will equal the given neighbour target.
     *          | new.getTargetNeighbour() == targetNeighbour
     */
    private void setTargetNeighbour(Vector targetNeighbour) {
        this.targetNeighbour = targetNeighbour;
    }
}