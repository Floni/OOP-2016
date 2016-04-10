package hillbillies.model.Unit;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * Created by timo on 3/17/16.
 *
 */
class RestActivity extends Activity {
    private static final double REST_DELAY = 0.2;

    private double restTimer;
    private double restDiff;
    private boolean initialRest;


    /**
     *  Starts the rest activity for the given unit.
     *
     * @param   unit
     *          The unit who is conducting the activity.
     *
     * @post    The unit can not switch activities until he recovers one hitPoint.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     */
    RestActivity(Unit unit) {
        super(unit);
        this.restTimer = REST_DELAY;
        this.restDiff = 0;
        this.initialRest = true;
    }


    /**
     * Updates the state of the rest activity.
     *
     * @param   dt
     *          The time step that is given.
     *
     * @post    If the unit does not have full hitpoints his hitpoints will regen.
     * @post    Else if the unit does not have full stamina his stamina will regen.
     * @post    Else the unit will stop resting.
     */
    @Override
    void advanceTime(double dt) {
        this.restTimer -= dt;
        if (this.restTimer <= 0) {
            this.restTimer += REST_DELAY;

            if (unit.getHitPoints() != unit.getMaxPoints()) {
                restDiff += (unit.getToughness()/200.0);
                // recover at least 1 HP
                if (restDiff >= 1) {
                    initialRest = false;
                    restDiff -= 1;
                    unit.setHitPoints(unit.getHitPoints()+1);
                }
            } else if (unit.getStamina() != unit.getMaxPoints()) {
                initialRest = false;
                restDiff += (unit.getToughness()/100.0);
                if (restDiff >= 1) {
                    restDiff -= 1;
                    unit.setStamina(unit.getStamina() + 1);
                }
            } else {
                unit.finishCurrentActivity();
            }
        }
    }


    /**
     * Returns true if the unit can switch activities.
     */
    @Override @Basic
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return !initialRest;
    }


    /**
     * Resumes the rest activity.
     */
    @Override
    public void resume() {
        this.restTimer = REST_DELAY;
        this.initialRest = true;
        this.restDiff = 0;
    }
}
