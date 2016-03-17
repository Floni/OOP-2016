package hillbillies.model.Unit;

/**
 * Created by timo on 3/17/16.
 */
class WorkActivity extends Activity {
    WorkActivity(Unit unit) {
        super(unit);
        this.workTimer  = 500.0 / unit.getStrength();
    }

    private double workTimer = 0;

    @Override
    void advanceTime(double dt) {
        workTimer -= dt;
        if (workTimer <= 0) {
            unit.addXp(10);
            workTimer = 0;
            unit.finishCurrentActivity();
        }
    }

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return true;
    }
}