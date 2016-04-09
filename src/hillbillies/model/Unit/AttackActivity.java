package hillbillies.model.Unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.Vector.Vector;

/**
 * Created by timo on 3/17/16.
 *
 */
class AttackActivity extends Activity {
    public static final double ATTACK_DELAY = 1;

    private double attackTimer;

    /**
     * Initialises the attack activity with the given attacker and defender.
     *
     * @param   unit
     *          The unit who conducts the attack.
     * @param   other
     *          The unit that is attacked.
     *
     *  @effect The unit's will face each other.
     *          | setOrientation()
     *  @effect The defender will defend against the attack
     *          | other.defend(unit)
     */
    public AttackActivity(Unit unit, Unit other) {
        super(unit);
        attackTimer = ATTACK_DELAY;

        Vector otherPos = other.getPosition();
        if (!unit.canAttack(other)) {
            throw new IllegalArgumentException("Other unit is to far away");
        }

        Vector diff = otherPos.subtract(unit.getPosition());
        unit.setOrientation(Math.atan2(diff.getY(), diff.getX()));
        other.setOrientation(Math.atan2(-diff.getY(), -diff.getX()));

        other.defend(unit);
    }

    /**
     * Updates the attack activity in function of the given time step.
     *
     * @param   dt
     *          The given time step.
     *
     * @effect  If the attack is completed, finish the attack activity
     *          | unit.finishCurrentActivity()
     */
    @Override
    void advanceTime(double dt) {
        this.attackTimer -= dt;
        if (this.attackTimer <= 0) {
            this.attackTimer = 0;

            unit.finishCurrentActivity();
        }
    }

    /**
     * Returns whether the unit can switch activities, which is always false.
     */
    @Override @Basic
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return false;
    }

    /**
     * Resumes the attack activity, which is not possible.
     *
     * @throws  IllegalStateException
     *          Always throws
     */
    @Override
    public void resume()  throws IllegalStateException {
        // can't happen
        throw new IllegalStateException("can't resume an attack");
    }
}
