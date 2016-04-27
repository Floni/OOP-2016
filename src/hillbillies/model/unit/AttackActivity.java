package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.vector.Vector;

/**
 * The activity for attacking another unit.
 */
class AttackActivity extends Activity {
    private static final double ATTACK_DELAY = 1;

    private double attackTimer;

    /**
     * Initializes the attack activity.
     *
     * @param   unit
     *          The unit who is attacking.
     *
     * @effect  Initialize the Activity with the given unit.
     *          | super(unit);
     *
     * @effect  Reset the activity.
     *          | this.reset()
     */
    AttackActivity(Unit unit) {
        super(unit);
    }

    /**
     * Initialises the attack activity with the given attacker and defender.
     *
     * @param   other
     *          The unit that is attacked.
     *
     * @effect  The unit's will face each other.
     *          | this.setOrientation(TODO)
     *          | other.setOrientation()
     * @effect  The defender will defend against the attack
     *          | other.defend(this.getUnit())
     *
     * @throws  InvalidUnitException
     *          The other unit is invalid.
     *          | other == null || other == this.getUnit() || other.isFalling() || !other.isAlive()
     * @throws  InvalidUnitException
     *          The other unit is too far away to attack.
     *          | !this.canAttack(other)
     * @throws  InvalidUnitException
     *          The other unit is of the same faction.
     *          | this.getUnit().getFaction() == other.getFaction()
     */
    void setTarget(Unit other) throws InvalidUnitException {
        if (other == null || other == getUnit() || other.isFalling() || !other.isAlive())
            throw new InvalidUnitException("The other unit is invalid");

        if (getUnit().getFaction() == other.getFaction())
            throw new InvalidUnitException("Can't attack units of the same faction");

        if (!this.canAttack(other))
            throw new InvalidUnitException("Other unit is to far away");

        attackTimer = ATTACK_DELAY;

        Vector diff = other.getPosition().subtract(getUnit().getPosition());
        getUnit().setOrientation(Math.atan2(diff.getY(), diff.getX()));
        other.setOrientation(Math.atan2(-diff.getY(), -diff.getX()));

        other.defend(getUnit());
    }

    /**
     * Updates the attack activity in function of the given time step.
     *
     * @param   dt
     *          The given time step.
     *
     * @post    The attackTimer is decreased with dt.
     *          | new.getAttackTimer() == this.getAttackTimer() - dt
     *
     * @effect  If the attack is completed, finishActivity the attack activity
     *          | if (new.getAttackTimer() <= 0)
     *          |   this.finishActivity()
     */
    @Override
    void advanceTime(double dt) {
        this.attackTimer -= dt;
        if (this.getAttackTimer() <= 0) {
            this.finishActivity();
        }
    }

    /**
     * Returns whether the unit can switch activities, which is always false.
     *
     * @return  Always false.
     *          | !result
     */
    @Override
    boolean canSwitch() {
        return false;
    }

    /**
     * Resets the activity.
     *
     * @post    The attackTimer will be reset.
     *          | this.getAttackTimer() == 0
     *
     * @effect  The tracker is interrupted.
     *          | this.interruptTracker()
     */
    @Override
    void reset() {
        this.attackTimer = 0; // reset attack timer
        this.interruptTracker();
    }

    /**
     * Returns whether the unit can attack the other unit.
     *
     * @param   other
     *          The unit to attack.
     *
     * @return  Returns True if the unit's are in adjacent cubes.
     *          | result == this.getPosition().isNextTo(other.getPosition())
     */
    private boolean canAttack(Unit other) {
        return getUnit().getPosition().isNextTo(other.getPosition());
    }

    /**
     * Returns the time left.
     */
    @Basic @Model
    private double getAttackTimer() {
        return this.attackTimer;
    }
}
