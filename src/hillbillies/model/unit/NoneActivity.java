package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import hillbillies.model.Terrain;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.util.Util;
import hillbillies.model.vector.IntVector;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default activity for when an unit isn't doing anything, also manages default behaviour.
 */
class NoneActivity extends Activity {

    /**
     * Initializes the default behavior activity.
     *
     * @param   unit
     *          The unit who starts default behavior.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     */
    NoneActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
    }

    /**
     * Updates the default behavior activity for the given time step.
     *
     * @param   dt
     *          The time step to update the activity with.
     *
     * @post    If default behavior is enabled and the unit has an assigned task, the task will be updated with time step dt.
     *          Else if default behavior is enabled and the unit has no assigned task,
     *            the unit will get the task with the highest priority from the scheduler of his faction.
     *          Else if default behavior is enabled and there are no tasks available,
     *           the unit does with an equal chance one of the following things:
     *             - The unit works on a neighbouring cube.
     *             - The unit starts resting.
     *             - The unit attacks an enemy unit that is in range.
     *             - The unit moves to a random valid position in the world which the unit can reach.
     */
    @Override @Model
    void advanceTime(double dt) {
        if (getUnit().isDefaultEnabled()) {
            if (getUnit().hasAssignedTask()) {
                getUnit().getAssignedTask().advanceTime(dt);
            } else if (getUnit().getFaction().getScheduler().isTaskAvailable()) {
                getUnit().assignTask(getUnit().getFaction().getScheduler().getTask(getUnit()));
            } else  {
                int random = Util.randomInt(4);
                switch (random) {
                    case 0: // work
                        List<IntVector> neighbours = Terrain.getNeighbours(getUnit().getPosition().toIntVector())
                                .filter(v -> getUnit().getWorld().getTerrain().isValidPosition(v)).collect(Collectors.toList());
                        getUnit().workAt(neighbours.get(Util.randomInt(neighbours.size())));
                        break;
                    case 1: // rest
                        getUnit().rest();
                        break;
                    case 2: //attack
                        Set<Unit> units = getUnit().getWorld().getUnits();
                        for (Unit other : units) {
                            if (other.getFaction() != getUnit().getFaction()) {
                                try {
                                    getUnit().attack(other);
                                    break;
                                } catch (InvalidUnitException ignored) {}
                            }
                        }
                        break;
                    case 3: // move
                        IntVector randPos;
                        do {
                            randPos = new IntVector(
                                    Util.randomInt(getUnit().getWorld().getTerrain().getMaxX()),
                                    Util.randomInt(getUnit().getWorld().getTerrain().getMaxY()),
                                    Util.randomInt(getUnit().getWorld().getTerrain().getMaxZ())
                            );
                            if (getUnit().isValidPosition(randPos)) {
                                try {
                                    getUnit().moveTo(randPos);
                                    break;
                                } catch (UnreachableTargetException ignored) {}
                            }
                        } while (true);
                        break;
                }
            }
        }
    }

    /**
     * Returns whether the unit can switch activities which is true when the unit is alive.
     *
     * @return  Always true.
     *          | result == true
     */
    @Override
    boolean canSwitch() {
        return true;
    }

    /**
     * Resets the activity, which does nothing.
     */
    @Override @Raw
    void reset() {
    }
}
