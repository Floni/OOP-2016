package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
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
    static final double SPRINT_DELAY = 0.1;

    protected IntVector target; // the final target
    private Vector targetNeighbour; // the next neighbour to reach
    protected Vector speed; // the speed at which we're going, doesn't use sprinting

    double sprintStaminaTimer; // timer for sprinting
    boolean sprinting; // true if we are sprinting

    private Deque<IntVector> path; // the path, may be null
    private Activity pendingActivity; // the activity that is pending to be executed when we reach the next centre.

    /**
     * Initializes the move activity for the given unit.
     *
     * @param   unit
     *          The unit that starts moving.
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     */
    MoveActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
        this.reset();
    }

    /**
     * Updates the move activity for the given time step
     *
     * @param   dt
     *          The given time step.
     *
     * @post    If default behavior is enabled the unit has a small chance to start sprinting.
     * @post    If the unit is sprinting, his stamina will drain by 1 point every .1 seconds and his speed will be doubled.
     * @post    If the unit arrives at the centre of a new cube he will receive 1 xp.
     * @post    The new position is equal to the old position + the speed multiplied by the time step.
     * @post    If the new new position is equal to the target the unit stops moving, otherwise he will continue moving.
     *
     * @effect  Sets the units new position
     *          | unit.setPosition(newPosition)
     */
    @Override
    void advanceTime(double dt) {
        if (this.sprinting) {
            sprintStaminaTimer -= dt;
            if(sprintStaminaTimer <= 0) {
                sprintStaminaTimer += SPRINT_DELAY;
                int newStamina = unit.getStamina()  - 1;
                if (newStamina >= 0)
                    unit.setStamina(newStamina);
                if (unit.getStamina() == 0)
                    this.sprinting = false;
            }
        } else if (unit.isDefaultEnabled()) {
            // TODO: fix with timer? or just once while moving?
            if (Math.random() >= 0.9999 && unit.getStamina() != 0)
                this.sprinting = true;
        }
        Vector newPosition = unit.getPosition().add(this.speed.multiply(this.sprinting ? 2*dt : dt));
        if (isAtNeighbour(newPosition)) {
            unit.setPosition(this.targetNeighbour);
            unit.addXp(1);
            if (getPendingActivity() != null) {
                unit.switchActivity(getPendingActivity());
                setPendingActivity(null);
            } else if (this.target == null || isAtTarget()) {
                if (this.hasTracker())
                    this.getTracker().setDone();
                unit.finishCurrentActivity();
            } else {
                if (path == null)
                    updateTarget(this.target);
                goToNextNeighbour();
            }
        } else {
            unit.setPosition(newPosition);
        }
    }

    /**
     * Returns whether the unit can switch activities, this is possible when the target is not null.
     */
    @Override @Basic
    boolean canSwitch() {
        return this.target != null;
    }

    /**
     * Resumes the moving activity, which does nothing since the target would only be updated.
     */
    @Override @Raw
    void reset() {
        this.target = null;
        this.targetNeighbour = null;
        this.speed = null;
        sprintStaminaTimer = 0;
        sprinting = false;
        this.path = null;
        this.pendingActivity = null;

        if (this.hasTracker())
            this.getTracker().setInterrupt();
        this.resetTracker();
    }

    /**
     * Checks whether the unit has arrived at the target
     *
     * @return  True if the unit's position equals the target position.
     *          | result == this.position.isEqualTo(this.target, POS_EPS)
     */
    boolean isAtTarget() {
        return unit.getPosition().toIntVector().isEqualTo(this.target);
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
    boolean isAtNeighbour(Vector newPosition) {
        double dist_new = newPosition.subtract(this.targetNeighbour).norm();
        double dist_cur = unit.getPosition().subtract(this.targetNeighbour).norm();
        return dist_new > dist_cur;
    }

    /**
     * Move to the next neighbour in the path and move the index of the path.
     *
     * @effect  Move to the next neighbour.
     *          | moveToNeighbour(path.get(idx))
     */
    private void goToNextNeighbour() {
        IntVector next = path.getFirst(); // examine next position
        if (!unit.isStablePosition(next) || !unit.isValidPosition(next)) {
            this.updateTarget(this.target); // recalc path
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
     *          | setOrientation()
     *
     * @throws  InvalidPositionException
     *          Throws when the neighbour position is not a valid position.
     */
    private void moveToNeighbour(IntVector neighbour) throws InvalidPositionException {
        if (!unit.isStablePosition(neighbour) || !unit.isValidPosition(neighbour))
            throw new InvalidPositionException("Invalid neighbour: ", neighbour);

        this.targetNeighbour = neighbour.toVector().add(World.Lc/2);
        this.speed = calculateSpeed(this.targetNeighbour);
        unit.setOrientation(Math.atan2(this.speed.getY(), this.speed.getX()));
    }

    /**
     * Moves to specified neighbour cube.
     *
     * @effect  Move to the next neighbour.
     *          | moveToNeighbour(curPos.add(dx, dy, dz))
     */
    private void moveToNeighbour(int dx, int dy, int dz) throws InvalidPositionException {
        IntVector curPos = unit.getPosition().toIntVector();
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

        this.target = newTarget;

        // get path:
        this.path = unit.getPathFinder().getPath(unit.getPosition().toIntVector(), newTarget);
        if (path == null) {
            unit.finishCurrentActivity();
            throw new UnreachableTargetException();
        }

        // check if we first need to center the unit:
        if (!unit.getPosition().subtract(
                unit.getPosition().toIntVector().toVector()).isEqualTo(new Vector(0.5, 0.5, 0.5), Unit.POS_EPS))
            this.path.push(unit.getPosition().toIntVector());

        if (path.size() == 0) {
            unit.finishCurrentActivity();
            throw new UnreachableTargetException();
        }

        // TEST:
        IntVector next = path.getFirst();
        if (!unit.isStablePosition(next) || !unit.isValidPosition(next)) {
            throw new InvalidStateException("????"); //TODO: test & fix
        }
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
        Vector diff = target.subtract(unit.getPosition());
        diff = diff.divide(diff.norm());

        double vw = 1.5*(unit.getStrength()+unit.getAgility())/(2*(unit.getWeight()));
        if (diff.getZ() > Unit.POS_EPS)
            vw *= 0.5;
        else if (diff.getZ() < -Unit.POS_EPS)
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
}