package hillbillies.model.Unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;

import java.util.List;

/**
 * Created by timo on 3/17/16.
 *
 */
class MoveActivity extends Activity {
    public static final double SPRINT_DELAY = 0.1;

    protected Vector target;
    private Vector targetNeighbour;
    protected Vector speed;

    double sprintStaminaTimer;
    boolean sprinting;

    private List<IntVector> path;
    private int idx;

    /**
     * Initializes the move activity for the given unit.
     *
     * @param   unit
     *          The unit that starts moving.
     */
    protected MoveActivity(Unit unit) {
        super(unit);
    }

    /**
     * Makes the unit move to the given location.
     *
     * @param   unit
     *          The unit that moves.
     * @param   target
     *          The location that the unit moves to.
     *
     * @effect  Updates the target of the unit.
     *          | updateTarget(target)
     */
    public MoveActivity(Unit unit, Vector target) {
        super(unit);
        updateTarget(target);

    }

    /**
     * Moves the unit to the given neighbouring position.
     *
     * @param   unit
     *          The unit who moves.
     * @param   dx
     *          The x component of the target direction.
     * @param   dy
     *          The y component of the target direction.
     * @param   dz
     *          The z component of the target direction.
     *
     * @effect  Moves the unit to the neighbouring cube.
     *          | moveToNeighbour(dx, dy, dz)
     */
    public MoveActivity(Unit unit, int dx, int dy, int dz) throws IllegalArgumentException {
        super(unit);
        moveToNeighbour(dx, dy, dz);
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
     */
    @Override
    void advanceTime(double dt) {
        double mod = 1;
        if (this.sprinting) {
            sprintStaminaTimer -= dt;
            mod = 2;
            if(sprintStaminaTimer <= 0) {
                sprintStaminaTimer += SPRINT_DELAY;
                int newStamina = unit.getStamina()  - 1;
                if (newStamina >= 0)
                    unit.setStamina(newStamina);
                if (unit.getStamina() == 0) {
                    mod = 1;
                    this.sprinting = false;
                }
            }
        } else if (unit.isDefaultEnabled()) {
            // fix with timer? or just once while moving?
            if (Math.random() >= 0.9999 && unit.getStamina() != 0)
                this.sprinting = true;
        }

        Vector newPosition = unit.getPosition().add(this.speed.multiply(mod*dt));
        if (isAtNeighbour(newPosition)) {
            unit.addXp(1);
            unit.setPosition(this.targetNeighbour);
            if (!unit.getPendingActivity().equalsClass(NoneActivity.class)) {
                unit.clearPendingActivity();
                unit.setLastActivity(this);
            } else if (this.target == null || isAtTarget()) {
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
     * Move to the next neighbour in the path and move the index of the path.
     *
     * @effect  Move to the next neighbour.
     *          | moveToNeighbour(path.get(idx))
     */
    private void goToNextNeighbour() {
        IntVector next = path.get(idx);
        if (!unit.isStablePosition(next) || !unit.isValidPosition(next)) {
            this.updateTarget(this.target); // recalc path
            return;
        }
        moveToNeighbour(path.get(idx));
        idx -= 1;
    }

    /**
     * Returns whether the unit can switch activities, this is possible when the target is not null.
     */
    @Override @Basic
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return this.target != null;
    }

    /**
     * Resumes the moving activity, which does nothing since the target would only be updated.
     */
    @Override
    public void resume() {

    }

    /**
     * Checks whether the unit has arrived at the target
     *
     * @return  True if the unit's position equals the target position.
     *          | result == this.position.isEqualTo(this.target, POS_EPS)
     */
    private boolean isAtTarget() {
        return unit.getPosition().isEqualTo(this.target, Unit.POS_EPS);
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
        double dist_new = newPosition.subtract(this.targetNeighbour).norm();
        double dist_cur = unit.getPosition().subtract(this.targetNeighbour).norm();
        return dist_new > dist_cur;
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
     * @throws  IllegalArgumentException
     *          Throws when the neighbour position is not a valid position.
     */
    private void moveToNeighbour(IntVector neighbour) throws IllegalArgumentException {
        this.targetNeighbour = neighbour.toVector().add(World.Lc/2);

        if (!unit.isStablePosition(this.targetNeighbour.toIntVector()) ||
                !unit.isValidPosition(this.targetNeighbour.toIntVector()))
            throw new IllegalArgumentException("Illegal neighbour");

        this.speed = calculateSpeed(this.targetNeighbour);
        unit.setOrientation(Math.atan2(this.speed.getY(), this.speed.getX()));
    }

    /**
     * Moves to specified neighbour cube.
     *
     * @effect  Move to the next neighbour.
     *          | moveToNeighbour(curPos.add(dx, dy, dz))
     */
    private void moveToNeighbour(int dx, int dy, int dz) throws IllegalArgumentException {
        IntVector curPos = unit.getPosition().toIntVector();
        moveToNeighbour(curPos.add(dx, dy, dz));
    }


    /**
     * Moves to the given adjacent cube.
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
    void updateAdjacent(int dx, int dy, int dz) throws IllegalArgumentException {
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
     * @throws  IllegalArgumentException
     *          Throws if the path is null or is empty.
     */
    void updateTarget(Vector newTarget) throws IllegalArgumentException {
        this.target = newTarget;

        this.path = unit.getPathFinder().getPath(unit.getPosition().toIntVector(), newTarget.toIntVector());
        if (path == null || path.size() == 0) {
            unit.finishCurrentActivity();
            throw new IllegalArgumentException("Impossible to path!!");
        }
        if (!unit.getPosition().subtract(unit.getPosition().toIntVector().toVector()).isEqualTo(new Vector(0.5, 0.5, 0.5), Unit.POS_EPS))
            this.path.add(unit.getPosition().toIntVector());
        idx = path.size() - 1;
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
    private Vector calculateSpeed(Vector target){
        double vb = 1.5*(unit.getStrength()+unit.getAgility())/(2*(unit.getWeight()));
        Vector diff = target.subtract(unit.getPosition());
        double d = diff.norm();
        diff = diff.divide(d);

        double vw = vb;
        if (diff.getZ() > Unit.POS_EPS) {
            vw = 0.5*vb;
        }
        else if (diff.getZ() < -Unit.POS_EPS) {
            vw = 1.2*vb;
        }
        return diff.multiply(vw);
    }
}