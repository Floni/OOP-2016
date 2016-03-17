package hillbillies.model.Unit;

/**
 * Created by timo on 3/17/16.
 */
class RestActivity extends Activity {
    public static final double REST_DELAY = 0.2;

    private double restTimer;
    private double restDiff;
    private boolean initialRest;

    public RestActivity(Unit unit) {
        super(unit);
        this.restTimer = REST_DELAY;
        this.restDiff = 0;
        this.initialRest = true;
    }

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

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return !initialRest;
    }
}
