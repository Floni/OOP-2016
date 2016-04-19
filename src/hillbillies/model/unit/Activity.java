package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;

/**
 * The abstract base class for all activities, providing shared methods.
 *
 * @invar   The unit must be valid & effective.
 *          | this.getUnit() != null.
 *
 */
abstract class Activity {
    protected final Unit unit;

    /**
     * Initialize a new Activity from the given unit.
     *
     * @param   unit
     *          The unit which will execute the activity.
     * @post    The unit will be set
     *          | new.getUnit() == unit
     *
     * @throws  IllegalArgumentException
     *          If the unit isn't effective.
     *          | unit == null
     */
    Activity(Unit unit) throws IllegalArgumentException {
        if (unit == null)
            throw new IllegalArgumentException("unit isn't effective");

        this.unit = unit;
    }

    /**
     * Returns the unit which will conduct the activity.
     * TODO: use getUnit() everywhere
     */
    @Basic
    Unit getUnit() {
        return this.unit;
    }

    /**
     * Does the required work for this activity.
     * @param   dt
     *          Time since last frame.
     */
    abstract void advanceTime(double dt);

    /**
     * Returns whether or not the unit may change Activities.
     */
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
    @Model
    boolean equalsClass(Class<? extends Activity> other) {
        return other.isAssignableFrom(this.getClass());
    }

    /**
     * Resumes the activity when it was interrupted.
     */
    abstract void resume();
}
