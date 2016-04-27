package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
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

    WorkActivity(Unit unit) {
        super(unit);
    }

    /**
     * Initialises the work activity for the given unit at the given position.
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
     * @effect  If the work is done the work activity is finished
     *          | finishWork()
     *          | finishCurrentActivity()
     */
    @Override
    void advanceTime(double dt) {
        workTimer -= dt;
        if (workTimer <= 0) {
            workTimer = 0;
            finishWork();
            getUnit().finishCurrentActivity();
        }
    }


    /**
     * Finishes the work activity and executes the required action.
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
    private void finishWork() {
        if (getUnit().isCarryingLog() || getUnit().isCarryingBoulder()){ //BOULDER OR LOG
            if (!World.isSolid(getUnit().getWorld().getCubeType(location)))
                getUnit().dropCarry(location);
            else
                return; // don't add xp
        } else if (getUnit().getWorld().getCubeType(location) == World.WORKSHOP &&
                getUnit().getWorld().getLogs(location).size() >= 1 && getUnit().getWorld().getBoulders(location).size() >= 1) {

            getUnit().getWorld().consumeBoulder(location);
            getUnit().getWorld().consumeLog(location);

            getUnit().setWeight(getUnit().getWeight() + 1);
            getUnit().setToughness(getUnit().getToughness() + 1);
        } else if (getUnit().getWorld().getBoulders(location).size() >= 1) {
            getUnit().pickUpBoulder(getUnit().getWorld().getBoulders(location).iterator().next());
        } else if (getUnit().getWorld().getLogs(location).size() >= 1) {
            getUnit().pickUpLog(getUnit().getWorld().getLogs(location).iterator().next());
        } else if (getUnit().getWorld().getCubeType(location) == World.TREE) {
            getUnit().getWorld().breakCube(location);
        } else if (getUnit().getWorld().getCubeType(location) == World.ROCK) {
            getUnit().getWorld().breakCube(location);
        }
        getUnit().addXp(10);
        if (this.hasTracker())
            this.getTracker().setDone();
    }

    /**
     * Returns whether the unit can switch activities.
     */
    @Override @Basic
    boolean canSwitch() {
        return true;
    }


    /**
     * Resets
     */
    @Override
    void reset() {
        this.location = null;
        this.workTimer = 0;

        if (this.hasTracker())
            this.getTracker().setInterrupt();
        this.resetTracker();
    }
}