package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

// TODO: a task isn't interrupted until noneActivity becomes active again.

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

    void switchActivity(Activity newActivity) {
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
     * Resumes the activity when it was interrupted.
     */
    @Raw
    abstract void reset(); // TODO: interrupt task, if activity is interrupted
}
