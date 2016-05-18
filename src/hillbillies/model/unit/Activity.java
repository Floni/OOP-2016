package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * The abstract base class for all activities, providing shared methods.
 *
 * @invar   The unit must be valid & effective.
 *          | this.getUnit() != null.
 *
 */
abstract class Activity {
    private final Unit unit;

    /**
     * Initialize a new Activity from the given unit.
     *
     * @param   unit
     *          The unit which will execute the activity.
     *
     * @post    The unit will be set.
     *          | new.getUnit() == unit
     *
     * @effect  Reset the activity.
     *          | this.reset()
     *
     * @throws  IllegalArgumentException
     *          If the unit isn't effective.
     *          | unit == null
     */
    Activity(Unit unit) throws IllegalArgumentException {
        if (unit == null)
            throw new IllegalArgumentException("unit isn't effective");

        this.unit = unit;
        this.reset();
    }

    /**
     * Returns the unit which will conduct the activity.
     */
    @Basic @Immutable
    Unit getUnit() {
        return this.unit;
    }

    /**
     * Switches the units activity.
     *
     * @param   newActivity
     *          The new Activity the unit will execute.
     *
     * @effect  If the unit has a tracker, and thus is executing a task, interrupt it.
     *          | if (this.getUnit().hasTracker()) then (this.getUnit().interruptTask())
     * @effect  Reset The current activity if it isn't the same as the newActivity.
     *          | if (newActivity != this.getUnit().getCurrentActivity())
     *          | then (this.getUnit().getCurrentActivity().reset())
     * @effect  Set the unit's currentActivity to the newActivity.
     *          | this.getUnit().setCurrentActivity(newActivity)
     */
    void switchActivity(Activity newActivity) {
        if (getUnit().hasTracker())
            getUnit().interruptTask();

        if (newActivity != getUnit().getCurrentActivity())
            getUnit().getCurrentActivity().reset();

        getUnit().setCurrentActivity(newActivity);
    }

    /**
     * Does the required work for this activity.
     *
     * @param   dt
     *          Time since last frame.
     */
    @Model
    abstract void advanceTime(double dt);

    /**
     * Returns whether or not the unit may change Activities.
     */
    abstract boolean canSwitch();

    /**
     * Resets the activity when it was interrupted.
     */
    @Raw
    abstract void reset();
}
