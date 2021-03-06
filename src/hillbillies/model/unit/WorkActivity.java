package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.Terrain;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

/**
 * The activity for working.
 *
 * @invar   location must be valid & effective
 *          | this.getUnit().getWorld().getTerrain().isValidPosition(location)
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
     */
    WorkActivity(Unit unit) {
        super(unit);
    }

    /**
     * Updates the working of the unit in function of the time.
     *
     * @param   dt
     *          The time step to update the activity with.
     *
     * @post    If the time delay has ended the unit will:
     *              If the unit carries a boulder or log, the boulder or log is dropped at the centre
     *              of the cube targeted by the labour action.
     *              Else if the target cube is a workshop and one boulder and one log are available on
     *              that cube, the unit will improve their equipment, consuming one boulder and one log
     *              (from the workshop cube), and increasing the unit's weight and toughness.
     *              Else if a boulder is present on the target cube, the unit shall pick up the boulder.
     *              Else if a log is present on the target cube, the unit shall pick up the log.
     *              Else if the target cube is wood, the cube collapses leaving a log.
     *              Else if the target cube is rock, the cube collapses leaving a boulder.
     *
     * @effect  If work is done, the unit receives 10 xp.
     *          | addXp(10)
     */
    @Override @Model
    void advanceTime(double dt) {
        workTimer -= dt;
        if (this.getWorkTimer() <= 0) {
            if (getUnit().isCarryingLog() || getUnit().isCarryingBoulder()){ //BOULDER OR LOG
                if (!Terrain.isSolid(getUnit().getWorld().getTerrain().getCubeType(getLocation()))) {
                    getUnit().dropCarry(getLocation());
                } else {
                    getUnit().finishCurrentActivity();
                    return; // no xp may be added because no job is completed
                }
            } else if (getUnit().getWorld().getTerrain().getCubeType(getLocation()) == Terrain.Type.WORKSHOP &&
                       getUnit().getWorld().getTerrain().getLogs(getLocation()).size() >= 1 &&
                       getUnit().getWorld().getTerrain().getBoulders(getLocation()).size() >= 1) {

                getUnit().getWorld().consumeBoulder(getLocation());
                getUnit().getWorld().consumeLog(getLocation());

                getUnit().setWeight(getUnit().getWeight() + 1);
                getUnit().setToughness(getUnit().getToughness() + 1);
            } else if (getUnit().getWorld().getTerrain().getBoulders(getLocation()).size() >= 1) {
                getUnit().pickUpGameObject(getUnit().getWorld().getTerrain().getBoulders(getLocation()).iterator().next());
            } else if (getUnit().getWorld().getTerrain().getLogs(getLocation()).size() >= 1) {
                getUnit().pickUpGameObject(getUnit().getWorld().getTerrain().getLogs(getLocation()).iterator().next());
            } else if (getUnit().getWorld().getTerrain().getCubeType(getLocation()) == Terrain.Type.TREE) {
                getUnit().getWorld().getTerrain().breakCube(getLocation());
            } else if (getUnit().getWorld().getTerrain().getCubeType(getLocation()) == Terrain.Type.ROCK) {
                getUnit().getWorld().getTerrain().breakCube(getLocation());
            } else {
                getUnit().finishCurrentActivity();
                return; // no xp may be added because no job is completed
            }
            getUnit().addXp(10);

            getUnit().finishCurrentActivity();
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
     */
    @Override
    void reset() {
        this.location = null;
        this.workTimer = 0;
    }


    /**
     * Starts the work activity for the given unit at the given position.
     *
     * @param   location
     *          The location at which the unit starts working.
     *
     * @post    The workTimer will be initialized.
     *          | new.getWorkTimer() == 500.0 / this.getUnit().getStrength()
     * @post    The work location is set.
     *          | new.getLocation() == location
     *
     * @effect  Makes the unit face in the direction of the working location.
     *          | this.setOrientation(Math.atan2(new.getLocation().subtract(this.getUnit().getPosition()).getY(),
     *          |                                new.getLocation().subtract(this.getUnit().getPosition()).getX()))
     *
     * @throws  InvalidPositionException
     *          Thrown if the location is invalid or the location isn't next to the unit.
     *          | !unit.isValidPosition(location) || !location.isNextTo(this.getUnit().getPosition().toIntVector())
     */
    void workAt(IntVector location) throws IllegalArgumentException, InvalidPositionException {
        if (!getUnit().getWorld().getTerrain().isValidPosition(location))
            throw new InvalidPositionException(location);

        if (!location.isNextTo(getUnit().getPosition().toIntVector()))
            throw new InvalidPositionException("Work location too far: ", location);

        this.workTimer  = 500.0 / getUnit().getStrength();
        this.location = location;

        Vector diff = location.toVector().add(Terrain.Lc/2).subtract(getUnit().getPosition());
        getUnit().setOrientation(Math.atan2(diff.getY(), diff.getX()));
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