package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.World;
import hillbillies.model.exceptions.UnreachableTargetException;
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
     * @post    If the unit still has an unfinished activity, he will resume this activity.
     * @post    Else if default behavior is enabled the unit does with an equal chance one of the following things.
     *          - The unit moves to a random valid position in the world which the unit can reach.
     *          - The unit works on a neighbouring cube.
     *          - The unit starts resting.
     *          - The unit attacks an enemy unit that is in range.
     */
    @Override
    void advanceTime(double dt) {
        if (!getUnit().getLastActivity().equalsClass(NoneActivity.class)) {
            getUnit().finishCurrentActivity(); // we still have an interrupted activity
        } else if (getUnit().isDefaultEnabled()) {
            if (getUnit().hasAssignedTask()) {
                getUnit().getAssignedTask().runFor(dt);
            } else if (getUnit().getFaction().getScheduler().isTaskAvailable()) {
                getUnit().assignTask(getUnit().getFaction().getScheduler().getTask(getUnit()));
            } else  {
                int random = (int)Math.floor(Math.random()*4);
                switch (random) {
                    case 3: // move
                        IntVector randPos;
                        do {
                            randPos = new IntVector(Math.random()*getUnit().getWorld().X_MAX,
                                    Math.random()*getUnit().getWorld().Y_MAX,
                                    Math.random()*getUnit().getWorld().Z_MAX);
                            if (getUnit().isValidPosition(randPos)) {
                                try {
                                    getUnit().moveTo(randPos);
                                    break;
                                } catch (UnreachableTargetException ignored) {}
                            }
                        } while (true);
                        break;
                    case 0: // work
                        List<IntVector> neighbours = World.getNeighbours(getUnit().getPosition().toIntVector())
                                .filter(v -> getUnit().getWorld().isValidPosition(v)).collect(Collectors.toList());
                        getUnit().workAt(neighbours.get((int)(Math.random() * neighbours.size())));
                        break;
                    case 1: // rest
                        getUnit().rest();
                        break;
                    case 2: //attack
                        Set<Unit> units = getUnit().getWorld().getUnits();
                        for (Unit other : units) {
                            if (other.getFaction() != getUnit().getFaction()) {
                                if (getUnit().canAttack(other) && !other.getCurrentActivity().equalsClass(FallActivity.class)) {
                                    getUnit().attack(other);
                                    break;
                                }
                            }
                        }
                        break;

                }
            }
        }
    }

    /**
     * Returns whether the unit can switch activities which is true when the unit is alive.
     */
    @Override @Basic
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return getUnit().isAlive();
    }


    /**
     * Resumes the activity, which does nothing.
     */
    @Override
    void resume() {
    }
}
