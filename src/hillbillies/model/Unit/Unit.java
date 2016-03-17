package hillbillies.model.Unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import hillbillies.model.Faction;
import hillbillies.model.PathFinder;
import hillbillies.model.Vector;
import hillbillies.model.World;

import java.util.stream.Stream;

/**
 *
 * Names of students:
 *
 * Timothy Werquin      ingenieurswetenschappen (computerwetenschappen en elektrotechniek)
 * Florian Van Heghe    ingenieurswetenschappen (computerwetenschappen en elektrotechniek)
 *
 * Link to git repository:
 * https://gitlab.com/timothyw/OOP-project
 */

/**
 * TODO:
 *  - Check access modifiers
 *  - Create Integer Vector -> use that for pathfinding
 *  - Clean and refactor code
 */

/**
 * The unit class, this class keeps tracks of the unit's position, speed and other attributes.
 * It provides methods to move, to attack or to rest.
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
 * @invar   The Agility of the unit must be valid.
 *          | isValidAgility(this.getAgility())
 * @invar   The Toughness of the unit must be valid.
 *          | isValidToughness(this.getToughness())
 * @invar   The HitPoints must be valid.
 *          | canHaveAsHitPoints(this.getHitPoints())
 * @invar   The stamina must be valid.
 *          | canHaveAsStamina(this.getStamina())
 */
public class Unit {

    //<editor-fold desc="Constants">
    public static final double POS_EPS = 0.05;

    public static final double REST_MINUTE = 3*60;

    public static final int MIN_ATTRIBUTE = 1;
    public static final int MAX_ATTRIBUTE = 200;

    public static final double INIT_ORIENTATION = Math.PI / 2;
    //</editor-fold>

    //<editor-fold desc="Variables">
    // constant used for none, none has no state!
    final Activity NONE_ACTIVITY = new NoneActivity(this);

    Vector position;

    String name;

    int weight, strength, agility, toughness;
    double orientation;
    int hitPoints, stamina;
    int xp, xpDiff;

    Activity currentActivity = NONE_ACTIVITY;
    Activity lastActivity = NONE_ACTIVITY;

    Activity pendingActivity = NONE_ACTIVITY;

    double restMinuteTimer;

    boolean defaultEnabled;

    Faction faction;
    World world;

    PathFinder<Vector> pathFinder;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Creates a new unit with the given position.
     *
     * @param   world
     *          The world to which the unit belongs.
     * @param   faction
     *          The faction to which the unit belongs.
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
     * @post    If the given strength is less then 25 then the initial strength is set to 25.
     *          If the given strength is more then 100 then the initial strength is set to 100.
     *          Otherwise the initial strength is set to the given strength.
     *          | if strength > 100
     *          | then new.getStrength() == 100
     *          | else if strength < 25
     *          | then new.getStrength() == 25
     *          | else new.getStrength() == strength
     * @post    If the given agility is less then 25 then the initial agility is set to 25.
     *          If the given agility is more then 100 then the initial agility is set to 100.
     *          Otherwise the initial agility is set to the given agility.
     *          | if agility > 100
     *          | then new.getAgility() == 100
     *          | else if agility < 25
     *          | then new.getAgility() == 25
     *          | else new.getAgility() == agility
     * @post    If the given weight is less then 25 then the initial weight is set to 25.
     *          If the given weight is more then 100 then the initial weight is set to 100.
     *          Otherwise the initial weight is set to the given weight.
     *          | if weight > 100
     *          | then new.getWeight() == 100
     *          | else if weight < 25
     *          | then new.getWeight() == 25
     *          | else new.getWeight() == weight
     * @post    If the given toughness is less then 25 then the initial toughness is set to 25.
     *          If the given toughness is more then 100 then the initial toughness is set to 100.
     *          Otherwise the initial toughness is set to the given toughness.
     *          | if toughness > 100
     *          | then new.getToughness() == 100
     *          | else if toughness < 25
     *          | then new.getToughness() == 25
     *          | else new.getToughness() == toughness
     * @post    Sets the hit points to their maximum value.
     *          | new.getHitPoints() == new.getMaxPoints()
     *          | && new.getStamina() == new.getMaxPoints()
     *
     * @effect  Set the name to the given name.
     *          | setName(name)
     * @effect  Set the position to the middle of the given block.
     *          | setPosition(x + Lc/2, y + Lc/2, z + Lc/2)
     * @effect  Sets the orientation to 90 degrees.
     *          | setOrientation(Math.PI/2)
     */
    @Raw
    public Unit(World world, String name, int x, int y, int z, int weight, int strength, int agility, int toughness)
            throws IllegalArgumentException {
        this.world = world;
        setName(name);
        setPosition(x + World.Lc/2, y + World.Lc/2, z + World.Lc/2);

        if (toughness < 25)
            toughness = 25;
        else if (toughness > 100)
            toughness = 100;

        if (agility < 25)
            agility = 25;
        else if (agility > 100)
            agility = 100;

        if (weight < 25)
            weight = 25;
        else if (weight > 100)
            weight = 100;

        if (strength < 25)
            strength = 25;
        else if (strength > 100)
            strength = 100;

        setToughness(toughness);
        setStrength(strength);
        setAgility(agility);
        setWeight(weight);

        // TODO: getter & setter?
        this.xpDiff = 0;
        this.xp = 0;

        int maxPoints = getMaxPoints();
        setHitPoints(maxPoints);
        setStamina(maxPoints);

        setOrientation(INIT_ORIENTATION);

        this.restMinuteTimer = REST_MINUTE;

        pathFinder = new PathFinder<>(new PathFinder.PathGlue<Vector>() {
            @Override
            public Stream<Vector> getNeighbours(Vector pos) {
                return world.getNeighbours(pos).filter(n -> isValidPosition(n.getX(), n.getY(), n.getZ()) && isStablePosition(n));
            }

            @Override
            public double getCost(Vector a, Vector b) {
                return a.substract(b).norm();
            }

            @Override
            public int getHeuristic(Vector a, Vector b) {
                return (int) Math.floor(Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ()));
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="advanceTime">
    /**
     * Updates the units state.
     *
     * @param   dt
     *          The time step taken between frames.
     *
     * @post    If the unit is moving then:
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
     * @post    If the unit is working then the workTimer is decreased. If the work timer has run the unit stops working.
     * @post    If the unit is attacking another unit the attackTimer is decreased,
     *              if it runs out the other unit has to defend and the unit stops attacking.
     * @post    If the unit is resting, the restTimer is decreased. If the timer runs out the timer is first reset.
     *              If the unit can heal HP, we heal HP otherwise the unit heals stamina,
     *              if neither can be increased the unit stops resting.
     * @post    If the unit is currently not conducting an activity,
     *              the unit continues an interrupted activity if it exists.
     *              If it doesn't exist and the default behaviour is enabled, we choose an activity at random.
     * @post    We also check if three minutes have past, if so we start resting and reset this timer.
     *
     * @effect  Stops sprinting if the unit's stamina is depleted or if the unit arrived at the target.
     *          Also starts sprinting randomly when default behavior is enabled. setSprint()
     * @effect  If the unit passes the centre of the target neighbouring cube this dt, the position of the unit
     *          is set to the centre of this neighbouring cube. setPosition()
     *
     * @throws  IllegalArgumentException
     *          The given time step was to big or negative.
     */
    public void advanceTime(double dt) throws  IllegalArgumentException {
        if (dt < 0 || dt >= 0.2)
            throw new IllegalArgumentException("Invalid dt");

        getCurrentActivity().advanceTime(dt);

        restMinuteTimer -= dt;
        if (restMinuteTimer <= 0 && currentActivity.canSwitch(RestActivity.class)) {
            restMinuteTimer += REST_MINUTE;
            rest();
        }

        if (!isStablePosition(getPositionVector())) {
            this.currentActivity = new FallActivity(this);
            this.lastActivity = NONE_ACTIVITY; //TODO: save Last activity?
            assert isMoving();
        }


    }
    //</editor-fold>

    //<editor-fold desc="Activity">

    /**
     * Returns the current activity.
     */
    @Basic
    Activity getCurrentActivity() {
        return this.currentActivity;
    }

    /**
     * Sets the current activity.
     *
     * @param   newActivity
     *          The new activity.
     *
     * @pre     The unit must be able to switch to the new activity.
     *          | canHaveAsActivity(newActivity)
     *
     * @post    If we are moving and the newActivity isn't moving or defending we set the pendingActivity.
     *          Otherwise we set the currentActivity
     *          | if this.isMoving() && newActivity != Unit.MOVE && newActivity != Unit.DEFEND
     *          | then new.pendingActivity == newActivity
     *          | else new.getCurrentActivity() == newActivity
     *
     * @post    lastActivity is set to the current activity.
     *          | if this.getCurrentActivity() != newActivity
     *          | then new.lastActivity == this.getCurrentActivity()
     *
     * @effect  If the unit is resting we reset the rest timers.
     *          | if this.isResting()
     *          | then this.resetRest()
     */
    void setCurrentActivity(Activity newActivity) {
        assert currentActivity.canSwitch(newActivity.getClass());
        // don't do the same activity twice
        if (!newActivity.equalsClass(currentActivity))
            this.lastActivity = getCurrentActivity();

        if (isMoving() && !newActivity.getClass().equals(MoveActivity.class))
            this.pendingActivity = newActivity;
        else
            this.currentActivity = newActivity;
    }

    /**
     * Finishes the current activity.
     *
     * @post    The new activity will be the last activity (for when interrupted).
     *          | new.getCurrentActivity() == this.lastActivity
     * @post    The lastActivity is set to None.
     *          | new.lastActivity == Unit.NONE
     */
    void finishCurrentActivity() {
        //TODO: provide reset method in activity to reset timers & stuff
        this.currentActivity = this.lastActivity;
        this.lastActivity = NONE_ACTIVITY;
    }
    //</editor-fold>

    //<editor-fold desc="Position">
    boolean isStablePosition(Vector position) {
        return isStablePosition(World.getCubePosition(position.toDoubleArray()));
    }

    boolean isStablePosition(int[] cube) {
        // next to edges or a neighbour is solid
        return  cube[0] == 0 || cube[0] == world.X_MAX - 1 || cube[1] == 0 ||
                cube[1] == world.Y_MAX - 1 || cube[2] == 0 || cube[2] == world.Z_MAX - 1 ||
                world.getNeighbours(new Vector(cube)).anyMatch(p ->
                    World.isSolid(world.getCubeType((int) p.getX(), (int) p.getY(), (int) p.getZ())));

    }

    /**
     * Checks whether the given position is valid.
     *
     * @param   x
     *          The x value of the unit's position.
     * @param   y
     *          The y value of the unit's position.
     * @param   z
     *          The z value of the unit's position.
     *
     * @return  True if the given position is within the boundaries of the world.
     *          | result == ((x >= 0) && (x < X_MAX) && (y >= 0) && (y < Y_MAX) && (z >= 0) && (z < Z_MAX))
     */
    public boolean isValidPosition(double x,double y,double z) {
        int[] cubePos = World.getCubePosition(new double[] {x, y ,z});
        return x >= 0 && x < world.X_MAX && y >= 0 && y < world.Y_MAX && z >= 0 && z < world.Z_MAX &&
                !World.isSolid(world.getCubeType(cubePos[0], cubePos[1], cubePos[2]));
    }


    /**
     * Checks whether the given position is valid and effective.
     *
     * @param   position
     *          The position to be tested.
     *
     * @return  True if the position is effective, has 3 components and is within bounds.
     *          | result == isEffectivePosition(position) && isValidPosition(position[0], position[1], position[2])
     */
    public boolean isValidPosition(double[] position) {
        return isEffectivePosition(position) && isValidPosition(position[0], position[1], position[2]);
    }

    /**
     * Checks whether the given position is effective.
     *
     * @param   position
     *          The position to be tested.
     *
     * @return  True if the position is effective and the length is 3.
     *          | result == position != null && position.length == 3
     */
    public static boolean isEffectivePosition(double[] position) {
        return position != null && position.length == 3;
    }

    /**
     * Sets the position of the unit.
     *
     * @param   x
     *          The x value of the new position.
     * @param   y
     *          The y value of the new position.
     * @param   z
     *          The z value of the new position.
     *
     * @post    The new position of this unit is equal to the given position.
     *          | new.getPosition()[0] == x &&
     *          | new.getPosition()[1] == y &&
     *          | new.getPosition()[2] == z
     *
     * @throws  IllegalArgumentException
     *          The given position is not valid.
     *          | !isValidPosition(x, y, z)
     */
    @Raw
    public void setPosition(double x, double y, double z) throws IllegalArgumentException {
        if (!isValidPosition(x, y, z))
            throw new IllegalArgumentException("The given position is out of bounds");
        this.position = new Vector(x, y, z);
    }


    /**
     * Sets the position of the unit.
     *
     * @param   position
     *          The new position as an array.
     *
     * @effect  The new position of this unit is equal to the given position.
     *          | this.setPosition(position[0], position[1], position[2])
     *
     * @throws  IllegalArgumentException
     *          The given position is not effective.
     *          | !isEffectivePosition(position)
     */
    public void setPosition(double[] position) throws IllegalArgumentException {
        if (!isEffectivePosition(position))
            throw new IllegalArgumentException("The given position is not effective");
        this.setPosition(position[0], position[1], position[2]);
    }

    /**
     * Sets the position of the unit.
     *
     * @param   position
     *          The new position for this unit.
     *
     * @post    The new position is equal to the given position.
     *          | new.getPosition() == position.toDoubleArray()
     *          | new.getPositionVector() == position
     *
     * @throws  IllegalArgumentException
     *          When the given position is not valid.
     *          | !isValidPosition(position.toDoubleArray())
     */
    public void setPosition(Vector position) throws IllegalArgumentException {
        if (!isValidPosition(position.toDoubleArray()))
            throw new IllegalArgumentException("The given position is not valid");
        this.position = position;
    }

    /**
     * Gets the position of the unit.
     */
    @Basic
    public double[] getPosition() {
        return position.toDoubleArray();
    }

    /**
     * Gets the current position as a vector.
     */
    @Basic
    Vector getPositionVector() {
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
    public static boolean isValidName(String name) {
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
     * Returns the weight of the unit.
     */
    @Basic
    public int getWeight() {
        return this.weight;
    }

    /**
     * Sets the units weight to the new weight.
     *
     * @param   weight
     *          The new weight.
     *
     * @post    If the weight is less then (strength+agility)/2, it's set to this value.
     *          | if weight < (this.strength + this.agility)/2
     *          | then new.getWeight() == (this.strength + this.agility)/2
     *          Otherwise if the weight is more then MAX_ATTRIBUTE, it's set to MAX_ATTRIBUTE.
     *          | if weight > MAX_ATTRIBUTE
     *          | then new.getWeight() == MAX_ATTRIBUTE
     *          If the weight is still less than MIN_ATTRIBUTE, it's set to MIN_ATTRIBUTE.
     *          | if weight < MIN_ATTRIBUTE
     *          | then new.getWeight == min(1, (this.strength + this.agility)/2)
     *          Otherwise the weight is set to the given weight.
     *          | else new.getWeight() == weight
     */
    @Model
    public void setWeight(int weight) {
        if (weight < MIN_ATTRIBUTE)
            weight = MIN_ATTRIBUTE;
        else if (weight > MAX_ATTRIBUTE)
            weight = MAX_ATTRIBUTE;
        int min = (strength + agility) / 2;
        if (weight < min)
            weight = min;
        this.weight = weight;
    }

    /**
     * Returns whether the strength is valid.
     * (only used in class invariant)
     *
     * @param   strength
     *          The strength to be verified.
     *
     * @return  Returns true if the strength is larger or equal to MIN_ATTRIBUTE
     *          and smaller or equal to MAX_ATTRIBUTE.
     *          | result == strength <= MAX_ATTRIBUTE && strength >= MIN_ATTRIBUTE
     */
    @SuppressWarnings("unused")
    public static boolean isValidStrength(int strength) {
        return strength <= MAX_ATTRIBUTE && strength >= MIN_ATTRIBUTE;
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
     * @post    If the given strength is less than MIN_ATTRIBUTE, the new strength is MIN_ATTRIBUTE.
     *          | if strength < MIN_ATTRIBUTE
     *          | then new.getStrength() == MIN_ATTRIBUTE
     *          If the given strength is more then MAX_ATTRIBUTE, the new strength is MAX_ATTRIBUTE.
     *          | else if strength > MAX_ATTRIBUTE
     *          | then new.getStrength() == MAX_ATTRIBUTE
     *          Otherwise the new strength is the given strength
     *          | else new.getStrength() == strength
     * @post    The weight is adapted to match the new strength.
     *          | if this.getWeight < (strength + this.agility)/2
     *          | then new.getWeight() == (strength + this.agility)/2
     *          | else new.getWeight() == this.getWeight()
     */
    @Raw
    public void setStrength(int strength) {
        if (strength < MIN_ATTRIBUTE)
            strength = MIN_ATTRIBUTE;
        else if (strength > MAX_ATTRIBUTE)
            strength = MAX_ATTRIBUTE;
        this.strength = strength;
        setWeight(getWeight());
    }


    /**
     * Returns whether the agility is valid.
     * (only used in class invariant)
     *
     * @param   agility
     *          The agility to be verified.
     *
     * @return  Returns true if the agility is larger or equal to MIN_ATTRIBUTE,
     *          and smaller or equal to MAX_ATTRIBUTE.
     *          | result == agility <= MAX_ATTRIBUTE && agility >= MIN_ATTRIBUTE
     */
    @SuppressWarnings("unused")
    public static boolean isValidAgility(int agility) {
        return agility <= MAX_ATTRIBUTE && agility >= MIN_ATTRIBUTE;
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
     *          | if agility < MIN_ATTRIBUTE
     *          | then new.getAgility() == MIN_ATTRIBUTE
     *          If the given agility is more then MAX_ATTRIBUTE, the new agility is MAX_ATTRIBUTE.
     *          | else if agility > MAX_ATTRIBUTE
     *          | then new.getAgility() == MAX_ATTRIBUTE
     *          Otherwise the new agility is the given agility.
     *          | else new.getAgility() == agility
     * @post    The weight is adapted to match the new agility.
     *          | if this.getWeight < (this.strength + agility)/2
     *          | then new.getWeight() == (this.strength + agility)/2
     *          | else new.getWeight() == this.getWeight()
     */
    @Raw
    public void setAgility(int agility) {
        if (agility < MIN_ATTRIBUTE)
            agility = MIN_ATTRIBUTE;
        else if (agility > MAX_ATTRIBUTE)
            agility = MAX_ATTRIBUTE;
        this.agility = agility;
        setWeight(getWeight());
    }


    /**
     * Returns whether the toughness is valid.
     * (only used in class invariant)
     *
     * @param   toughness
     *          The toughness to be verified.
     *
     * @return  Returns true if the toughness is larger or equal to MIN_ATTRIBUTE,
     *          and smaller or equal to MAX_ATTRIBUTE.
     *          | result == toughness <= MAX_ATTRIBUTE && toughness >= MIN_ATTRIBUTE
     */
    @SuppressWarnings("unused")
    public static boolean isValidToughness(int toughness) {
        return toughness <= MAX_ATTRIBUTE && toughness>= MIN_ATTRIBUTE;
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
     *          | if toughness < MIN_ATTRIBUTE
     *          | then new.getToughness() == MIN_ATTRIBUTE
     *          If the given toughness is more then MAX_ATTRIBUTE, the new toughness is MAX_ATTRIBUTE.
     *          | else if toughness > MAX_ATTRIBUTE
     *          | then new.getToughness() == MAX_ATTRIBUTE
     *          Otherwise the new toughness is the given toughness.
     *          | else new.getToughness() == toughness
     */
    @Raw
    public void setToughness(int toughness) {
        if (toughness > MAX_ATTRIBUTE)
            toughness = MAX_ATTRIBUTE;
        else if (toughness < MIN_ATTRIBUTE)
            toughness = MIN_ATTRIBUTE;
        this.toughness = toughness;
    }

    /**
     * Returns whether the hitPoints are valid.
     * (only used in class invariant)
     *
     * @param   hitPoints
     *          The hit points to be verified.
     *
     * @return  Returns true if the hitPoints are larger than or equal to 0
     *          and smaller than or equal to the maximum amount of hitPoints.
     *          | result == (hitPoints <= getMaxPoints()) && (hitPoints >= 0)
     */
    @SuppressWarnings("unused")
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
        assert hitPoints <= getMaxPoints() && hitPoints >= 0;
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
     *          |   new.getHitPoints() == 0
     */
    @Model
    void deduceHitPoints(int hitPoints)  {
        int newHitPoints = this.getHitPoints() - hitPoints;
        if (newHitPoints <= 0) {
            currentActivity = NONE_ACTIVITY;
            lastActivity = NONE_ACTIVITY;
            pendingActivity = NONE_ACTIVITY;
            this.hitPoints = 0;
            world.removeUnit(this);
        }
        this.setHitPoints(newHitPoints);
    }

    public boolean isAlive() {
        return (this.getHitPoints() > 0);
    }

    /**
     * Returns whether the stamina is valid.
     * (only used in class invariant)
     *
     * @param   stamina
     *          The stamina to be verified.
     *
     * @return  Returns true if the amount of stamina is larger than or equal to 0
     *          and smaller than or equal to the maximum amount of hitPoints.
     *          | result == (stamina <= getMaxPoints()) && (stamina >= 0)
     */
    @SuppressWarnings("unused")
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
        assert stamina <= getMaxPoints() && stamina >= 0;
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
     *          and smaller than 2*PI
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
     */
    @Basic
    public boolean isMoving() {
        return currentActivity.equalsClass(MoveActivity.class);
    }


    /**
     * Starts the unit moving towards one of the adjacent cubes.
     *
     * @param   dx
     *          the x direction
     * @param   dy
     *          the y direction
     * @param   dz
     *          the z direction
     *
     * @post    The unit will be moving
     *          | new.isMoving() == True
     * @post    The unit will move to the target when calling advanceTime()
     *          | new.getPosition[0] == old.getPosition[0] + dx &&
     *          | new.getPosition[1] == old.getPosition[1] + dy &&
     *          | new.getPosition[2] == old.getPosition[2] + dz
     *
     * @effect  Set the unit's orientation
     *          | setOrientation(Math.atan2(getSpeed().getY(), getSpeed().getX()));
     *
     * @throws  IllegalArgumentException
     *          If the target cube is not within the world bounds
     *          | !isValidPosition(this.getPosition()[0] + dx,this.getPosition()[1] + dy,this.getPosition()[2] + dz)
     * @throws  IllegalArgumentException
     *          If the dx, dy or dz values aren't -1, 0 or +1
     *          | Math.abs(dx) > 1 || Math.abs(dy) > 1 || Math.abs(dz) > 1
     * @throws  IllegalStateException
     *          If the unit can't move right now
     *          | !canHaveAsActivity(Unit.MOVE)
     */
    public void moveToAdjacent(int dx, int dy, int dz) throws IllegalArgumentException, IllegalStateException {
        if (!currentActivity.canSwitch(MoveActivity.class)) {
            throw new IllegalStateException("Can't move right now");
        }

        if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || Math.abs(dz) > 1) {
            throw new IllegalArgumentException("Illegal dx, dy and/or dz");
        }

        if (isMoving())
            ((MoveActivity)getCurrentActivity()).updateAdjecent(dx, dy, dz);
        else
            setCurrentActivity(new MoveActivity(this, new int[] {dx, dy, dz}));
    }


    /**
     * Starts the units movement to the given target cube.
     *
     * @param   target
     *          The coordinates of the target cubes.
     *
     * @post    The unit starts moving.
     *          | new.isMoving() == true
     *
     * @post    The unit will move to the target when calling advanceTime().
     *          | new.getPosition == target.add(Lc/2)
     *
     * @throws  IllegalArgumentException
     *          If the given target is not valid.
     *          | !isValidPosition(target[0], target[1], target[2])
     * @throws  IllegalStateException
     *          If the unit can't move.
     *          | !this.canHaveAsActivity(Unit.MOVE)f
     *
     */
    public void moveTo(int[] target) throws IllegalArgumentException, IllegalStateException {
        if (!currentActivity.canSwitch(MoveActivity.class)) {
            throw new IllegalStateException("can't path right now");
        }

        Vector newTarget = new Vector(target[0], target[1], target[2]);
        newTarget = newTarget.add(World.Lc/2);

        if (!isValidPosition(newTarget.toDoubleArray())) {
            throw new IllegalArgumentException("invalid target");
        }

        if (isMoving())
            ((MoveActivity)getCurrentActivity()).updateTarget(newTarget);
        else
            setCurrentActivity(new MoveActivity(this, newTarget));
    }

    //</editor-fold>

    //<editor-fold desc="SpeedNSprint">
    /**
     * Gets the units movement speed.
     *
     * @return  Returns the base speed if the unit is moving and not sprinting.
     *          Returns 0 if the unit is not moving.
     *          Returns twice the base speed if the unit is sprinting.
     *          | if (getSpeed() != null && !isSprinting())
     *          |   then result == getSpeed
     *          | if (getSpeed() == null)
     *          |   then result == 0
     *          | if (getSpeed() != null && isSprinting())
     *          |   then result == 2 * getSpeed
     */
    public double getSpeedScalar() {
        if (!isMoving())
            return 0;
        MoveActivity current = (MoveActivity)getCurrentActivity();
        double speedScalar = current.speed == null ? 0 : current.speed.norm();
        return isSprinting() ? 2*speedScalar : speedScalar;
    }

    /**
     * Enables or disables sprint mode for this unit.
     *
     * @param   sprint
     *          Declares if the unit wants to sprint or stop sprinting.
     *
     * @post    Sets whether the unit is sprinting or not.
     *          | new.isSprinting == sprint
     *
     * @throws  IllegalStateException
     *          Throws if the unit wants to sprint and the unit has no stamina
     *          or the unit is not moving.
     *          | sprint && (getStamina() == 0 || !isMoving())
     */
    public void setSprint(boolean sprint) throws IllegalStateException {
        if (sprint && (getStamina() == 0 || !isMoving())) {
            throw new IllegalStateException("Can't sprint right now");
        }
        if (!isMoving()) return; // throw?

        MoveActivity current = (MoveActivity)getCurrentActivity();
        if (!current.sprinting && sprint)
            current.sprintStaminaTimer = MoveActivity.SPRINT_DELAY;
        current.sprinting = sprint;
    }

    /**
     * Returns True if the unit is sprinting
     */
    @Basic
    public boolean isSprinting() {
        return isMoving() && ((MoveActivity)getCurrentActivity()).sprinting;
    }
    //</editor-fold>

    //<editor-fold desc="Working">
    /**
     * Returns True if the unit is working
     */
    @Basic
    public boolean isWorking() {
        return getCurrentActivity().equalsClass(WorkActivity.class);
    }

    /**
     * The unit starts working.
     *
     * @post    Makes the unit start working
     *          | new.isWorking == True
     *
     * @throws  IllegalArgumentException
     *          Throws if the unit can't work.
     *          | (!canHaveAsActivity(Unit.WORK)
     */
    public void work() throws IllegalArgumentException {
        if (!getCurrentActivity().canSwitch(WorkActivity.class)) {
            throw new IllegalArgumentException("can't work right now");
        }
        if (!isWorking())
            setCurrentActivity(new WorkActivity(this));
    }
    //</editor-fold>

    //<editor-fold desc="Fighting">
    /**
     * Returns True if the unit is attacking.
     */
    @Basic
    public boolean isAttacking() {
        return currentActivity.equalsClass(AttackActivity.class);
    }

    /**
     * Attacks an other unit.
     *
     * @param   other
     *          The unit that is attacked by this unit.
     *
     * @post    Makes the other unit defend.
     *          | (new other).isDefending == True
     * @post    Makes the units look at each other.
     *          | new.getOrientation() == atan2((other.getPosition[1] - this.getPosition[1]),
     *          |       (other.getPosition[0] - this.getPosition[0]))
     *          | (new other).getOrientation() == atan2((this.getPosition[1] - other.getPosition[1]),
     *          |       (this.getPosition[0] - other.getPosition[0]))
     * @post    Makes the unit attack.
     *          | new.isAttacking == True
     *
     * @throws  IllegalArgumentException
     *          Throws if the other unit is null or if the unit tries to attack itself.
     *          | (other == null || other == this)
     * @throws  IllegalArgumentException
     *          Throws if the unit can't attack.
     *          | !canHaveAsActivity(Unit.ATTACK)
     * @throws  IllegalArgumentException
     *          Throws if the other unit is not in attack range.
     *          | ( (abs(this.getPosition[0] - other.getPosition[0])) > 1 ||
     *          | ( (abs(this.getPosition[1] - other.getPosition[1])) > 1 ||
     *          | ( (abs(this.getPosition[2] - other.getPosition[2])) > 1 )
     */
    public void attack(Unit other) throws IllegalArgumentException {
        if (other == null || other == this || other.getCurrentActivity().equalsClass(FallActivity.class))
            throw new IllegalArgumentException("The other unit is invalid");
        if (!currentActivity.canSwitch(AttackActivity.class))
            throw new IllegalArgumentException("Can't attack right now");
        if (this.getFaction() == other.getFaction())
            throw new IllegalArgumentException("Can't attack units of the same faction");

        // TODO: move to attachActivity constructor:
        Vector otherPos = other.getPositionVector();
        int[] otherCube = World.getCubePosition(otherPos.toDoubleArray());
        int[] posCube = World.getCubePosition(this.getPosition());

        for (int i = 0; i < 3; i++) {
            int diff = otherCube[i] - posCube[i];
            if (diff > 1 || diff < -1) {
                throw new IllegalArgumentException("the other unit is to far away");
            }
        }

        Vector diff = otherPos.substract(this.position);
        this.setOrientation(Math.atan2(diff.getY(), diff.getX()));
        other.setOrientation(Math.atan2(-diff.getY(), -diff.getX()));

        other.defend(this);

        setCurrentActivity(new AttackActivity(this));
    }

    /**
     * Defends against another unit's attack.
     *
     * @param   attacker
     *          The unit that attacks this unit.
     *
     * @post    If the unit dodges the attack he jumps in the x and y direction
     *          both with a distance in range of -1 to 1
     *          and the new position can not be equal to the original.
     *          The unit does not take any damage.
     *          | if (random < (0.20 * this.getAgility() / attacker.getAgility()) )
     *          |   then new.getPosition()[0] == old.getPosition[0] + 2 * Math.random -1 &&
     *          |       new.getPosition()[1] == old.getPosition[1] + 2 * Math.random -1) &&
     *          |       (new.getHitPoints == old.getHitPoints)
     *          Else if the attack is blocked, the unit will not take any damage.
     *          | else if ( random < (0.25 * (this.getStrength + this.getAgility)/(other.getStrength + other.getAgility)))
     *          |   then ( new.getHitPoints == old.getHitPoints)
     *          Else if the attack hit the unit, it will take damage equal to the attacker's strength/10.
     *          | Else
     *          |   ( deduceHitPoints (attack.getStrength() / 10) )
     *
     * @effect  Finishes the current activity.
     *          | finishCurrentActivity()
     * @effect  Sets the position to the random position after dodging.
     *          | setPosition()
     * @effect  Deduces the given amount of hit points from the unit's hp.
     *          | deduceHitPoints()
     *
     */
    void defend(Unit attacker) {
        double probabilityDodge = 0.20 * (this.getAgility() / attacker.getAgility());
        if (Math.random() < probabilityDodge) {
            // TODO: fix dodging
            double newX = 2 * Math.random() - 1;
            double newY = 2 * Math.random() - 1;
            if (newX == 0 && newY == 0) {
                double range = 1 - POS_EPS;
                double newRandom = Math.random() * range + POS_EPS;
                if (Math.random() >= 0.5)
                    newX = 2 * newRandom - 1;
                else
                    newY = 2 * newRandom - 1;
            }
            newX += getPositionVector().getX();
            newY += getPositionVector().getY();
            if (newX < 0 || newX >= world.X_MAX){
                newX = -newX;
            }
            if (newY < 0 ||newY >= world.Y_MAX) {
                newY = -newY;
            }
            setPosition(newX, newY, getPositionVector().getZ());
            Vector diff = attacker.getPositionVector().substract(this.position);
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

    }
    //</editor-fold>

    //<editor-fold desc="Resting">
    /**
     * Returns True if the unit is resting.
     */
    @Basic
    public boolean isResting() {
        return currentActivity.equalsClass(RestActivity.class);
    }

    /**
     * Starts resting.
     *
     * @post    If the unit is not resting then reset the rest state.
     *          | if !old.isResting
     *          | then new.initialRest == true && new.restDiff == 0
     * @post    The unit is resting.
     *          | new.isResting() == true
     *
     * @throws  IllegalStateException
     *          Throws if the unit is moving.
     *          | this.isMoving()
     */
    public void rest() throws IllegalStateException {
        if (!currentActivity.canSwitch(RestActivity.class))
            throw new IllegalStateException("Can't rest right now");

        if (!isResting())
            setCurrentActivity(new RestActivity(this));
    }
    //</editor-fold>

    //<editor-fold desc="Default behaviour">

    /**
     * Starts the default behavior.
     *
     * @post    The default behavior is enabled.
     *          | new.isDefaultEnabled == True
     */
    public void startDefaultBehaviour() {
        this.defaultEnabled = true;
    }

    /**
     * Stops the default behavior.
     *
     * @post    The default behavior has stopped.
     *          | new.isDefaultEnabled == False
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
     * Adds the given xp to the Unit
     *
     * @param   xp
     *          The xp to be added
     *
     * @post    The new xp will match
     *          | new.getXp() == this.getXp() + xp
     *
     * @effect  The unit will level up
     *          | ...
     */
    void addXp(int xp) {
        this.xp += xp;
        this.xpDiff += xp;
        levelUp();
    }

    @Basic
    public int getXp() {
        return this.xp;
    }

    private void levelUp() {
        while (this.xpDiff >= 10) {
            int rand = (int) Math.floor(Math.random()*3);
            this.xpDiff -= 10;
            if (rand == 0)
                this.setStrength(this.getStrength() + 1);
            else if (rand == 1)
                this.setAgility(this.getAgility() + 1);
            else
                this.setToughness(this.getToughness() + 1);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Faction">
    @Basic
    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    //</editor-fold>
}
