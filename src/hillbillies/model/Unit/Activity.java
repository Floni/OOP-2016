package hillbillies.model.Unit;

/**
 * Created by timo on 3/17/16.
 * 
 */
abstract class Activity {
    protected Unit unit;

    protected Activity(Unit unit) {
        this.unit = unit;
    }

    abstract void advanceTime(double dt);

    abstract boolean canSwitch(Class<? extends Activity> newActivity);

    /**
     * Returns whether this activity equals the given activity.
     *
     * @effect  Checks whether the class equals the given class.
     *          | this.equalsClass(other.getClass())
     */
    boolean equalsClass(Activity other) {
        return this.equalsClass(other.getClass());
    }


    /**
     * Returns whether this activity equals the given activity.
     *
     * @param   other
     *          The activity to compare with.
     *
     * @return  Returns True if the activities are the same.
     */
    boolean equalsClass(Class<? extends Activity> other) {
        return other.isAssignableFrom(this.getClass());
    }

    public abstract void resume();
}
