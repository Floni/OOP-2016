package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * The activity for resting.
 */
class RestActivity extends Activity {
    private static final double REST_DELAY = 0.2;

    private double restTimer;
    private double restDiff;
    private boolean initialRest;


    /**
     * Creates the rest activity for the given unit.
     *
     * @param   unit
     *          The unit who is conducting the activity.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit)
     */
    RestActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
    }

    /**
     * Updates the state of the rest activity.
     *
     * @param   dt
     *          The time step that is given.
     *
     * TODO: one post!!!!!!, use getUnit(), redo comment
     * @post    If the unit does not have full hitpoints his hitpoints will regen.
     * @post    Else if the unit does not have full stamina his stamina will regen.
     * @post    Else the unit will stop resting.
     */
    @Override @Model
    void advanceTime(double dt) {
        this.setRestTimer(this.getRestTimer() - dt);
        if (this.getRestTimer() <= 0) {
            this.setRestTimer(this.getRestTimer() + REST_DELAY);

            if (unit.getHitPoints() != unit.getMaxPoints()) {
                restDiff += (unit.getToughness()/200.0);
                // recover at least 1 HP
                if (getRestDiff() >= 1) {
                    this.resetInitialRest();
                    restDiff -= 1;
                    unit.setHitPoints(unit.getHitPoints()+1);
                }
            } else if (unit.getStamina() != unit.getMaxPoints()) {
                this.resetInitialRest();
                restDiff += (unit.getToughness()/100.0);

                if (getRestDiff() >= 1) {
                    restDiff -= 1;
                    unit.setStamina(unit.getStamina() + 1);
                }
            } else {
                getUnit().finishCurrentActivity();
            }
        }
    }


    /**
     * Returns true if the unit can switch activities.
     *
     * @return  The unit may switch when the activity isn't in the initial rest.
     *          | result == !this.isInitialRest()
     */
    @Override
    boolean canSwitch() {
        return !this.isInitialRest();
    }

    /**
     * Resets the rest.
     *
     * @post    The activity is in it's initial rest
     *          | new.isInitialRest() == true
     * @post    The point difference is reset.
     *          | new.getRestDiff() == 0
     * @post    The timer is reset.
     *          | new.getRestTimer() == REST_DELAY
     */
    @Override @Raw
    void reset() {
        this.setRestTimer(REST_DELAY);
        this.initialRest = true;
        this.restDiff = 0;
    }

    /**
     * Sets the rest timer to the given value.
     *
     * @param   val
     *          The value to set the rest timer to.
     *
     * @post    The rest timer wil be equal to the given value
     *          | new.getRestTimer() == val
     */
    private void setRestTimer(double val) {
        this.restTimer = val;
    }

    /**
     * Returns the rest timer.
     */
    @Basic @Model
    private double getRestTimer() {
        return this.restTimer;
    }

    /**
     * Returns the rest timer difference.
     */
    @Basic @Model
    private double getRestDiff() {
        return this.restDiff;
    }

    /**
     * Returns whether the rest activity is still in the initial rest phase.
     */
    @Basic @Model
    private boolean isInitialRest() {
        return this.initialRest;
    }

    /**
     * Disables the initial rest period.
     *
     * @post    The initial rest phase will be disabled.
     *          | new.isInitialRest() == false
     */
    private void resetInitialRest() {
        this.initialRest = false;
    }
}
