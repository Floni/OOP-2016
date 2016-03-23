package hillbillies.model.Unit;

/**
 * Created by timo on 3/17/16.
 */
abstract class Activity {
    protected Unit unit;

    public Activity(Unit unit) {
        this.unit = unit;
    }

    abstract void advanceTime(double dt);

    abstract boolean canSwitch(Class<? extends Activity> newActivity);

    boolean equalsClass(Activity other) {
        return this.equalsClass(other.getClass());
    }

    boolean equalsClass(Class<? extends Activity> other) {
        return other.isAssignableFrom(this.getClass());
    }

    public abstract void resume();
}
