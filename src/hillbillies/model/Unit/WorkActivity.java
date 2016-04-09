package hillbillies.model.Unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;

/**
 * Created by timo on 3/17/16.
 *
 */
class WorkActivity extends Activity {

    private double workTimer = 0;
    private IntVector location;

    /**
     * Initialises the work activity for the given unit at the given position.
     *
     * @param   unit
     *          The unit which starts working.
     * @param   location
     *          The location at which the unit starts working.
     *
     * @effect  Makes the unit face in the direction of the working location
     *          | setOrientation()
     */
    WorkActivity(Unit unit, IntVector location) {
        super(unit);
        this.workTimer  = 500.0 / unit.getStrength();
        this.location = location;
        Vector diff = location.toVector().add(World.Lc/2).subtract(unit.getPosition());
        unit.setOrientation(Math.atan2(diff.getY(), diff.getX()));
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
            unit.finishCurrentActivity();
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
        if (unit.isCarryingLog() || unit.isCarryingBoulder()){ //BOULDER OR LOG
            if (!World.isSolid(unit.getWorld().getCubeType(location))) {
                unit.dropCarry(location);
                unit.addXp(10);
            }
        } else if (unit.getWorld().getCubeType(location) == World.WORKSHOP && unit.getWorld().getLogs(location).size() >= 1 &&
                unit.getWorld().getBoulders(location).size() >= 1) {

            unit.getWorld().consumeBoulder(location);
            unit.getWorld().consumeLog(location);

            unit.setWeight(unit.getWeight() + 1);
            unit.setToughness(unit.getToughness() + 1);
            unit.addXp(10);
        } else if (unit.getWorld().getBoulders(location).size() >= 1) {
            unit.pickUpBoulder(unit.getWorld().getBoulders(location).iterator().next());
            unit.addXp(10);
        } else if (unit.getWorld().getLogs(location).size() >= 1) {
            unit.pickUpLog(unit.getWorld().getLogs(location).iterator().next());
            unit.addXp(10);
        } else if (unit.getWorld().getCubeType(location) == World.TREE) {
            unit.getWorld().breakCube(location);
            unit.addXp(10);
        } else if (unit.getWorld().getCubeType(location) == World.ROCK) {
            unit.getWorld().breakCube(location);
            unit.addXp(10);
        }
    }

    /**
     * Returns whether the unit can switch activities.
     */
    @Override @Basic
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return true;
    }


    /**
     * Resumes the work activity.
     */
    @Override
    public void resume() {
        unit.finishCurrentActivity();
    }
}