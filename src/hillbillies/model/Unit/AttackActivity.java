package hillbillies.model.Unit;

import hillbillies.model.Vector.Vector;

/**
 * Created by timo on 3/17/16.
 *
 */
class AttackActivity extends Activity {
    public static final double ATTACK_DELAY = 1;

    private double attackTimer;

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

    @Override
    public void resume()  throws IllegalStateException {
        // can't happen
        throw new IllegalStateException("can't resume an attack");
    }
}
