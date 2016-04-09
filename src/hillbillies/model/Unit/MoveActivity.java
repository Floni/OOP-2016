package hillbillies.model.Unit;

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

    protected MoveActivity(Unit unit) {
        super(unit);
    }

    public MoveActivity(Unit unit, Vector target) {
        super(unit);
        updateTarget(target);

    }

    public MoveActivity(Unit unit, int dx, int dy, int dz) throws IllegalArgumentException {
        super(unit);
        moveToNeighbour(dx, dy, dz);
    }

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

    private void goToNextNeighbour() {
        moveToNeighbour(path.get(idx));
        idx -= 1;
    }

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return this.target != null;
    }

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
     * @param neighbour The neighbour to move to.
     * @throws IllegalArgumentException
     */
    private void moveToNeighbour(IntVector neighbour) throws IllegalArgumentException {
        this.targetNeighbour = neighbour.toVector().add(World.Lc/2);

        if (!unit.isValidPosition(this.targetNeighbour.toIntVector()))
            throw new IllegalArgumentException("Illegal neighbour");

        this.speed = calculateSpeed(this.targetNeighbour);
        unit.setOrientation(Math.atan2(this.speed.getY(), this.speed.getX()));
    }

    private void moveToNeighbour(int dx, int dy, int dz) throws IllegalArgumentException {
        IntVector curPos = unit.getPosition().toIntVector();
        moveToNeighbour(curPos.add(dx, dy, dz));
    }

    void updateAdjacent(int dx, int dy, int dz) throws IllegalArgumentException {
        moveToNeighbour(dx, dy, dz);
        this.path = null;
    }

    void updateTarget(Vector newTarget) throws IllegalArgumentException {
        this.target = newTarget;

        this.path = unit.getPathFinder().getPath(unit.getPosition().toIntVector(), newTarget.toIntVector());
        if (path == null || path.size() == 0)
            throw new IllegalArgumentException("Impossible to path!!");
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