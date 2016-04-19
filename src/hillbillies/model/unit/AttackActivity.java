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

    /**
     * Initialises the attack activity with the given attacker and defender.
     *
     * @param   unit
     *          The unit who conducts the attack.
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
    AttackActivity(Unit unit, Unit other) throws IllegalArgumentException, InvalidUnitException {
        super(unit);
        attackTimer = ATTACK_DELAY;

        if (other == null || other == getUnit() || other.getCurrentActivity().equalsClass(FallActivity.class))
            throw new InvalidUnitException("The other unit is invalid");

        if (getUnit().getFaction() == other.getFaction())
            throw new InvalidUnitException("Can't attack units of the same faction");

        Vector otherPos = other.getPosition();
        if (!unit.canAttack(other))
            throw new InvalidUnitException("Other unit is to far away");

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
     * @pre The method shouldn't be called
     *      | false
     */
    @Override
    void resume()  throws IllegalStateException {
        // can't happen
        assert false;
    }
}
