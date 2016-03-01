package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Value;
import ogp.framework.util.ModelException;

import java.util.ArrayList;

/**
 * ....
 * TODO: class invariants
 * @invar   The position of the unit must be valid
 *          | isValidPosition(this.getPosition())
 */
public class Unit {
    @Value
    private enum Activity {
        WORK,
        MOVE,
        REST,
        ATTACK,
        NONE
    }

    //<editor-fold desc="Constants">
    public static final int X_MAX = 50;
    public static final int Y_MAX = 50;
    public static final int Z_MAX = 50;

    public static final double Lc = 1.0;
    public static final double POS_EPS = 0.05;

    public static final double REST_DELAY = 0.2;
    public static final double SPRINT_DELAY = 0.1;
    //</editor-fold>

    //<editor-fold desc="Variables">
    private Vector position;
    private String name;
    private int weight, strength, agility, toughness;
    private double orientation;
    private double hitPoints, stamina;

    private Activity currentActivity = Activity.NONE;

    private Vector target;
    private Vector targetNeighbour;

    private Vector speed;

    private boolean sprinting;
    private double sprintStaminaTimer;

    private double workTimer;

    private double attackTimer;
    private Unit defender;

    private double restTimer;
    private boolean initialRest;
    private double restHPDiff;
    private double restMinuteTimer;

    private boolean defaultEnabled;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Creates a new unit with the given position
     * @param   x
     *          The initial x value of the position
     * @param   y
     *          The initial y value of the position
     * @param   z
     *          The initial z value of the position
     * @post    If the given strength is less then 25 then the initial strength is set to 25.
     *          If the given strength is more then 100 then the initial strength is set to 100.
     *          | if strength > 100
     *          | then new.getStrength() == 100
     *          | else if strength < 25
     *          | then new.getStrength() == 25
     * @post    If the given agility is less then 25 then the initial agility is set to 25.
     *          If the given agility is more then 100 then the initial agility is set to 100.
     *          | if agility > 100
     *          | then new.getAgility() == 100
     *          | else if agility < 25
     *          | then new.getAgility() == 25
     * @post    If the given weight is less then 25 then the initial weight is set to 25.
     *          If the given weight is more then 100 then the initial weight is set to 100.
     *          | if weight > 100
     *          | then new.getWeight() == 100
     *          | else if weight < 25
     *          | then new.getWeight() == 25
     * @post    If the given toughness is less then 25 then the initial toughness is set to 25.
     *          If the given toughness is more then 100 then the initial toughness is set to 100.
     *          | if toughness > 100
     *          | then new.getToughness() == 100
     *          | else if toughness < 25
     *          | then new.getToughness() == 25
     * @post    Sets the hit points to their maximum value
     *          | new.getHitPoints() == new.getMaxPoints()
     *          | && new.getStamina() == new.getMaxPoints()
     * @effect  Sets the position to the middle of the block
     *          | setPosition(x + Lc/2, y + Lc/2, z + Lc/2)
     * @effect  Sets the name
     *          | setName(name)
     * @effect  Sets the attributes
     *          | setToughness(toughness) && setStrength(strength)
     *          | && setAgility(agility) && setWeight(weight)
     * @effect  Sets the orientation to 90 degrees
     *          | setOrientation(Math.PI/2)
     *
     */
    public Unit(String name, int x, int y, int z, int weight, int strength, int agility, int toughness)
            throws IllegalArgumentException {
        setName(name);
        setPosition(x + Lc/2, y + Lc/2, z + Lc/2);
        
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

        int maxPoints = getMaxPoints();
        setHitPoints(maxPoints);
        setStamina(maxPoints);

        setOrientation(Math.PI/2);

        this.restMinuteTimer = 3*60;

        //TODO: enable default behavior at start?
    }
    //</editor-fold>

    //<editor-fold desc="advanceTime">
    public void advanceTime(double dt) throws  ModelException {
        if (dt < 0 || dt >= 0.2)
            throw new ModelException("Invalid dt");
        switch(currentActivity) {
            case MOVE:
                double mod = 1;
                if (isSprinting()) {
                    sprintStaminaTimer -= dt;
                    mod = 2;
                    if(sprintStaminaTimer <= 0) {
                        sprintStaminaTimer += SPRINT_DELAY;
                        int newStamina = getStamina()  - 1;
                        if (newStamina >= 0)
                            setStamina(newStamina);
                        if (getStamina() == 0) {
                            mod = 1;
                            setSprint(false);
                        }
                    }
                }

                // setPosition(pos[0] + mod*speed[0] * dt, pos[1] + mod*speed[1] * dt, pos[2] + mod* speed[2] * dt);
                //this.position = this.position.add(this.speed.multiply(mod*dt));
                setPosition(this.position.add(this.speed.multiply(mod*dt)));
                if (isAtNeighbour()) {
                    this.position = this.targetNeighbour;
                    if (this.target == null || isAtTarget()) {
                        setSprint(false);
                        this.currentActivity = Activity.NONE;
                        this.speed = null;
                        this.target = null;
                        this.targetNeighbour = null;
                    } else {
                        int[] posC = getCubePosition(getPosition());
                        int[] targetC = getCubePosition(this.target.toDoubleArray());
                        int[] dp = new int[3];
                        for (int i = 0; i < 3; i++) {
                            if (posC[i] == targetC[i])
                                dp[i] = 0;
                            else if (posC[i] < targetC[i])
                                dp[i] = 1;
                            else
                                dp[i] = -1;
                        }
                        this.currentActivity = Activity.NONE;
                        moveToAdjacent(dp[0], dp[1], dp[2]);

                    }
                }
                break;
            case WORK:
                workTimer -= dt;
                if (workTimer <= 0) {
                    workTimer = 0;
                    currentActivity = Activity.NONE;
                }
                break;
            case ATTACK:
                this.attackTimer -= dt;
                if (this.attackTimer <= 0) {
                    this.defender.defend(this);
                    this.attackTimer = 0;
                    this.currentActivity = Activity.NONE;
                }
                break;
            case REST:
                this.restTimer -= dt;
                if (this.restTimer <= 0) {
                    this.restTimer += REST_DELAY;
                    if (getHitPoints() != getMaxPoints()) {
                        double diff = (getToughness()/200.0);
                        restHPDiff += diff;
                        setHitPoints(this.hitPoints + diff);
                        // recover at least 1 HP
                        if (restHPDiff >= 1)
                            initialRest = false;
                    } else if (getStamina() != getMaxPoints()) {
                        initialRest = false;
                        setStamina(this.stamina + (getToughness()/100.0));
                    } else {
                        this.currentActivity = Activity.NONE;
                    }
                }
                break;
            case NONE:
                if (target != null) {
                    // continue moving if moving got interrupted
                    this.currentActivity = Activity.MOVE;
                } else if (defaultEnabled) {
                    int random = (int)Math.floor(Math.random()*3);
                    switch (random) {
                        case 0: // move to random location
                            int[] randLoc = new int[3];
                            for (int i = 0; i < 3; i++) {
                                randLoc[i] = (int)Math.floor(Math.random()*50);
                            }
                            moveTo(randLoc);
                            break;
                        case 1: // work
                            work();
                            break;
                        case 2: // rest
                            rest();
                            break;
                    }
                }
                break;
        }

        restMinuteTimer -= dt;
        if (restMinuteTimer <= 0) {
            restMinuteTimer += 3 * 60;
            rest();
        }

    }

    private boolean isAtTarget() {
        return this.position.isEqualsTo(this.target, POS_EPS);
    }

    private boolean isAtNeighbour() {
        return this.position.isEqualsTo(this.targetNeighbour, POS_EPS);
    }
    //</editor-fold>

    private Activity getCurrentActivity() {
        return this.currentActivity;
    }

    private void setCurrentActivity(Activity newActivity) {
        assert canHaveAsActivity(newActivity);
        this.currentActivity = newActivity;
    }

    private boolean canHaveAsActivity(Activity newActivity) {
        switch (getCurrentActivity()) {
            case WORK:
                return true;
            case NONE:
                return true;
            case REST:
                return !initialRest; // can't interrupt the initial rest period
            case MOVE:
                // moveToAdjacent only when attacked
                //TODO: return false, except when needing to rest -> moveToAdjacent first then rest?
                // moveTo yes, but must resume when done.
                return this.target != null;
            case ATTACK:
                // can't stop an attack?
                return false;
        }
        return false;
    }

    private void setTarget(Vector target) {
        this.target = target;
    }

    private void setTargetNeighbour(Vector targetNeighbour) {
        this.targetNeighbour = targetNeighbour;
    }

    //<editor-fold desc="Position">
    /**
     * Checks if the given position is valid
     * @param   x
     *          The x value of the unit's position
     * @param   y
     *          The y value of the unit's position
     * @param   z
     *          The z value of the unit's position
     * @return  True if the given position is within the boundaries of the world
     *          | result == ((x >= 0) && (x < X_MAX) && (y >= 0) && (y < Y_MAX) && (z >= 0) && (z < Z_MAX))
     */
    public static boolean isValidPosition(double x,double y,double z) {
        return x >= 0 && x < X_MAX && y >= 0 && y < Y_MAX && z >= 0 && z < Z_MAX;
    }


    /**
     * Checks if the given position is valid
     * @param   position
     *          The position to be tested.
     * @return  True if the position is effective, has 3 components and is within bounds.
     *          | result == isEffectivePosition(position) && isValidPosition(position[0], position[1], position[2])
     */
    public static boolean isValidPosition(double[] position) {
        return isEffectivePosition(position) && isValidPosition(position[0], position[1], position[2]);
    }

    /**
     * Checks if the given position is effective
     * @param   position
     *          The position to be tested
     * @return  True if the position is effective and the length is 3
     *          | result == position != null && position.length == 3
     */
    public static boolean isEffectivePosition(double[] position) {
        return position != null && position.length == 3;
    }

    /**
     * Sets the position of the unit.
     * @param   x
     *          The x value of the new position
     * @param   y
     *          The y value of the new position
     * @param   z
     *          The z value of the new position
     * @post    The new position of this unit is equal to the given position
     *          | new.getPosition()[0] == x &&
     *          | new.getPosition()[1] == y &&
     *          | new.getPosition()[2] == z
     * @throws  IllegalArgumentException
     *          The given position is not valid
     *          | !isValidPosition(x,y,z)
     */
    public void setPosition(double x,double y,double z) throws IllegalArgumentException {
        if (!isValidPosition(x, y, z))
            throw new IllegalArgumentException("The given position is out of bounds");
        this.position = new Vector(x, y, z);
    }


    /**
     * Sets the position of the unit.
     * @param   position
     *          The new position as an array
     * @effect  The new position of this unit is equal to the given position
     *          | this.setPosition(position[0], position[1], position[2])
     * @throws  IllegalArgumentException
     *          The given position is not effective
     *          | position == null
     */
    public void setPosition(double[] position) throws IllegalArgumentException {
        if (!isEffectivePosition(position))
            throw new IllegalArgumentException("The given position is not effective");
        this.setPosition(position[0], position[1], position[2]);
    }

    /**
     * Sets the position of the unit.
     * @param   position
     *          The new position for this unit
     * @post    The new position is equal to the given position
     *          | new.getPosition() == position.toDoubleArray()
     * @throws  IllegalArgumentException
     *          When the given position is not valid
     *          | !isValidPosition(position.toDoubleArray())
     */
    public void setPosition(Vector position) throws IllegalArgumentException {
        if (!isValidPosition(position.toDoubleArray()))
            throw new IllegalArgumentException("The given position is not valid");
        this.position = position;
    }


    /**
     * Gets the position of the unit
     */
    @Basic
    public double[] getPosition() {
        return position.toDoubleArray();
    }

    /**
     * Returns the coordinates of the cube that the unit currently occupies
     * @return  Returns the rounded down position of the unit
     *          | result[0] == floor(getPosition()[0]) &&
     *          | result[1] == floor(getPosition()[1]) &&
     *          | result[2] == floor(getPosition()[2]}
     */
    public static int[] getCubePosition(double[] position) {
        return new int[] {
                (int)Math.floor(position[0]),
                (int)Math.floor(position[1]),
                (int)Math.floor(position[2])
        };
    }
    //</editor-fold>

    //<editor-fold desc="Name">
    /**
     * Checks wether the name is valid
     * @param   name
     *          The name to be checked
     * @return  True if name is at least 2 characters long,
     *          starts with an uppercase letter and contains only letters, spaces and quotes.
     *          | result == (name.length() > 2) && name.matches("[A-Z][a-zA-Z'\" ]*")
     */
    public static boolean isValidName(String name) {
        return name.length() > 2 && name.matches("[A-Z][a-zA-Z'\" ]*");
    }

    /**
     * Returns the name of the unit
     */
    @Basic
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the unit
     * @param   name
     *          The new name for the unit
     * @post    The new name is the given name
     *          | new.getName() == name
     * @throws  IllegalArgumentException
     *          The name is not valid
     *          | !isValidName(name)
     */
    public void setName(String name) throws IllegalArgumentException {
        if (!isValidName(name))
            throw new IllegalArgumentException("Invalid name");
        this.name = name;
    }
    //</editor-fold>

    //<editor-fold desc="Properties">

    /**
     * Returns whether the weight is valid
     * @return  Returns true if the weight is larger or equal to 1,
     *          larger or equal to (strength + agility)/2,
     *          smaller or equal to 200
     *          | result ==  (weight >= 1) && (weight <= 200) && (weight >= (getAgility()+getStrength())/2)
     */
    public boolean isValidWeight(int weight) {
        return (weight >= 1) && (weight <= 200) && (weight >= (getAgility()+getStrength())/2);
    }

    /**
     * Returns the weight of the unit
     */
    @Basic
    public int getWeight() {
        return this.weight;
    }

    /**
     * Sets the units weight to the new weight
     * @param   weight
     *          The new weight
     * @post    If the weight is less then (strength+agility)/2, it's set to this value
     *          | if weight < (this.strength + this.agility)/2
     *          | then new.getWeight() == (this.strength + this.agility)/2
     *          Otherwise if the weight is more then 200, it's set to 200
     *          | if weight > 200
     *          | then new.getWeight() == 200
     *          If the weight is still less then one, it's set to one
     *          | if weight < 1
     *          | then new.getWeight == min(1, (this.strength + this.agility)/2)
     *          Otherwise the weight is set to the given weight
     *          | else new.getWeight() == weight
     */
    public void setWeight(int weight) {
        if (weight < 1)
            weight = 1;
        else if (weight > 200)
            weight = 200;
        int min = (strength + agility) / 2;
        if (weight < min)
            weight = min;
        this.weight = weight;
    }

    /**
     * Returns whether the strength is valid
     * @return  Returns true if the strength is larger or equal to 1,
     *          and smaller or equal to 200.
     *          | result == strength <= 200 && strength >= 1
     */
    public boolean isValidStrenth(int strength) {
        return strength <= 200 && strength >= 1;
    }

    /**
     * Returns the strength of the unit
     */
    @Basic
    public int getStrength() {
        return this.strength;
    }

    /**
     * Sets the strength of the unity
     * @param   strength
     *          The new strength for the unit
     * @post    If the given strength is less then one, the new strength is one
     *          | if strength < 1
     *          | then new.getStrength() == 1
     *          If the given strength is more then 200, the new strength is 200
     *          | else if strength > 200
     *          | then new.getStrength() == 200
     *          Otherwise the new strength is the given strength
     *          | else new.getStrength() == strength
     * @post    The weight is adapted to match the new strength
     *          | if this.getWeight < (strength + this.agility)/2
     *          | then new.getWeight() == (strength + this.agility)/2
     *          | else new.getWeight() == this.getWeight()
     */
    public void setStrength(int strength) {
        if (strength < 1)
            strength = 1;
        else if (strength > 200)
            strength = 200;
        this.strength = strength;
        setWeight(getWeight());
    }


    /**
     * Returns whether the agility is valid
     * @return  Returns true if the agility is larger or equal to 1,
     *          and smaller or equal to 200.
     *          | result == agility <= 200 && agility >= 1
     */
    public boolean isValidAgility(int agility) {
        return agility <= 200 && agility >= 1;
    }

    /**
     * Returns the agility of the unit
     */
    @Basic
    public int getAgility() {
        return this.agility;
    }

    /**
     * Sets the agility of the unit
     * @param   agility
     *          The new agility
     * @post    If the given agility is less then one, the new agility is one
     *          | if agility < 1
     *          | then new.getAgility() == 1
     *          If the given agility is more then 200, the new agility is 200
     *          | else if agility > 200
     *          | then new.getAgility() == 200
     *          Otherwise the new agility is the given agility
     *          | else new.getAgility() == agility
     * @post    The weight is adapted to match the new agility
     *          | if this.getWeight < (this.strength + agility)/2
     *          | then new.getWeight() == (this.strength + agility)/2
     *          | else new.getWeight() == this.getWeight()
     */
    public void setAgility(int agility) {
        if (agility < 1)
            agility = 1;
        else if (agility > 200)
            agility = 200;
        this.agility = agility;
        setWeight(getWeight());
    }


    /**
     * Returns whether the toughness is valid
     * @return  Returns true if the toughness is larger or equal to 1,
     *          and smaller or equal to 200.
     *          | result == toughness <= 200 && toughness >= 1
     */
    public boolean isValidToughness(int toughness) {
        return toughness <= 200 && toughness>= 1;
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
     * @param   toughness
     *          The new toughness
     * @post    If the given toughness is less then one, the new toughness is one
     *          | if toughness < 1
     *          | then new.getToughness() == 1
     *          If the given toughness is more then 200, the new toughness is 200
     *          | else if toughness > 200
     *          | then new.getToughness() == 200
     *          Otherwise the new toughness is the given toughness
     *          | else new.getToughness() == toughness
     */
    public void setToughness(int toughness) {
        if (toughness > 200)
            toughness = 200;
        else if (toughness < 1)
            toughness = 1;
        this.toughness = toughness;
    }

    /**
     * Returns whether the hitpoints are valid
     * @return  Returns true if the hitpoints are larger than or equal to 0
     *          and smaller than or equal to the maximum amount of hitpoints
     *          | result == (hitPoints <= getMaxPoints()) && (hitPoints >= 0)
     */
    public boolean isValidHitPoints(int hitPoints) {
        return hitPoints <= getMaxPoints() && hitPoints >= 0;
    }

    /**
     * Returns the current amount of hitPoints of the unit
     */
    @Basic
    public int getHitPoints() {
        return (int)Math.floor(this.hitPoints);
    }

    /**
     * Sets the current hitPoints to the given amount of hitPoints
     * @param   hitPoints
     *          The new hitPoints of the unit
     * @pre     The hitPoints must be greater or equal than 0 and smaller or equal than max hitPoints
     *          | (hitPoints <= getMaxPoints()) && (hitPoints >= 0;)
     * @post    The new hitPoints equal the given hitPoints
     *          | new.getHitPoints() == hitPoints
     *
     */
    public void setHitPoints(double hitPoints) {
        assert hitPoints <= getMaxPoints() && hitPoints >= 0;
        this.hitPoints = hitPoints;
    }


    /**
     * Returns whether the amount of stamina is valid
     * @return  Returns true if the amount of stamina is larger than or equal to 0
     *          and smaller than or equal to the maximum amount of hitpoints
     *          | result == (stamina <= getMaxPoints()) && (stamina >= 0)
     */
    public boolean isValidStamina(int stamina) {
        return stamina <= getMaxPoints() && stamina >= 0;
    }

    /**
     * Returns the current amount of stamina of the unit
     */
    @Basic
    public int getStamina() {
        return (int)Math.floor(this.stamina);
    }

    /**
     * Sets the current amount of stamina to the given amount of stamina
     * @param   stamina
     *          The new amount of stamina of the unit
     * @pre     The stamina must be greater or equal than 0 and smaller or equal than max stamina
     *          | (stamina <= getMaxPoints()) && (stamina >= 0;)
     * @post    The new stamina equal the given stamina
     *          | new.getStamina() == stamina
     *
     */
    public void setStamina(double stamina){
        assert stamina <= getMaxPoints() && stamina >= 0;
        this.stamina = stamina;
    }

    /**
     * Return the maximum amount of hitPoints and stamina
     * @return  Returns 200*weight/100*toughness/100
     *          | result == ceil(200*weight*toughness/10000)
     */
    public int getMaxPoints() {
        return (200*weight*toughness+9999)/10000;
    }
    //</editor-fold>

    //<editor-fold desc="Orientation">

    /**
     * Returns whether the orientation is valid
     * @return  Returns true if the orientation is equal to or larger than 0
     *          and smaller than 2*PI
     *          | result == (orientation < 2*Math.PI) && (orientation >= 0)
     */
    public boolean isValidOrientation(double orientation) {
        return orientation < 2*Math.PI && orientation >= 0;
    }

    /**
     * Returns the current orientation
     */
    @Basic
    public double getOrientation() {
        return this.orientation;
    }

    /**
     * Sets the orientation of the unit
     * @param   orientation
     *          The new orientation
     * @post    The new orientation is the same as the old orientation but between 0 and 2*PI
     *          | new.getOrientation() == ((2*Math.PI) + (orientation % (2*Math.PI))) % 2* Math.PI
     */
    public void setOrientation(double orientation) {
        this.orientation = ((Math.PI*2) + (orientation % (2*Math.PI))) % (2*Math.PI);
    }
    //</editor-fold>

    //<editor-fold desc="Movement">
    /**
     * Returns True if the unit is moving
     */
    @Basic
    public boolean isMoving() {
        return currentActivity == Activity.MOVE;
    }

    /**
     * Starts the unit moving towards one of the adjacent cubes.
     * @param   dx
     *          the x direction
     * @param   dy
     *          the y direction
     * @param   dz
     *          the z direction
     * @throws  IllegalArgumentException
     *          If the target cube is not within the world bounds
     *          | !isValidPosition(this.getPosition()[0] + dx,this.getPosition()[1] + dy,this.getPosition()[2] + dz)
     */
    public void moveToAdjacent(int dx, int dy, int dz) throws IllegalArgumentException {
        if (!canHaveAsActivity(Activity.MOVE)) {
            throw new IllegalArgumentException("can't move right now");
        }

        int[] curPos = getCubePosition(getPosition());
        Vector target = new Vector(curPos[0] + dx, curPos[1] + dy, curPos[2] + dz);
        target = target.add(Lc/2);
        //target = target.map((double val) -> val + Lc/2);

        if (!isValidPosition(target.toDoubleArray())) {
            throw new IllegalArgumentException("target out of bounds");
        }
        setTargetNeighbour(target);
        setSpeed(calculateSpeed(target));
        setOrientation(Math.atan2(this.speed.getY(), this.speed.getX()));

        setCurrentActivity(Activity.MOVE);
    }

    /**
     * Starts the units movement to the given target cube.
     * @param   target
     *          The coordinates of the target cubes
     * @throws  IllegalArgumentException
     *          If the given target is not valid
     *          | !isValidPosition(target[0], target[1], target[2])
     */
    public void moveTo(int[] target) throws IllegalArgumentException {
        if (!canHaveAsActivity(Activity.MOVE)) {
            throw new IllegalArgumentException("can't path right now");
        }
        Vector newTarget = new Vector(target[0], target[1], target[2]);
        newTarget = newTarget.add(Lc/2);
        if (!isValidPosition(newTarget.toDoubleArray())) {
            throw new IllegalArgumentException("invalid target");
        }
        setTarget(newTarget);

        if (this.targetNeighbour == null)
            setTargetNeighbour(this.position); // initial neighbour, gets reset in advanceTime

        if (this.speed == null)
            setSpeed(new Vector(0, 0, 0)); // gets calculated in moveToAdjacent, which gets called in advanceTime

        setCurrentActivity(Activity.MOVE);
    }
    //</editor-fold>

    //<editor-fold desc="Speed">

    private void setSpeed(Vector speed) {
        this.speed = speed;
    }

    private Vector calculateSpeed(Vector target){
        double vb = 1.5*(getStrength()+getAgility())/(2*(getWeight()));
        Vector diff = target.substract(this.position);
        double d = diff.norm();
        diff = diff.divide(d);

        double vw = vb;
        if (diff.getZ() > POS_EPS) {
            vw = 0.5*vb;
        }
        else if (diff.getZ() < -POS_EPS) {
            vw = 1.2*vb;
        }
        return diff.multiply(vw);
    }

    /**
     * Gets the units movement speed.
     */
    @Basic
    public double getSpeedScalar() {
        double speedScalar;
        if (this.speed != null) {
            speedScalar = this.speed.norm();
        } else {
            speedScalar = 0;
        }

        if (isSprinting()) {
            return 2*speedScalar;
        }
        return speedScalar;
    }
    //</editor-fold>

    //<editor-fold desc="Sprinting">
    /**
     * Enables or disables sprint mode.
     * @post    ...
     *          | ...
     */
    public void setSprint(boolean sprint) {
        if (sprint && (getStamina() == 0 || !isMoving())) {
            return;
        }
        if (!this.isSprinting() && sprint) {
            sprintStaminaTimer = 0.1;
        }
        this.sprinting = sprint;
    }
    /**
     * Returns True if the unit is sprinting
     */
    @Basic
    public boolean isSprinting() {
        return this.sprinting;
    }
    //</editor-fold>

    //<editor-fold desc="Woring">
    /**
     * Returns True if the unit is working
     */
    @Basic
    public boolean isWorking() {
        return currentActivity == Activity.WORK;
    }

    /**
     * Start working
     */
    public void work() throws IllegalArgumentException {
        //TODO: interrupt moveTo but not moveToAdjacent
        if (!canHaveAsActivity(Activity.WORK)) {
            throw new IllegalArgumentException("can't work right now");
        }
        this.currentActivity = Activity.WORK;
        this.workTimer = 500.0 / getStrength();
    }
    //</editor-fold>

    //<editor-fold desc="Fighting">
    /**
     * Returns True if the unit is attacking
     */
    @Basic
    public boolean isAttacking() {
        return currentActivity == Activity.ATTACK;
    }

    /**
     * Attacks an other unit
     *
     */
    public void attack(Unit other) throws IllegalArgumentException {
        if (other == null)
            throw new IllegalArgumentException("the other unit is invalid");
        if (!canHaveAsActivity(Activity.ATTACK)) {
            throw new IllegalArgumentException("can't attack right now");
        }
        Vector otherPos = new Vector(other.getPosition());
        int[] otherCube = getCubePosition(otherPos.toDoubleArray());
        int[] posCube = getCubePosition(this.getPosition());

        for (int i = 0; i < 3; i++) {
            int diff = otherCube[i] - posCube[i];
            if (diff > 1 || diff < -1) {
                throw new IllegalArgumentException("the other unit is to far away");
            }
        }

        //TODO: add method to other?
        other.currentActivity = Activity.NONE;

        Vector diff = otherPos.substract(this.position);
        this.setOrientation(Math.atan2(diff.getY(), diff.getX()));

        currentActivity = Activity.ATTACK;
        attackTimer = 1;
        this.defender = other;
    }

    public void defend(Unit attacker) {
        Vector otherPos = new Vector(attacker.getPosition());
        Vector diff = otherPos.substract(this.position);
        this.setOrientation(Math.atan2(diff.getY(), diff.getX()));

        this.currentActivity = Activity.NONE; // switch back to default activity

        double probabilityDodge = 0.20 * (this.getAgility() / attacker.getAgility());
        if (Math.random() <= probabilityDodge) {
            // dodge
            ArrayList<int[]> possiblePositions = new ArrayList<>();
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -1; dy < 2; dy++) {
                    for (int dz = -1; dz < 2; dz++) {
                        if (!(dx == 0 && dy == 0 && dz == 0)) {
                            Vector pos = this.position.add(new Vector(dx, dy, dz));
                            if (isValidPosition(pos.toDoubleArray())) {
                                possiblePositions.add(new int[]{dx, dy, dz});
                            }
                        }
                    }
                }
            }
            int randIndex = (int)Math.floor(Math.random() * possiblePositions.size());
            int[] dp = possiblePositions.get(randIndex);
            moveToAdjacent(dp[0], dp[1], dp[2]);
        } else {
            double probabilityBlock = 0.25 *
                    ((this.getStrength()+this.getAgility())/(attacker.getStrength()+attacker.getAgility()));
            if (Math.random() > probabilityBlock) {
                setHitPoints(getHitPoints() - (attacker.getStrength()/10));
            }
        }

    }
    //</editor-fold>

    //<editor-fold desc="Resting">
    /**
     * Returns True if the unit is resting
     */
    @Basic
    public boolean isResting() {
        return currentActivity == Activity.REST;
    }

    /**
     * Starts resting
     * @post    The unit is resting
     *          | new.isResting() == true
     * @throws  RuntimeException
     *          Throws if the unit is moving
     *          | this.isMoving()
     */
    public void rest() throws RuntimeException {
        if (!canHaveAsActivity(Activity.REST))
            throw new RuntimeException("can't rest right now");
        currentActivity = Activity.REST;
        this.restTimer = 0.2;
        this.restHPDiff = 0;
        this.initialRest = true;
    }
    //</editor-fold>

    //<editor-fold desc="Default behaviour">
    public void startDefaultBehaviour() {
        this.defaultEnabled = true;
    }

    public void stopDefaultBehaviour() {
        this.defaultEnabled = false;
    }

    public boolean isDefaultEnabled() {
        return  this.defaultEnabled;
    }
    //</editor-fold>
}
