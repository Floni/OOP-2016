package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.vector.Vector;

/**
 * The activity for attacking another unit.
 */
class AttackActivity extends Activity {
    private static final double ATTACK_DELAY = 1;

    private double attackTimer;

    AttackActivity(Unit unit) {
        super(unit);
    }

    /**
     * Initialises the attack activity with the given attacker and defender.
     *
     * @param   other
     *          The unit that is attacked.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     * @effect  The unit's will face each other.
     *          | setOrientation()
     * @effect  The defender will defend against the attack
     *          | other.defend(unit)
     *
     * @throws  InvalidUnitException
     *          The other unit is too far away to attack.
     * @throws  InvalidUnitException
     *          The other unit is of the same faction.
     */
    void setTarget(Unit other) throws InvalidUnitException {
        if (other == null || other == getUnit() || other.isFalling())
            throw new InvalidUnitException("The other unit is invalid");

        if (getUnit().getFaction() == other.getFaction())
            throw new InvalidUnitException("Can't attack units of the same faction");

        Vector otherPos = other.getPosition();
        if (!this.canAttack(other))
            throw new InvalidUnitException("Other unit is to far away");

        attackTimer = ATTACK_DELAY;

        Vector diff = otherPos.subtract(getUnit().getPosition());
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
     * @effect  If the attack is completed, finish the attack activity
     *          | unit.finishCurrentActivity()
     */
    @Override
    void advanceTime(double dt) {
        this.attackTimer -= dt;
        if (this.attackTimer <= 0) {
            this.attackTimer = 0;

            if (this.hasTracker())
                this.getTracker().setDone();
            getUnit().finishCurrentActivity();
        }
    }

    /**
     * Returns whether the unit can switch activities, which is always false.
     */
    @Override @Basic
    boolean canSwitch() {
        return false;
    }

    /**
     * Resumes the attack activity, which is not possible.
     */
    @Override
    void reset() {
        this.attackTimer = 0; // reset attack timer
        if (this.hasTracker())
            this.getTracker().setInterrupt();
        this.resetTracker();
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
    boolean canAttack(Unit other) {
        return getUnit().getPosition().isNextTo(other.getPosition());
    }
}
