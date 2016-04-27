package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;
import hillbillies.model.World;

/**
 * The activity for working.
 *
 * @invar   location must be valid & effective
 *          | this.getUnit().getWorld().isValidPosition(location)
 */
class WorkActivity extends Activity {

    private double workTimer;
    private IntVector location;

    /**
     * Creates the work activity for the given unit.
     *
     * @param   unit
     *          The unit who is conducting the activity.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit)
     * @effect  Reset this activity.
     *          | this.reset()
     */
    WorkActivity(Unit unit) {
        super(unit);
    }

    /**
     * Starts the work activity for the given unit at the given position.
     *
     * @param   location
     *          The location at which the unit starts working.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     * @effect  Makes the unit face in the direction of the working location
     *          | setOrientation()
     *
     * @throws  InvalidPositionException
     *          Thrown if the location is invalid or the location isn't next to the unit.
     *          | !unit.isValidPosition(location) || dist(unit.getPosition(), location) > 1
     */
    void workAt(IntVector location) throws IllegalArgumentException, InvalidPositionException {
        if (!getUnit().getWorld().isValidPosition(location))
            throw new InvalidPositionException(location);

        if (!location.isNextTo(getUnit().getPosition().toIntVector()))
            throw new InvalidPositionException("Work location too far: ", location);

        this.workTimer  = 500.0 / getUnit().getStrength();
        this.location = location;

        Vector diff = location.toVector().add(World.Lc/2).subtract(getUnit().getPosition());
        getUnit().setOrientation(Math.atan2(diff.getY(), diff.getX()));
    }


    /**
     * Updates the working of the unit in function of the time.
     *
     * @param   dt
     *          The time step to update the activity with.
     *
     * TODO
     *      * Finishes the work activity and executes the required action.
     *
     * @post    If the unit carries a boulder or log, the boulder or log is dropped at the centre
     *          of the cube targeted by the labour action.
     * @post    Else if the target cube is a workshop and one boulder and one log are available on
     *          that cube, the unit will improve their equipment, consuming one boulder and one log
     *          (from the workshop cube), and increasing the unit's weight and toughness.
     * @post    Else if a boulder is present on the target cube, the unit shall pick up the boulder.
     * @post    Else if a log is present on the target cube, the unit shall pick up the log.
     * @post    Else if the target cube is wood, the cube collapses leaving a log.
     * @post    Else if the target cube is rock, the cube collapses leaving a boulder.
     *
     * @effect  If work is done, the unit receives 10 xp.
     *          | addXp()
     */
    @Override
    void advanceTime(double dt) {
        workTimer -= dt;
        if (this.getWorkTimer() <= 0) {
            if (unit.isCarryingLog() || unit.isCarryingBoulder()){ //BOULDER OR LOG
                if (!World.isSolid(unit.getWorld().getCubeType(getLocation())))
                    unit.dropCarry(getLocation());
                else
                    return; // don't add xp
            } else if (unit.getWorld().getCubeType(getLocation()) == World.WORKSHOP &&
                    unit.getWorld().getLogs(getLocation()).size() >= 1 && unit.getWorld().getBoulders(getLocation()).size() >= 1) {

                unit.getWorld().consumeBoulder(getLocation());
                unit.getWorld().consumeLog(getLocation());

                unit.setWeight(unit.getWeight() + 1);
                unit.setToughness(unit.getToughness() + 1);
            } else if (unit.getWorld().getBoulders(getLocation()).size() >= 1) {
                unit.pickUpBoulder(unit.getWorld().getBoulders(getLocation()).iterator().next());
            } else if (unit.getWorld().getLogs(getLocation()).size() >= 1) {
                unit.pickUpLog(unit.getWorld().getLogs(getLocation()).iterator().next());
            } else if (unit.getWorld().getCubeType(getLocation()) == World.TREE) {
                unit.getWorld().breakCube(getLocation());
            } else if (unit.getWorld().getCubeType(getLocation()) == World.ROCK) {
                unit.getWorld().breakCube(getLocation());
            }
            unit.addXp(10);

            this.finishActivity();
        }
    }

    /**
     * Returns whether the unit can switch activities.
     *
     * @return  Always true.
     *          | result == true
     */
    @Override
    boolean canSwitch() {
        return true;
    }


    /**
     * Resets the work activity.
     *
     * @post    The location won't be effective.
     *          | new.getLocation() == null
     * @post    The timer will be reset.
     *          | new.getWorkTimer() == 0
     *
     * @effect  Interrupt the tracker.
     *          | this.interruptTracker()
     */
    @Override
    void reset() {
        this.location = null;
        this.workTimer = 0;
        this.interruptTracker();
    }

    /**
     * Returns the location the activity is working at.
     */
    @Basic @Model
    private IntVector getLocation() {
        return this.location;
    }

    /**
     * Returns the timer for this work activity.
     */
    @Basic @Model
    private double getWorkTimer() {
        return this.workTimer;
    }
}