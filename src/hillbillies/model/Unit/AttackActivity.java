package hillbillies.model.Unit;

/**
 * Created by timo on 3/17/16.
 */
class AttackActivity extends Activity {
    public static final double ATTACK_DELAY = 1;

    private double attackTimer;

    public AttackActivity(Unit unit) {
        super(unit);
        attackTimer = ATTACK_DELAY;
    }

    @Override
    void advanceTime(double dt) {
        this.attackTimer -= dt;
        if (this.attackTimer <= 0) {
            this.attackTimer = 0;

            unit.finishCurrentActivity();
        }
    }

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return false;
    }
}
