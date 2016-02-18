package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * ....
 * TODO: split isValidPosition in effective & in bounds
 *
 * @invar   The position of the unit must be valid
 *          | isValidPosition(this.getPosition())
 */
public class Unit {
    public static final int X_MAX = 50;
    public static final int Y_MAX = 50;
    public static final int Z_MAX = 50;

    public static final double Lc = 1.0;

    private final double[] position = new double[3];


    /**
     * Creates a new unit with the given position
     * @param   x
     *          The initial x value of the position
     * @param   y
     *          The initial y value of the position
     * @param   z
     *          The initial z value of the position
     * @effect  Sets the position to the middle of the block
     *          | setPosition(x + Lc/2, y + Lc/2, z + Lc/2)
     *
     */
    public Unit(int x, int y, int z) {
        setPosition(x + Lc/2, y + Lc/2, z + Lc/2);
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
     *          | result == position != null && position.length == 3 && isValidPosition(position[0], position[1], position[2])
     */
    public static boolean isValidPosition(double[] position) {
        return position != null && position.length == 3 && isValidPosition(position[0], position[1], position[2]);
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
        if (!isValidPosition(position))
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



}
