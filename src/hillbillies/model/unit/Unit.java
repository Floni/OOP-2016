package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import hillbillies.model.*;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.programs.statement.ActivityTracker;
import hillbillies.model.util.PathFinder;
import hillbillies.model.util.Util;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

import java.util.ArrayList;

/**
 *
 * Names of students:
 *
 * Timothy Werquin      Ingenieurswetenschappen (Computerwetenschappen en Elektrotechniek)
 * Florian Van Heghe    Ingenieurswetenschappen (Computerwetenschappen en Elektrotechniek)
 *
 * Link to git repository:
 * https://gitlab.com/timothyw/OOP-project
 *
 */

// TODO: redo all comments in Unit & World

/**
 * The unit class, this class keeps tracks of the unit's position, speed and other attributes.
 * It provides methods to move, to attack and to rest.
 *
 * @invar   The position of the unit must be valid.
 *          | isValidPosition(this.getPosition())
 * @invar   The orientation fof the unit must be valid.
 *          | isValidOrientation(this.getOrientation())
 * @invar   The weight of the unit must be valid.
 *          | canHaveAsWeight(this.getWeight())
 * @invar   The Strength of the unit must be valid.
 *          | isValidStrength(this.getStrength())
 * @invar   The Agility of the unit must be valid.
 *          | isValidAgility(this.getAgility())
 * @invar   The Toughness of the unit must be valid.
 *          | isValidToughness(this.getToughness())
 * @invar   The HitPoints must be valid.
 *          | canHaveAsHitPoints(this.getHitPoints())
 * @invar   The stamina must be valid.
 *          | canHaveAsStamina(this.getStamina())
 * @invar   The unit must always be in a faction if the world is effective.
 *          | if (getWorld() != null)
 *          |   then (getFaction != null)
 */
public class Unit {

    //<editor-fold desc="Constants">
    private static final double REST_MINUTE = 3*60;

    private static final int MIN_ATTRIBUTE = 1;
    private static final int MAX_ATTRIBUTE = 200;

    private static final double INIT_ORIENTATION = Math.PI / 2;
    //</editor-fold>

    //<editor-fold desc="Variables">
    private String name;
    private Vector position;
    private double orientation;
    private int weight, strength, agility, toughness;
    private int hitPoints, stamina;
    private int xp, xpDiff;
    private boolean defaultEnabled;
    private Vector speed;
    private boolean sprinting;

    private Faction faction;
    private World world;

    private Log carryLog;
    private Boulder carryBoulder;

    private Task task;

    private double restMinuteTimer;

    private final NoneActivity noneActivity = new NoneActivity(this);
    private final MoveActivity moveActivity = new MoveActivity(this);
    private final FallActivity fallActivity = new FallActivity(this);
    private final RestActivity restActivity = new RestActivity(this);
    private final WorkActivity workActivity = new WorkActivity(this);
    private final FollowActivity followActivity = new FollowActivity(this);
    private final AttackActivity attackActivity = new AttackActivity(this);

    private Activity currentActivity = noneActivity;
    private Activity lastActivity = noneActivity;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Creates a new unit with the given position and attributes.
     *
     * @param   name
     *          The name of the new unit.
     * @param   x
     *          The initial x value of the position.
     * @param   y
     *          The initial y value of the position.
     * @param   z
     *          The initial z value of the position.
     * @param   weight
     *          The initial weight of the unit.
     * @param   strength
     *          The initial strength of the unit.
     * @param   agility
     *          The initial agility of the unit.
     * @param   toughness
     *          The initial toughness of the unit.
     *
     * @post    Sets the hit points to their maximum value.
     *          | new.getHitPoints() == new.getMaxPoints()
     *          | && new.getStamina() == new.getMaxPoints()
     *
     * @effect  Set the name to the given name.
     *          | new.setName(name)
     * @effect  Set the position to the middle of the given block.
     *          | new.setPosition(new Vector(x + Lc/2, y + Lc/2, z + Lc/2))
     * @effect  Sets the orientation to 90 degrees.
     *          | new.setOrientation(Unit.INIT_ORIENTATION)
     * @effect  Sets the toughness to toughness clamped between 25 and 100
     *          | new.setToughness(Util.clamp(toughness, 25, 100))
     * @effect  Sets the agility to agility clamped between 25 and 100
     *          | new.setAgility(Util.clamp(agility, 25, 100))
     * @effect  Sets the weight to weight clamped between 25 and 100
     *          | new.setWeight(Util.clamp(weight, 25, 100))
     * @effect  Sets the strength to strength clamped between 25 and 100
     *          | new.setStrength(Util.clamp(strength, 25, 100))
     * @effect  Resets the XP.
     *          | new.resetXp()
     */
    @Raw
    public Unit(String name, int x, int y, int z, int weight, int strength, int agility, int toughness)
            throws IllegalArgumentException, InvalidPositionException {
        setName(name);
        Vector position = new Vector(x, y, z).add(Terrain.Lc/2);
        setPosition(position);
        setOrientation(INIT_ORIENTATION);

        setToughness(Util.clamp(toughness, 25, 100));
        setAgility(Util.clamp(agility, 25, 100));
        setWeight(Util.clamp(weight, 25, 100));
        setStrength(Util.clamp(strength, 25, 100));

        resetXp();

        int maxPoints = getMaxPoints();
        setHitPoints(maxPoints);
        setStamina(maxPoints);


        setRestMinuteTimer(REST_MINUTE);
    }

    /**
     * Terminates the current unit.
     *
     * @post    The unit is not doing any activities.
     *          | !isMoving() && !isWorking() && !isResting() && !isAttacking()
     * @post    The unit's hit points will be 0.
     *          | new.getHitPoints() == 0
     * @post    The unit is not alive.
     *          | new.isAlive() == false
     * @post    The unit is removed from the world.
     *          | !this.getWorld().getUnits().contains(this)
     * @post    The unit's world will be set to null
     *          | new.getWorld() == null
     *
     * @effect  Stop the task the unit is running, if applicable.
     *          | if (this.getFaction() != null) then this.stopTask()
     * @effect  If the unit is carrying an item, drop it.
     *          | if (this.isCarryingBoulder() || this.isCarryingLog())
     *          |   this.dropCarry(this.getPosition().toIntVector())
     */
    public void terminate() {
        if (this.isCarryingBoulder() || this.isCarryingLog())
            this.dropCarry(this.getPosition().toIntVector());

        this.setCurrentActivity(null);
        this.setLastActivity(null);

        if (this.getFaction() != null) {
            this.stopTask();
        }

        this.setHitPoints(0);
        if (this.getWorld() != null)
            this.getWorld().removeUnit(this);
        setWorld(null);
    }

    /**
     * Sets the world of the unit.
     *
     * @param   world
     *          The world to which the unit is added.
     *
     * @post    The unit's world will be set to the given world.
     *          | new.getWorld() == world
     */
    @Raw
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Returns the world this unit belongs to.
     */
    @Basic
    public World getWorld() {
        return this.world;
    }

    /**
     * Returns the pathfinder of the unit
     *
     * @return  The pathfinder of the world the unit is in.
     *          | result == this.getWorld().getPathFinder()
     */
    @Model // model for moveTo -> unreachable.
    PathFinder<IntVector> getPathFinder() {
        return getWorld().getPathFinder();
    }
    //</editor-fold>

    //<editor-fold desc="advanceTime">
    /**
     * Updates the units state.
     * TODO
     *
     * @param   dt
     *          The time step taken between frames.
     *
     * @post    If the unit isn't alive then this method has no effect.
     * @post    If the unit is moving then:
     *          0: If default behaviour is enabled, the unit has a small chance to start sprinting.
     *          1: If the unit is sprinting the unit's stamina is decreased with one:
     *              1.1: if the stamina timer is smaller than 0, the unit's stamina is decreased with 1.
     *              1.2: if the unit's stamina is zero, the unit stops sprinting.
     *          2: If the unit isn't sprinting and the default behaviour is enabled,
     *                  the unit has a small change to start sprinting.
     *          3: The next position is calculated as position + v*dt.
     *          4: If the next position is further away from the target neighbour the unit has arrived
     *                  and the unit's position is set to the position of the target neighbour.
     *                  3.1: If we have a pendingActivity we switch to that activity.
     *                  3.2: If the unit is at the target or the unit has no other target, the unit stops moving.
     *                  3.3: Otherwise the unit moves to the next neighbour.
     *          5: Otherwise we set the position to the next position.
     *          6: The unit gains 1 xp for each cube he moved.
     * @post    If the unit is working then the workTimer is decreased.
     *          If the work is finished then the unit does the following:
     *              If the unit carries a boulder or log, the boulder or log is dropped at the centre
     *              of the cube targeted by the labour action.
     *              Else if the target cube is a workshop and one boulder and one log are available on
     *              that cube, the unit will improve their equipment, consuming one boulder and one log
     *              (from the workshop cube), and increasing the unit's weight and toughness.
     *              Else if a boulder is present on the target cube, the unit shall pick up the boulder.
     *              Else if a log is present on the target cube, the unit shall pick up the log.
     *              Else if the target cube is wood, the cube collapses leaving a log.
     *              Else if the target cube is rock, the cube collapses leaving a boulder.
     * @post    The attack timer decreases and if the timer is 0 then the unit can attack again.
     * @post    If the unit is resting, the restTimer is decreased. If the timer runs out the timer is first reset.
     *              If the unit can heal HP, we heal HP otherwise the unit heals stamina,
     *              if neither can be increased the unit stops resting.
     * @post    If the unit is currently not conducting an activity,
     *              the unit continues an interrupted activity if it exists.
     *              If it doesn't exist and the default behaviour is enabled, the unit will conduct a random activity.
     * @post    If the unit is not standing on a passable position, the unit falls.
     *          Falling moves the unit down with the falling speed, if the new position is above a solid cube or the world ground,
     *          the unit will take damage equal to 10 times the amount off cubes he has traveled.
     *          Else the unit will keep falling and his position will be set to the new position.
     * @post    We also check if three minutes have past, if so we start resting and reset this timer.
     *
     * @effect  Stops sprinting if the unit's stamina is depleted or if the unit arrived at the target.
     *          Also starts sprinting randomly when default behavior is enabled.
     *          | this.setSprint(false)
     * @effect  If the unit passes the centre of the target neighbouring cube this dt, the position of the unit
     *          is set to the centre of this neighbouring cube.
     *          | this.setPosition()
     * @effect  If work is done, the unit receives 10 xp.
     *          | this.addXp()
     * @effect  A unit will take falling damage.
     *          | this.deduceHitPoints()
     *
     * @throws  IllegalArgumentException
     *          The given time step was to big or negative.
     */
    public void advanceTime(double dt) throws IllegalArgumentException {
        if (dt < 0 || dt >= 0.2)
            throw new IllegalArgumentException("Invalid dt");
        if (!isAlive())
            return;

        getCurrentActivity().advanceTime(dt);

        setRestMinuteTimer(getRestMinuteTimer()-dt);
        if (getRestMinuteTimer() <= 0 && this.canSwitchActivity()) {
            setRestMinuteTimer(getRestMinuteTimer() + REST_MINUTE);
            rest();
        }

        if (!isStablePosition(getPosition().toIntVector()) && !isFalling()) {
            getCurrentActivity().pause();
            getCurrentActivity().reset();
            this.getFallActivity().startFalling();
            setCurrentActivity(this.getFallActivity());

            getLastActivity().pause();
            getLastActivity().reset();
            setLastActivity(this.getNoneActivity());
        }


    }
    //</editor-fold>

    //<editor-fold desc="Activity">
    /**
     * Returns the MoveActivity for this unit.
     */
    @Basic @Immutable @Model
    private MoveActivity getMoveActivity() {
        return this.moveActivity;
    }

    /**
     * Returns the WorkActivity for this unit.
     */
    @Basic @Immutable @Model
    private WorkActivity getWorkActivity() {
        return this.workActivity;
    }

    /**
     * Returns the AttackActivity for this unit.
     */
    @Basic @Immutable @Model
    private AttackActivity getAttackActivity() {
        return this.attackActivity;
    }

    /**
     * Returns the RestActivity for this unit.
     */
    @Basic @Immutable @Model
    private RestActivity getRestActivity() {
        return this.restActivity;
    }

    /**
     * Returns the FallActivity for this unit.
     */
    @Basic @Immutable @Model
    private FallActivity getFallActivity() {
        return this.fallActivity;
    }

    /**
     * Returns the NoneActivity for this unit.
     */
    @Basic @Immutable @Model
    private NoneActivity getNoneActivity() {
        return this.noneActivity;
    }

    /**
     * Returns the FollowActivity for this unit.
     */
    @Basic @Immutable @Model
    private FollowActivity getFollowActivity() {
        return this.followActivity;
    }

    /**
     * Returns the current activity.
     */
    @Basic @Model
    Activity getCurrentActivity() {
        return this.currentActivity;
    }

    /**
     * Set the current activity.
     *
     * @param   newActivity
     *          The new Activity
     *
     * @post    The new Activity will be set
     *          | new.getCurrentActivity() == newActivity
     */
    @Model
    private void setCurrentActivity(Activity newActivity) {
        this.currentActivity = newActivity;
    }

    /**
     * Returns whether or not the unit can do the given Activity.
     *
     * @return  True if the current activity can switch.
     *          | result == this.getCurrentActivity().canSwitch() TODO: canSwitch model?
     */
    @Model // model for throws & stuff
    private boolean canSwitchActivity() {
        return getCurrentActivity().canSwitch();
    }

    /**
     * Sets the current activity.
     *
     * @param   newActivity
     *          The new activity.
     *
     * TODO
     * @post    If we are moving and the newActivity is not moveActivity, we set the pendingActivity.
     *          Otherwise we set the currentActivity and update the lastActivity.
     *          | if (this.getCurrentActivity() == this.getMoveActivity() &&
     *          |     newActivity != this.getMoveActivity() &&
     *          |     newActivity != this.getMoveActivity().getPendingActivity())
     *          |   then new.getMoveActivity().getPendingActivity() == newActivity
     *          | else
     *          |   ((if (this.getCurrentActivity() != newActivity
     *          |       then new.lastActivity == this.getCurrentActivity())
     *          |    then new.getLastActivity() == this.getCurrentActivity())
     *          |  && new.getCurrentActivity() == newActivity)
     *
     * @throws  InvalidActionException
     *          If the unit can't switch activities
     *          | !this.canSwitchActivity()
     */
    @Model
    void switchActivity(Activity newActivity) throws InvalidActionException {
        if (!canSwitchActivity())
            throw new InvalidActionException("can't change activity");

        if (getCurrentActivity() == getMoveActivity() && newActivity != getMoveActivity().getPendingActivity()) {
            if (newActivity != getMoveActivity()) {
                if (getMoveActivity().getPendingActivity() != null)
                    getMoveActivity().getPendingActivity().reset();
                getMoveActivity().setPendingActivity(newActivity);
            }
        } else if (newActivity != getCurrentActivity()) {
            if (getLastActivity() != newActivity)
                getLastActivity().reset();
            setLastActivity(getCurrentActivity());
            getLastActivity().pause();
            setCurrentActivity(newActivity);
            getCurrentActivity().resume();
        }
    }

    /**
     * Finishes the current activity.
     * @effect  Pauses and resets the currentActivity.
     *          | this.getCurrentActivity().pause() && this.getCurrentActivity().reset()
     * @effect  Switch to the last activity and resume it.
     *          | this.setCurrentActivity(this.getLastActivity()) && this.getLastActivity().resume()
     * @effect  Clear the lastActivity.
     *          | this.setLastActivity(this.getNoneActivity())
     */
    void finishCurrentActivity() {
        getCurrentActivity().pause();
        getCurrentActivity().reset();

        setCurrentActivity(getLastActivity());
        getCurrentActivity().resume();

        setLastActivity(this.getNoneActivity());
    }


    /**
     * Returns the last activity.
     */
    @Basic
    Activity getLastActivity() {
        return lastActivity;
    }

    /**
     * Sets the last Activity.
     *
     * @param   lastActivity
     *          The last activity to be set.
     *
     * @post    The lastActivity will be set.
     *          | new.getLastActivity() == lastActivity
     */
    @Model
    private void setLastActivity(Activity lastActivity) {
        this.lastActivity = lastActivity;
    }
    //</editor-fold>

    //<editor-fold desc="Position">
    /**
     * Check whether the cube is a stable position for unit's to be at.
     * If the unit isn't on a stable position it will fall.
     *
     * @param   cube
     *          The cube to check.
     *
     * @return  True if the cube is next to a border of the world or if it has any solid neighbours.
     *          | result == (cube.getX() == 0 || cube.getX() == world.getTerrain().getMaxX() - 1 ||
     *          |            cube.getY() == 0 || cube.getY() == world.getTerrain().getMaxY() - 1 ||
     *          |            cube.getZ() == 0 || cube.getZ() == world.getTerrain().getMaxX() - 1 ||
     *          |            Terrain.getNeighbours(cube).anyMatch(p -> World.isSolid(world.getCubeType(p))))
     */
    public static boolean isStablePosition(World world, IntVector cube) {
        return  cube.getX() == 0 || cube.getX() == world.getTerrain().getMaxX() - 1 ||
                cube.getY() == 0 || cube.getY() == world.getTerrain().getMaxY() - 1 ||
                cube.getZ() == 0 || cube.getZ() == world.getTerrain().getMaxZ() - 1 ||
                Terrain.getNeighbours(cube).anyMatch(p -> Terrain.isSolid(world.getTerrain().getCubeType(p)));
    }

    /**
     * Check whether the cube is a stable position for unit's to be at.
     *
     * @param   cube
     *          The cube to check.
     *
     * @return  Returns true if the position is stable in the current world.
     *          | result == Unit.isStablePosition(this.getWorld(), cube)
     */
    public boolean isStablePosition(IntVector cube) {
        return Unit.isStablePosition(getWorld(), cube);
    }

    /**
     * Checks whether the given position is a valid position for the unit to be at.
     *
     * @param   world
     *          The world to check in.
     * @param   cubePos
     *          The position to be checked.
     *
     * @return  True if the given position is within the boundaries of the world and if it is not solid or
     *          if the world is null.
     *          | result == (world == null)
     *          |           || ((world.isValidPosition(cubePos))
     *          |               && (!Terrain.isSolid(world.getTerrain().getCubeType(cubePos)))
     */
    public static boolean isValidPosition(World world, IntVector cubePos) {
        return  world == null ||
                (world.getTerrain().isValidPosition(cubePos) &&
                    !Terrain.isSolid(world.getTerrain().getCubeType(cubePos)));
    }

    /**
     * Checks whether the given position is a valid position for the unit to be at.
     *
     * @param   pos
     *          The position to be checked.
     *
     * @return  Check if the position is valid in the current world.
     *          | result == Unit.isValidPosition(this.getWorld(), pos)
     */
    public boolean isValidPosition(IntVector pos) {
        return Unit.isValidPosition(getWorld(), pos);
    }


    /**
     * Checks whether the given position is effective.
     *
     * @param   position
     *          The position to be tested.
     *
     * @return  True if the position is effective.
     *          | result == (position != null)
     */
    private static boolean isEffectivePosition(Vector position) {
        return position != null;
    }

    /**
     * Sets the position of the unit.
     *
     * @param   position
     *          The new position for this unit.
     *
     * @post    The new position is equal to the given position.
     *          | new.getPosition() == position
     *
     * @throws  InvalidPositionException
     *          When the given position is not valid or not effective.
     *          | !isValidPosition(position) || !isEffectivePosition(position)
     */
    public void setPosition(Vector position) throws InvalidPositionException {
        if (!isEffectivePosition(position) || !isValidPosition(position.toIntVector()))
            throw new InvalidPositionException(position);
        this.position = position;
    }

    /**
     * Gets the current position as a vector.
     */
    @Basic
    public Vector getPosition() {
        return this.position;
    }
    //</editor-fold>

    //<editor-fold desc="Name">
    /**
     * Checks whether the name is valid.
     *
     * @param   name
     *          The name to be checked.
     *
     * @return  True if name is at least 2 characters long,
     *          starts with an uppercase letter and contains only letters, spaces and quotes.
     *          | result == (name.length() > 2) && name.matches("[A-Z][a-zA-Z'\" ]*")
     */
    private static boolean isValidName(String name) {
        return name.length() > 2 && name.matches("[A-Z][a-zA-Z'\" ]*");
    }

    /**
     * Returns the name of the unit.
     */
    @Basic
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the unit.
     *
     * @param   name
     *          The new name for the unit.
     *
     * @post    The new name is the given name.
     *          | new.getName() == name
     *
     * @throws  IllegalArgumentException
     *          The name is not valid.
     *          | !isValidName(name)
     */
    @Raw
    public void setName(String name) throws IllegalArgumentException {
        if (!isValidName(name))
            throw new IllegalArgumentException("Invalid name");
        this.name = name;
    }
    //</editor-fold>

    //<editor-fold desc="Attributes">
    /**
     * Returns whether or not the weight is valid.
     * (only used in class invariant)
     *
     * @param   weight
     *          The weight to be verified.
     *
     * @return  Returns true if the weight is larger or equal to MIN_ATTRIBUTE,
     *          larger or equal to (strength + agility)/2,
     *          smaller or equal to MAX_ATTRIBUTE.
     *          | result ==  (weight >= MIN_ATTRIBUTE) && (weight <= MAX_ATTRIBUTE) && (weight >= (getAgility()+getStrength())/2)
     */
    @SuppressWarnings("unused")
    public boolean canHaveAsWeight(int weight) {
        return (weight >= MIN_ATTRIBUTE) && (weight <= MAX_ATTRIBUTE) && (weight >= (getAgility()+getStrength())/2);
    }

    /**
     * Returns the weight of the unit with carry weight included.
     *
     * @return  Returns the total weight of the unit.
     *          | result == getBasicWeight() + getCarryWeight()
     */
    public int getWeight() {
        return getBasicWeight() + getCarryWeight();
    }

    /**
     * Returns the weight of the unit without any carry weight.
     */
    @Basic
    private int getBasicWeight() {
        return this.weight;
    }

    /**
     * Returns the weight of the gameObject which the unit is carrying.
     *
     * @return  Returns the weight which the unit is carrying.
     *          | if (this.isCarryingBoulder())
     *          |   then result == this.getCarryBoulder().getWeight()
     *          | else if (isCarryingLog())
     *          |   then result == this.getCarryLog().getWeight()
     *          | else
     *          |   result == 0
     */
    @Model // TODO: cascade model?
    private int getCarryWeight() {
        return (isCarryingBoulder() ? getCarryBoulder().getWeight() : (isCarryingLog() ? getCarryBoulder().getWeight() : 0));
    }

    /**
     * Sets the units weight to the new weight.
     *
     * @param   weight
     *          The new weight.
     *
     * @post    The new weight is weight clamped between (strength + agility / 2) and MAX_ATTRIBUTE
     *          | new.getBasicWeight() == Util.clamp(((this.getStrength() + this.getAgility()) / 2), MAX_ATTRIBUTE)
     */
    @Model
    public void setWeight(int weight) {
        int min = (this.getStrength() + this.getAgility()) / 2;
        weight = Util.clamp(weight, min, MAX_ATTRIBUTE);
        this.weight = weight;
    }

    /**
     * Checks whether the attribute is valid.
     * (only used in class invariant)
     *
     * @param   attribute
     *          The strength to be verified.
     *
     * @return  Returns true if the attribute is larger or equal to MIN_ATTRIBUTE
     *          and smaller or equal to MAX_ATTRIBUTE.
     *          | result == strength <= MAX_ATTRIBUTE && strength >= MIN_ATTRIBUTE
     */
    @SuppressWarnings("unused")
    public static boolean isValidAttribute(int attribute) {
        return attribute <= MAX_ATTRIBUTE && attribute >= MIN_ATTRIBUTE;
    }

    /**
     * Returns the strength of the unit.
     */
    @Basic
    public int getStrength() {
        return this.strength;
    }

    /**
     * Sets the strength of the unit.
     *
     * @param   strength
     *          The new strength for the unit.
     *
     * @post    The strength is set to strength clamped between MIN_ATTRIBUTE and MAX_ATTRIBUTE.
     *          | new.getStrength() == Util.clamp(strength, MIN_ATTRIBUTE, MAX_ATTRIBUTE);
     *
     * @effect  The weight is corrected.
     *          | this.setWeight(this.getBasicWeight())
     */
    @Raw
    public void setStrength(int strength) {
        this.strength = Util.clamp(strength, MIN_ATTRIBUTE, MAX_ATTRIBUTE);
        setWeight(getBasicWeight());
    }

    /**
     * Returns the agility of the unit.
     */
    @Basic
    public int getAgility() {
        return this.agility;
    }

    /**
     * Sets the agility of the unit.
     *
     * @param   agility
     *          The new agility.
     *
     * @post    If the given agility is less then MIN_ATTRIBUTE, the new agility is MIN_ATTRIBUTE.
     *          | new.getAgility() == Util.clamp(agility, MIN_ATTRIBUTE, MAX_ATTRIBUTE)
     *
     * @effect  The weight is corrected.
     *          | this.setWeight(this.getBasicWeight())
     */
    @Raw
    public void setAgility(int agility) {
        this.agility = Util.clamp(agility, MIN_ATTRIBUTE, MAX_ATTRIBUTE);
        setWeight(getBasicWeight());
    }

    /**
     * Returns the toughness of the current unit
     */
    @Basic
    public int getToughness() {
        return this.toughness;
    }

    /**
     * Sets the toughness of the current unit
     *
     * @param   toughness
     *          The new toughness.
     *
     * @post    If the given toughness is less then MIN_ATTRIBUTE, the new toughness is MIN_ATTRIBUTE.
     *          | new.getToughness() == Util.clamp(toughness, MIN_ATTRIBUTE, MAX_ATTRIBUTE)
     */
    @Raw
    public void setToughness(int toughness) {
        this.toughness = Util.clamp(toughness, MIN_ATTRIBUTE, MAX_ATTRIBUTE);
    }

    /**
     * Returns whether the hitPoints are valid.
     *
     * @param   hitPoints
     *          The hit points to be verified.
     *
     * @return  Returns true if the hitPoints are larger than or equal to 0
     *          and smaller than or equal to the maximum amount of hitPoints.
     *          | result == (hitPoints <= getMaxPoints()) && (hitPoints >= 0)
     */
    public boolean canHaveAsHitPoints(int hitPoints) {
        return hitPoints <= getMaxPoints() && hitPoints >= 0;
    }

    /**
     * Returns the current amount of hitPoints of the unit
     */
    @Basic
    public int getHitPoints() {
        return this.hitPoints;
    }

    /**
     * Sets the current hitPoints to the given amount of hitPoints.
     *
     * @param   hitPoints
     *          The new hitPoints of the unit.
     *
     * @pre     The hitPoints must be greater or equal than 0 and smaller or equal than max hitPoints.
     *          | (hitPoints <= getMaxPoints()) && (hitPoints >= 0)
     *
     * @post    The new hitPoints equal the given hitPoints.
     *          | new.getHitPoints() == hitPoints
     *
     */
    @Raw @Model
    void setHitPoints(int hitPoints) {
        assert canHaveAsHitPoints(hitPoints);
        this.hitPoints = hitPoints;
    }

    /**
     *  Deduces the given amount hit points from the unit's hp.
     *
     * @param   hitPoints
     *          The Amount of hit points to be deduced.
     *
     * @post    The unit's new hp shall be equal to the its old hp deduced by the given hitPoints or
     *          zero if the new hp would otherwise be negative
     *          | if ( (old.getHitPoints() - hitPoints) > 0 )
     *          |   then new.getHitPoints ()== old.getHitPoints() - hitPoints
     *          | else
     *          |   this.terminate()
     *
     * @effect  If the units hp hits 0 or goes below zero the unit will be terminated
     *          | this.terminate()
     */
    @Model
    void deduceHitPoints(int hitPoints)  {
        int newHitPoints = this.getHitPoints() - hitPoints;
        if (newHitPoints <= 0) {
            this.terminate();
        } else {
            this.setHitPoints(newHitPoints);
        }
    }

    /**
     * Returns whether the unit is alive.
     *
     * @return  Returns true if the unit's hp is greater than 0.
     *          | result == this.getHitPoints() > 0
     */
    public boolean isAlive() {
        return (this.getHitPoints() > 0);
    }

    /**
     * Returns whether the stamina is valid.
     *
     * @param   stamina
     *          The stamina to be verified.
     *
     * @return  Returns true if the amount of stamina is larger than or equal to 0
     *          and smaller than or equal to the maximum amount of hitPoints.
     *          | result == (stamina <= getMaxPoints()) && (stamina >= 0)
     */
    public boolean canHaveAsStamina(int stamina) {
        return stamina <= getMaxPoints() && stamina >= 0;
    }

    /**
     * Returns the current stamina of the unit.
     */
    @Basic
    public int getStamina() {
        return this.stamina;
    }

    /**
     * Sets the current amount of stamina to the given stamina.
     *
     * @param   stamina
     *          The new amount of stamina of the unit
     *
     * @pre     The stamina must be greater or equal than 0 and smaller or equal than max stamina
     *          | (stamina <= getMaxPoints()) && (stamina >= 0)
     *
     * @post    The new stamina equals the given stamina.
     *          | new.getStamina() == stamina
     *
     */
    @Raw @Model
    void setStamina(int stamina){
        assert canHaveAsStamina(stamina);
        this.stamina = stamina;
    }

    /**
     * Return the maximum amount of hitPoints and stamina.
     *
     * @return  Returns 200*(weight/100)*(toughness/100) as an integer
     *          | result == ceil(200*weight*toughness/10000)
     */
    public int getMaxPoints() {
        return (200*weight*toughness+9999)/10000;
    }
    //</editor-fold>

    //<editor-fold desc="Orientation">
    /**
     * Returns whether the orientation is valid.
     * (only used in class invariant)
     *
     * @param   orientation
     *          The orientation to be verified.
     *
     * @return  Returns true if the orientation is equal to or larger than 0
     *          and smaller than 2*PI.
     *          | result == (orientation < 2*Math.PI) && (orientation >= 0)
     */
    @SuppressWarnings("unused")
    public static boolean isValidOrientation(double orientation) {
        return orientation < 2*Math.PI && orientation >= 0;
    }

    /**
     * Returns the current orientation.
     */
    @Basic
    public double getOrientation() {
        return this.orientation;
    }

    /**
     * Sets the orientation of the unit.
     *
     * @param   orientation
     *          The new orientation.
     *
     * @post    The new orientation is the same as the old orientation but between 0 and 2*PI.
     *          | new.getOrientation() == ((2*Math.PI) + (orientation % (2*Math.PI))) % 2* Math.PI
     */
    @Raw @Model
    void setOrientation(double orientation) {
        this.orientation = ((Math.PI*2) + (orientation % (2*Math.PI))) % (2*Math.PI);
    }
    //</editor-fold>

    //<editor-fold desc="Movement">
    /**
     * Returns True if the unit is moving.
     *
     * @return  True if the unit is moving, falling or following.
     *          | result == this.getCurrentActivity() == this.getMoveActivity() ||
     *          |           this.getCurrentActivity() == this.getFallActivity() ||
     *          |           this.getCurrentActivity() == this.getFollowActivity()
     */
    public boolean isMoving() {
        return  this.getCurrentActivity() == this.getMoveActivity() ||
                this.getCurrentActivity() == this.getFallActivity() ||
                this.getCurrentActivity() == this.getFollowActivity();
    }

    /**
     * Makes the unit move towards one of the adjacent cubes.
     *TODO
     * @param   dx
     *          the x direction
     * @param   dy
     *          the y direction
     * @param   dz
     *          the z direction
     *
     * @post    The unit will be moving
     *          | new.isMoving() == true
     *
     * @effect  The unit will point to the target cube.
     *          | new.getOrientation() == Math.atan2(dy, dx)
     *
     * @throws  InvalidPositionException
     *          If the target cube is not within the world bounds
     *          | !this.isValidPosition(this.getPosition().add(dx, dy, dz))
     *          | || !this.isStablePosition(this.getPosition().add(dx, dy, dz))
     * @throws  IllegalArgumentException
     *          If the dx, dy or dz values aren't -1, 0 or +1
     *          | Math.abs(dx) > 1 || Math.abs(dy) > 1 || Math.abs(dz) > 1
     * @throws  InvalidActionException
     *          If the unit can't move right now
     *          | !this.canSwitchActivity()
     */
    public void moveToAdjacent(int dx, int dy, int dz)
            throws IllegalArgumentException, InvalidActionException, InvalidPositionException {

        if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || Math.abs(dz) > 1)
            throw new IllegalArgumentException("Illegal dx, dy and/or dz");

        this.getMoveActivity().updateAdjacent(dx, dy, dz);
        this.switchActivity(this.getMoveActivity());
    }


    /**
     * Starts the units movement to the given target cube.
     *TODO
     * @param   target
     *          The coordinates of the target cubes.
     *
     * @post    The unit starts moving.
     *          | new.isMoving() == true
     * @post    The unit will move to the target when calling advanceTime().
     *          | new.getPosition == target.add(Lc/2)
     *
     * @throws  InvalidPositionException
     *          If the given target is not valid.
     *          | !isValidPosition(target[0], target[1], target[2])
     * @throws  InvalidActionException
     *          If the unit can't move.
     *          | !this.canHaveAsActivity(MOVE_ACTIVITY_CLASS)
     * @throws  UnreachableTargetException
     *          If the unit can't reach the target.
     *          | !this.getPathFinder().isReachable(this.getPosition().toIntVector(), target)
     */
    public void moveTo(IntVector target)
            throws InvalidPositionException, InvalidActionException, UnreachableTargetException {

        this.getMoveActivity().updateTarget(target);
        this.switchActivity(this.getMoveActivity());
    }


    /**
     * Makes the current unit follow the given unit.
     *
     * @param   other
     *          The unit to be followed.
     *
     * @post    The unit starts moving.
     *          | new.isMoving() == true
     * @post    The unit will move to a position next to the other unit when calling advanceTime().
     *          | new.getPosition().toIntVector.().isNextTo(new.other.getPosition)
     * @post    The unit will stop moving if the other unit is dead.
     *          | if (!new.other.isAlive())
     *          |   then new.getPosition() == current.getPosition()
     *
     * @throws  InvalidActionException
     *          Never throws this exception.
     * @throws  InvalidPositionException
     *          Throws if the other's unit position is not a valid position.
     *          | !((getWorld().isValidPosition(cubePos)) && (!World.isSolid(world.getCubeType(cubePos))) ||
     *          |           (this.getWorld() == null))
     * @throws  UnreachableTargetException
     *          Throws if the position of the other unit is unreachable.
     *          | this.getPathFinder(this.getPosition().toIntVector(), target.toIntVector()) == null
     */
    public void follow(Unit other) throws InvalidActionException,
            InvalidPositionException, UnreachableTargetException, InvalidUnitException {
        this.getFollowActivity().setOther(other);
        this.switchActivity(this.getFollowActivity());
    }
    //</editor-fold>

    //<editor-fold desc="SpeedNSprint">
    /**
     * Returns the speed of the unit. (if the unit is sprinting, the speed is doubled)
     */
    @Basic @Model
    Vector getSpeed() {
        if (this.isSprinting())
            return this.speed.multiply(2);
        else
            return this.speed;
    }

    /**
     * Sets the speed of the unit to the given value.
     *
     * @param   speed
     *          The value to set the speed to.
     *
     * @post    The new speed of the unit will equal the given speed.
     *          | new.getSpeed() == speed
     */
    void setSpeed(Vector speed) {
        this.speed = speed;
    }

    /**
     * Gets the unit's movement speed.
     *
     * @return  Returns the norm of the speed or zero if the speed is null.
     *          | if (this.getSpeed() == null) then result == 0
     *          | else result == this.getSpeed().norm()
     */
    public double getSpeedScalar() {
        return getSpeed() == null ? 0.0 : getSpeed().norm();
    }

    /**
     * Returns True if the unit is sprinting
     */
    @Basic
    public boolean isSprinting() {
        return this.sprinting;
    }

    /**
     * Sets sprinting to the given boolean.
     *
     * @param   newSprint
     *          The boolean to set sprinting to.
     *
     * @post    Sprinting will equal the given boolean
     *          | new.isSprinting() == newSprint
     *
     * @throws  InvalidActionException
     *          Thrown if the unit can't sprint
     *          | newSprint && (this.getStamina() == 0 || !this.isMoving() || this.isFalling())
     */
    public void setSprinting(boolean newSprint) throws InvalidActionException {
        if (newSprint && (getStamina() == 0 || !isMoving() || isFalling()))
            throw new InvalidActionException("Can't sprint right now");

        this.sprinting = newSprint;
    }
    //</editor-fold>

    //<editor-fold desc="Working">
    /**
     * Returns True if the unit is working
     *
     * @return  True if the unit is working.
     *          | result == this.getCurrentActivity() == this.getWorkActivity()
     */
    public boolean isWorking() {
        return this.getCurrentActivity() == this.getWorkActivity();
    }

    /**
     * The unit starts working at the given location.
     *
     * @post    Makes the unit start working
     *          | new.isWorking() == True
     *
     * @throws  InvalidActionException
     *          Throws if the unit can't work.
     *          | (!canHaveAsActivity(WORK_ACTIVITY_CLASS)
     * @throws  InvalidPositionException
     *          Throws if the location is out of range
     *          | (Math.abs(diff.getX()) > 1 || Math.abs(diff.getY()) > 1 || Math.abs(diff.getZ()) > 1)
     * @throws  InvalidPositionException
     *          Throws if the location isn't valid.
     *          | !this.getWorld().isValidPosition(location)
     */
    public void workAt(IntVector location) throws InvalidActionException, InvalidPositionException {
        this.getWorkActivity().workAt(location);
        this.switchActivity(this.getWorkActivity());
    }

    /**
     * Returns the log the unit is carrying or null.
     */
    @Basic
    private Log getCarryLog() {
        return carryLog;
    }

    /**
     * Sets the log the unit is carrying.
     *
     * @param   carryLog
     *          The log to carry
     *
     * @post    The unit will be carrying carryLog
     *          | new.getCarryLog() == carryLog
     */
    private void setCarryLog(Log carryLog) {
        this.carryLog = carryLog;
    }

    /**
     * Returns the Boulder the unit is carrying or null.
     */
    @Basic
    private Boulder getCarryBoulder() {
        return carryBoulder;
    }

    /**
     * Sets the boulder the unit is carrying.
     *
     * @param   carryBoulder
     *          The boulder to carry
     *
     * @post    The unit will be carrying carryBoulder
     *          | new.getCarryBoulder() == carryBoulder
     */
    private void setCarryBoulder(Boulder carryBoulder) {
        this.carryBoulder = carryBoulder;
    }

    /**
     *  Returns whether the unit is carrying a log.
     *
     *  @return     True if the unit is carrying a log.
     *              | result == (this.getCarryLog() == null)
     */
    public boolean isCarryingLog() {
        return this.getCarryLog() != null;
    }

    /**
     * Returns whether the unit is carrying a boulder.
     *
     * @return      True if the unit is carrying a boulder.
     *              | result == (this.getCarryBoulder() == null)
     */
    public boolean isCarryingBoulder()  {
        return this.getCarryBoulder() != null;
    }

    /**
     * Makes the unit drop whatever it is carrying.
     *
     * @param   workLoc
     *          The location to drop the object.
     *
     *TODO
     */
    void dropCarry(IntVector workLoc) {
        if (isCarryingLog()) {
            this.getCarryLog().setPosition(workLoc.toVector().add(Terrain.Lc/2));
            getWorld().addGameObject(this.getCarryLog());
            this.setCarryLog(null);
        } else if (isCarryingBoulder()) {
            this.getCarryBoulder().setPosition(workLoc.toVector().add(Terrain.Lc/2));
            getWorld().addGameObject(this.getCarryBoulder());
            this.setCarryBoulder(null);
        }
    }

    /**
     * Picks up the given log.
     *
     * @param   log
     *          The log to pick up.
     *
     * TODO
     */
    void pickUpLog(Log log) {
        this.setCarryLog(log);
        getWorld().removeGameObject(log);
    }

    /**
     * Picks up the given boulder.
     *
     * @param   boulder
     *          The boulder to pick up.
     *
     * TODO
     */
    void pickUpBoulder(Boulder boulder) {
        this.setCarryBoulder(boulder);
        getWorld().removeGameObject(boulder);
    }

    //</editor-fold>

    //<editor-fold desc="Fighting">
    /**
     * Returns True if the unit is attacking.
     *
     * @return  True if the unit is attacking.
     *          | result == this.getCurrentActivity() == this.getAttackActivity()
     */
    public boolean isAttacking() {
        return this.getCurrentActivity() == this.getAttackActivity();
    }

    /**
     * Attacks another unit.
     *
     * @param   other
     *          The unit that is attacked by this unit.
     *
     * @post    Makes the units look at each other.
     *          | new.getOrientation() == atan2((other.getPosition().getY() - this.getPosition().getY()),
     *          |       (other.getPosition().getX() - this.getPosition().getX()))
     *          | (new other).getOrientation() == atan2((this.getPosition().getY() - other.getPosition().getY()),
     *          |       (this.getPosition().getX() - other.getPosition().getX()))
     * @post    Makes the unit attack.
     *          | new.isAttacking == True
     *
     * @throws  InvalidUnitException
     *          Throws if the other unit is null or if the unit tries to attack itself or if the other unit is falling
     *          | (other == null || other == this || other.getCurrentActivity.equalsClass(FALL_ACTIVITY_CLASS))
     * @throws  InvalidActionException
     *          Throws if the unit can't attack.
     *          | !this.canSwitchActivity()
     * @throws  InvalidUnitException
     *          Throws if the other unit is not in attack range.
     *          | (!this.canAttack(other))
     * @throws  InvalidUnitException
     *          Throws if the other unit is in the same faction.
     *          | (this.getFaction() == other.getFaction())
     */
    public void attack(Unit other) throws InvalidActionException, InvalidUnitException {
        this.getAttackActivity().setTarget(other);
        this.switchActivity(this.getAttackActivity());
    }

    /**
     * Defends against another unit's attack.
     *
     * @param   attacker
     *          The unit that attacks this unit.
     *
     * @post    If the unit dodges the attack he jumps in the x and y direction
     *          both with a distance in range of -1 to 1 to a valid position.
     *          and the new position can not be equal to the original.
     *          The unit does not take any damage.
     *          The unit receives xp and the units look at each other.
     *          | if (random < (0.20 * this.getAgility() / attacker.getAgility()) )
     *          |   then (new.getPosition()[0] == old.getPosition[0] + 2 * Math.random -1 &&
     *          |       new.getPosition()[1] == old.getPosition[1] + 2 * Math.random -1) &&
     *          |       (new.getHitPoints == old.getHitPoints) &&
     *          Else if the attack is blocked, the unit will not take any damage and will get xp.
     *          | else if ( random < (0.25 * (this.getStrength + this.getAgility)/(other.getStrength + other.getAgility)))
     *          |   then ( new.getHitPoints == old.getHitPoints)
     *          Else if the attack hit the unit, it will take damage equal to the attacker's strength/10 and the attackers receives xp.
     *          | Else
     *          |   ( deduceHitPoints (attacker.getStrength() / 10) )
     *
     * @effect  Sets the position to the random position after dodging.
     *          | this.setPosition()
     * @effect  Deduces the given amount of hit points from the unit's hp.
     *          | this.deduceHitPoints()
     * @effect  Adds xp for dodge, block and successful attack.
     *          | if (...)
     *          | this.addXp(...)
     * @effect  Turns the units towards each other.
     *          | this.setOrientation()
     * @effect  Pauses the current activity.
     *          | this.getCurrentActivity().pause()
     * @efect   Resumes the current activity.
     *          | this.getCurrentActivity().resume()
     */
    void defend(Unit attacker) {
        getCurrentActivity().pause();
        double probabilityDodge = 0.20 * (this.getAgility() / attacker.getAgility());
        if (Math.random() < probabilityDodge) {
            Vector randPos;
            do {
                randPos = new Vector(getPosition().getX() -1 + Math.random() * 2,
                        getPosition().getY() -1 + Math.random() * 2,
                        getPosition().getZ());
            } while (!isValidPosition(randPos.toIntVector()));
            setPosition(randPos);
            // update orientation:
            Vector diff = attacker.getPosition().subtract(this.position);
            this.setOrientation(Math.atan2(diff.getY(), diff.getX()));
            attacker.setOrientation(Math.atan2(-diff.getY(), -diff.getX()));
            this.addXp(20);
        } else {
            double probabilityBlock = 0.25 *
                    ((this.getStrength()+this.getAgility())/(attacker.getStrength()+attacker.getAgility()));
            if (Math.random() >= probabilityBlock) {
                deduceHitPoints(attacker.getStrength() / 10);
                attacker.addXp(20);
            } else {
                this.addXp(20);
            }
        }
        getCurrentActivity().resume();
    }
    //</editor-fold>

    //<editor-fold desc="Resting">
    /**
     * Returns True if the unit is resting.
     *
     * @return  True if the current activity is restActivity.
     *          | result == this.getCurrentActivity() == this.getRestActivity()
     */
    public boolean isResting() {
        return this.getCurrentActivity() == this.getRestActivity();
    }

    /**
     * Starts resting.
     *
     * @post    The unit is resting.
     *          | new.isResting() == true
     *
     * @throws  InvalidActionException
     *          Throws if the unit is moving or attacking TODOs.
     *          | !this.canSwitchActivity()
     */
    public void rest() throws InvalidActionException {
        this.switchActivity(this.getRestActivity());
    }

    /**
     * Sets the rest timer.
     *
     * @param   val
     *          The new Value of the timer.
     *
     * @post    The timer will be set.
     *          | new.getRestMinuteTimer() == val
     */
    private void setRestMinuteTimer(double val) {
        this.restMinuteTimer = val;
    }

    /**
     * Returns the time left until the unit has to rest.
     */
    @Basic
    private double getRestMinuteTimer() {
        return this.restMinuteTimer;
    }
    //</editor-fold>

    //<editor-fold desc="Default behaviour">
    /**
     * Starts the default behavior.
     *
     * @post    The default behavior is enabled.
     *          | new.isDefaultEnabled() == true
     */
    public void startDefaultBehaviour() {
        this.defaultEnabled = true;
    }

    /**
     * Stops the default behavior.
     *
     * @post    The default behavior has stopped.
     *          | new.isDefaultEnabled() == false
     */
    public void stopDefaultBehaviour() {
        this.defaultEnabled = false;
    }

    /**
     * True if the default behaviour is enabled.
     */
    @Basic
    public boolean isDefaultEnabled() {
        return this.defaultEnabled;
    }
    //</editor-fold>

    //<editor-fold desc="Leveling and Xp">

    /**
     * Rests the xp and xpDiff
     *
     * @post    The xp will be set to zero.
     *          | new.getXp() == 0
     * @post    The xp difference will be set to zero.
     *          | new.getXpDiff() == 0
     */
    @Model
    private void resetXp() {
        this.xp = 0;
        this.xpDiff = 0;
    }

    /**
     * Adds the given xp to the xp counter and xp difference.
     *
     * @param   xp
     *          The xp to add
     *
     * @post    The units xp will be increased with xp.
     *          | new.getXp() == xp + this.getXp()
     * @post    XpDiff will be increased with xp.
     *          | new.getXpDiff() == xp + this.getXpDiff()
     */
    void addXp(int xp) {
        int oldXp = this.getXp();
        this.setXp(oldXp + xp);
        this.setXpDiff(oldXp + xp);
        levelUp();
    }

    /**
     * Returns the amount of xp.
     */
    @Basic
    public int getXp() {
        return this.xp;
    }

    /**
     * Sets the units xp.
     *
     * @param   xp
     *          The new xp value.
     *
     * @post    The xp will be set.
     *          | new.getXp() == xp
     */
    private void setXp(int xp) {
        this.xp = xp;
    }

    /**
     * Returns the xp difference.
     */
    @Basic @Model
    private int getXpDiff() {
        return xpDiff;
    }

    /**
     * Sets the Xp difference.
     * @param xpDiff
     */
    private void setXpDiff(int xpDiff) {
        this.xpDiff = xpDiff;
    }

    /**
     * Levels the unit.
     * TODO
     * @post    While the xpDiff is larger than or equal to 10, increase a random attribute that is not yet the maximum by 1.
     *          | while xpDiff >= 10
     *          | if (this.getStrength() < 200)
     *          |   attributes.add(0)
     *          | if (this.getAgility() < 200)
     *          |   attributes.add(1)
     *          | if (this.getStrength() < 200)
     *          |   attributes.add(2)
     *          | if (attributes.get(random) == 0)
     *          |   new.getStrength == old.getStrength + 1
     *          | if (attributes.get(random) == 1)
     *          |   new.getAgility == old.getAgility + 1
     *          | if (attributes.get(random) == 2)
     *          |   new.getToughness = old.getToughness + 1
     *
     * @effect  Update the weight of the unit if necessary.
     *          | this.setWeight(old.getWeight)
     */
    @Model
    private void levelUp() {
        while (this.getXpDiff() >= 10) {
            this.setXpDiff(this.getXpDiff() - 10);

            ArrayList<Integer> attributes = new ArrayList<>();
            if (this.getStrength() < 200)
                attributes.add(0);
            if (this.getAgility() < 200)
                attributes.add(1);
            if (this.getToughness() < 200)
                attributes.add(2);

            if (attributes.isEmpty())
                return;

            int rand = (int) Math.floor(Math.random()*attributes.size());
            int attr = attributes.get(rand);

            if (attr == 0)
                this.setStrength(this.getStrength() + 1);
            else if (attr == 1)
                this.setAgility(this.getAgility() + 1);
            else if (attr == 2)
                this.setToughness(this.getToughness() + 1);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Faction">
    /**
     * Returns the faction of the unit.
     */
    @Basic
    public Faction getFaction() {
        return this.faction;
    }

    /**
     * Sets the faction of the unit.
     *
     * @param   faction
     *          The faction to which the unit should be added.
     *
     * @post    The unit's faction is the given faction.
     *          | new.getFaction() == faction
     */
    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    //</editor-fold>

    //<editor-fold desc="Tasks">

    /**
     * Returns whether the unit has an assigned task.
     *
     * @return  ...
     *          | result == this.getAssignedTask() != null
     */
    public boolean hasAssignedTask() {
        return this.getAssignedTask() != null;
    }

    /**
     * Returns the unit's assigned task.
     */
    @Basic
    public Task getAssignedTask() {
        return this.task;
    }

    /**
     * Assigns a task to the unit.
     *
     * @param   task
     *          The task to assign.
     *
     * @post    The units task will be assigned.
     *          | new.getAssignedTask() == task
     */
    public void assignTask(Task task) {
        this.task = task;
    }


    public void finishTask() { // TODO: remove?
        if (hasAssignedTask())
            getAssignedTask().finish();
    }

    /**
     * Stops the assigned task
     *
     * @effect  Stop the assigned task if effective.
     *          | if (this.hasAssignedTask()) then this.getAssignedTask().interrupt()
     */
    private void stopTask() {
        if (hasAssignedTask())
            getAssignedTask().interrupt();

    }

    /**
     * Returns whether the unit is falling.
     *
     * @return  True if the unit is falling.
     *          | result == (this.getCurrentActivity() == this.getFallActivity())
     */
    public boolean isFalling() {
        return this.getCurrentActivity() == this.getFallActivity();
    }

    /**
     * Sets the tracker of the current activity.
     *
     * @param   tracker
     *          The tracker to set.
     *
     * @effect  Set the currentActivity's tracker.
     *          | this.getCurrentActivity().setTracker(tracker)
     */
    public void setActivityTracker(ActivityTracker tracker) {
        getCurrentActivity().setTracker(tracker);
    }

    //</editor-fold>
}
