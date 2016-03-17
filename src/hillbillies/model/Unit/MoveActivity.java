package hillbillies.model.Unit;

import hillbillies.model.Vector;
import hillbillies.model.World;

import java.util.List;

/**
 * Created by timo on 3/17/16.
 */
class MoveActivity extends Activity {
    public static final double SPRINT_DELAY = 0.1;

    protected Vector target;
    private Vector targetNeighbour;
    protected Vector speed;

    double sprintStaminaTimer;
    boolean sprinting;

    private List<Vector> path;
    private int idx;

    protected MoveActivity(Unit unit) {
        super(unit);
    }

    public MoveActivity(Unit unit, Vector target) {
        super(unit);
        updateTarget(target);

    }

    public MoveActivity(Unit unit, int[] adjacent) throws IllegalArgumentException {
        super(unit);
        moveToNeighbour(adjacent);
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

        Vector newPosition = unit.getPositionVector().add(this.speed.multiply(mod*dt));
        if (isAtNeighbour(newPosition)) {
            unit.addXp(1);
            unit.setPosition(this.targetNeighbour);
            if (!unit.pendingActivity.equalsClass(NoneActivity.class)) {
                unit.currentActivity = unit.pendingActivity;
                unit.pendingActivity = unit.NONE_ACTIVITY;
                unit.lastActivity = this;
            } else if (this.target == null || isAtTarget()) {
                unit.finishCurrentActivity();
            } else {
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

    /**
     * Checks whether the unit has arrived at the target
     *
     * @return  True if the unit's position equals the target position.
     *          | result == this.position.isEqualTo(this.target, POS_EPS)
     */
    private boolean isAtTarget() {
        return unit.getPositionVector().isEqualTo(this.target, Unit.POS_EPS);
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
        double dist_new = newPosition.substract(this.targetNeighbour).norm();
        double dist_cur = unit.getPositionVector().substract(this.targetNeighbour).norm();
        return dist_new > dist_cur;
    }


    /**
     * Moves to specified neighbour cube (must be next to current position).
     * @param neighbour The neighbour to move to.
     * @throws IllegalArgumentException
     */
    private void moveToNeighbour(Vector neighbour) throws IllegalArgumentException {
        this.targetNeighbour = neighbour.add(World.Lc/2);

        if (!unit.isValidPosition(this.targetNeighbour.toDoubleArray()))
            throw new IllegalArgumentException("Illegal neighbour");

        this.speed = calculateSpeed(this.targetNeighbour);
        unit.setOrientation(Math.atan2(this.speed.getY(), this.speed.getX()));
    }

    private void moveToNeighbour(int[] adjacent) throws IllegalArgumentException {
        int[] curPos = World.getCubePosition(unit.getPosition());
        Vector target = new Vector(curPos[0] + adjacent[0], curPos[1] + adjacent[1], curPos[2] + adjacent[2]);
        moveToNeighbour(target);
    }

    void updateAdjecent(int dx, int dy, int dz) throws IllegalArgumentException {
        moveToNeighbour(new int[]{dx, dy, dz});
        // TODO: recalc path
    }

    void updateTarget(Vector newTarget) throws IllegalArgumentException {
        this.target = newTarget;

        this.path = unit.pathFinder.getPath(new Vector(World.getCubePosition(unit.getPosition())),
                new Vector(World.getCubePosition(target.toDoubleArray())));
        if (path == null)
            throw new IllegalArgumentException("Impossible to path!!");

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
     *          | this.getPositionVector().add(result.multiply(getPositionVector().subtract(target).norm()
     *          | / getSpeedScalar())).isEqualTo(target)
     */
    private Vector calculateSpeed(Vector target){
        double vb = 1.5*(unit.getStrength()+unit.getAgility())/(2*(unit.getWeight()));
        Vector diff = target.substract(unit.getPositionVector());
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