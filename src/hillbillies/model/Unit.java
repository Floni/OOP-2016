package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Value;
import ogp.framework.util.ModelException;

/**
 * ....
 *
 * @invar   The position of the unit must be valid
 *          | isValidPosition(this.getPosition())
 */
public class Unit {
    @Value
    private enum Activity {
        WORK,
        MOVE,
        SPRINT,
        REST,
        ATTACK,
        NONE
    }

    public static final int X_MAX = 50;
    public static final int Y_MAX = 50;
    public static final int Z_MAX = 50;

    public static final double Lc = 1.0;

    private final double[] position = new double[3];
    private String name;
    private int weight, strength, agility, toughness;
    private double orientation;
    private int hitPoints, stamina;

    private Activity currentActivity = Activity.NONE;
    private int[] target;


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
     * @effect  Sets the position to the middle of the block
     *          | setPosition(x + Lc/2, y + Lc/2, z + Lc/2)
     * @effect  Sets the name
     *          | setName(name)
     * @effect  Sets the attributes
     *          | setToughness(toughness) && setStrength(strength)
     *          | && setAgility(agility) && setWeight(weight)
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
        currentActivity = Activity.MOVE;
    }

    public void advanceTime(double dt) throws  ModelException {
        if (dt < 0 || dt >= 0.2)
            throw new ModelException("Invalid dt");
        if (isMoving()) {
            double[] oldPos = getPosition();
            setPosition(oldPos[0] + dt * Math.cos(getOrientation()), oldPos[1] + dt * Math.sin(getOrientation()), oldPos[2]);
            setOrientation(getOrientation() + Math.PI / 4 * dt);
        }

    }

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
     *          | new.getPosition() == {x, y, z}
     * @throws  IllegalArgumentException
     *          The given position is not valid
     *          | !isValidPosition(x,y,z)
     */
    public void setPosition(double x,double y,double z) throws IllegalArgumentException {
        if (!isValidPosition(x, y, z))
            throw new IllegalArgumentException("The given position is out of bounds");
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = z;
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
     * Gets the position of the unit
     */
    @Basic
    public double[] getPosition() {
        return position.clone();
    }

    /**
     * ...
     */
    public int[] getCubePosition() {
        return new int[] {
                (int)Math.floor(position[0]),
                (int)Math.floor(position[1]),
                (int)Math.floor(position[2])
        };
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
     * Returns the current amount of hitPoints of the unit
     */
    @Basic
    public int getHitPoints() {
        return this.hitPoints;
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
    public void setHitPoints(int hitPoints) {
        assert hitPoints <= getMaxPoints() && hitPoints >= 0;
        this.hitPoints = hitPoints;
    }

    /**
     * Returns the current amount of stamina of the unit
     */
    @Basic
    public int getStamina() {
        return this.stamina;
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
    public void setStamina(int stamina){
        assert stamina <= getMaxPoints();
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

    /**
     * Returns True if the unit is moving
     */
    @Basic
    public boolean isMoving() {
        return currentActivity == Activity.MOVE || currentActivity == Activity.SPRINT;
    }

    public void moveToAdjacent(int dx, int dy, int dz) {
        currentActivity = Activity.MOVE;
        target = new int[3];
        int[] curPos = getCubePosition();
        target[0] = curPos[0] + dx;
        target[1] = curPos[1] + dy;
        target[2] = curPos[2] + dz;
        //TODO: complete
    }

    /**
     * Returns True if the unit is working
     */
    @Basic
    public boolean isWorking() {
        return currentActivity == Activity.WORK;
    }

    /**
     * Returns True if the unit is attacking
     */
    @Basic
    public boolean isAttacking() {
        return currentActivity == Activity.ATTACK;
    }

    /**
     * Returns True if the unit is resting
     */
    @Basic
    public boolean isResting() {
        return currentActivity == Activity.REST;
    }

    public void rest() {
        currentActivity = Activity.REST;
    }

    /**
     * Returns True if the unit is sprinting
     */
    @Basic
    public boolean isSprinting() {
        return currentActivity == Activity.SPRINT;
    }
}
