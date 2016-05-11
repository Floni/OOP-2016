package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import hillbillies.model.programs.statement.ActivityTracker;

/**
 * The abstract base class for all activities, providing shared methods.
 *
 * @invar   The unit must be valid & effective.
 *          | this.getUnit() != null.
 *
 */
abstract class Activity {
    protected final Unit unit;
    private ActivityTracker tracker;

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

    //<editor-fold desc="Tracker">
    /**
     * Sets the tracker for this activity.
     *
     * @param   tracker
     *          The tracker.
     *
     * @post    The tracker will be set.
     *          | new.getTracker() == tracker
     */
    void setTracker(ActivityTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Returns true if this activity has a tracker.
     *
     * @return  True if the tracker is effective
     *          | result == this.getTracker() != null
     */
    boolean hasTracker() {
        return this.tracker != null;
    }

    /**
     * Returns the tracker of this activity.
     * @return
     */
    @Basic
    ActivityTracker getTracker() {
        return tracker;
    }

    /**
     * Resets the tracker of this activity
     *
     * @post    the activity won't have a tracker.
     *          | !new.hasTracker()
     */
    void resetTracker() {
        this.tracker = null;
    }

    /**
     * Finishes this activity.
     *
     * @pre     This activity must be the unit's current activity.
     *          | getUnit().getCurrentActivity() == this
     *
     * @effect  If this activity has a tracker, it's marked done.
     *          | if (this.hasTracker)
     *          |   this.getTracker().setDone()
     * @effect  Finishes this activity.
     *          | this.getUnit().finishCurrentActivity().
     */
    void finishActivity() {
        if (getUnit().getCurrentActivity() == this) {
            if (this.hasTracker())
                this.getTracker().setDone();
            getUnit().finishCurrentActivity();
        }
        this.reset();
    }

    /**
     * Interrupts the tracker, use when resetting the activity.
     *
     * @effect  If this activity has a tracker, it's marked interrupted.
     *          | if (this.hasTracker)
     *          |   this.getTracker().setInterrupt()
     * @effect  Reset this activity's tracker.
     *          | this.resetTracker()
     */
    void interruptTracker() {
        if (this.hasTracker())
            this.getTracker().setInterrupt();
        this.resetTracker();
    }
    //</editor-fold>

    /**
     * Does the required work for this activity.
     *
     * @param   dt
     *          Time since last frame.
     */
    abstract void advanceTime(double dt);

    /**
     * Returns whether or not the unit may change Activities.
     */
    abstract boolean canSwitch();

    /**
     * Pauses the activity, called when the unit is interrupted.
     * After this, the activity can be resumed or finished (reset).
     */
    abstract void pause();

    /**
     * Called after pause when the activity is resumed.
     */
    abstract void resume();

    /**
     * Resumes the activity when it was interrupted.
     */
    @Raw
    abstract void reset();

}
