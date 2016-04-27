package hillbillies.model.vector;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Value;

/**
 * Java doesn't allow math on general number type -> we can't use generics.
 *
 */
@Value
public class IntVector {
    public static final IntVector ZERO = new IntVector(0, 0, 0);

    private final int X;
    private final int Y;
    private final int Z;

    /**
     * Create a new IntVector with the given x, y and z coordinate.
     * @param   x
     *          The x coordinate
     * @param   y
     *          The y coordinate
     * @param   z
     *          The z coordinate
     * @post    The given coordinates are used
     *          | new.getX() == x && new.getY() == y && new.getZ() == z
     */
    public IntVector(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    /**
     * Create a new IntVector with the given coordinates
     * @param   coords
     *          x, y and z coordinates in a int array
     * @post    The given coordinates are used
     *          | new.getX() == coords[0] && new.getY() == coords[1] && new.getZ() == coords[2]
     * @throws  IllegalArgumentException
     *          The given array must have length three
     *          | coords.length != 3
     */
    public IntVector(int[] coords) {
        if (coords.length != 3)
            throw new IllegalArgumentException("invalid coords");
        X = coords[0];
        Y = coords[1];
        Z = coords[2];
    }

    public IntVector(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public IntVector(double x, double y, double z) {
        this((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    /**
     * Multiplies the vector with the given constant
     * @param   scalar
     *          | The constant with which each coordinate is muliplied
     * @return  A new vector ...
     *          | result.getX() == this.getX() * scalar &&
     *          | result.getY() == this.getY() * scalar &&
     *          | result.getZ() == this.getZ() * scalar
     */
    public IntVector multiply(int scalar) {
        return new IntVector(getX()*scalar, getY()*scalar, getZ()*scalar);
    }

    /**
     * Divides the vector with the given constant
     * @param   divisor
     *          | The constant with which each coordinate is divided
     * @return  A new vector ...
     *          | result.getX() == this.getX() / divisor &&
     *          | result.getY() == this.getY() / divisor &&
     *          | result.getZ() == this.getZ() / divisor
     */
    public IntVector divide(int divisor) {
        return new IntVector(getX()/divisor, getY()/divisor, getZ()/divisor);
    }

    /**
     * Adds the given constant to each coordinate of the vector
     *
     * @param   constant
     *          | The constant
     *
     * @return  A new vector ...
     *          | result.getX() == this.getX() + constant &&
     *          | result.getY() == this.getY() + constant &&
     *          | result.getZ() == this.getZ() + constant
     */
    public IntVector add(int constant) {
        return new IntVector(getX() + constant, getY()+constant, getZ()+constant);
    }

    /**
     * Adds the given constant to each coordinate of the vector
     *
     * @param   dx  the x diff
     * @param   dy  the y diff
     * @param   dz  the z diff
     *
     * @return  A new vector ...
     *          | result.getX() == this.getX() + dx &&
     *          | result.getY() == this.getY() + dz &&
     *          | result.getZ() == this.getZ() + dy
     */
    public IntVector add(int dx, int dy, int dz) {
        return new IntVector(getX() + dx, getY()+dy, getZ()+dz);
    }

    /**
     * Adds the given vector to this vector
     * @param   other
     *          | The vector to be added
     * @return  A new vector ...
     *          | result.getX() == this.getX() + other.getX() &&
     *          | result.getY() == this.getY() + other.getY() &&
     *          | result.getZ() == this.getZ() + other.getZ()
     */
    public IntVector add(IntVector other) {
        return new IntVector(getX() + other.getX(), getY() + other.getY(), getZ() + other.getZ());
    }

    /**
     * Subtracts the given vector to this vector
     * @param   other
     *          | The vector to be subtracted
     * @return  A new vector ...
     *          | result.getX() == this.getX() - other.getX() &&
     *          | result.getY() == this.getY() - other.getY() &&
     *          | result.getZ() == this.getZ() - other.getZ()
     */
    public IntVector subtract(IntVector other) {
        return new IntVector(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    /**
     * Calculates the dot product of 2 vectors
     * @param   other
     *          | The other vector for the dot product
     * @return  The dot product
     *          | result == (this.getX()*other.getX() + this.getY()*other.getY() + this.getZ()*other.getZ())
     */
    public int dot(IntVector other) {
        return  getX()*other.getX() + getY()*other.getY() + getZ()*other.getZ();
    }

    /**
     * Calculates the norm of the vector
     * @return  The norm
     *          | result == Math.sqrt(this.dot(this))
     */
    public double norm() {
        return Math.sqrt(this.dot(this));
    }

    /**
     * Compares two vectors
     * @param   other
     *          | The vector with which this vector is compared
     * @return  True if the two vectors are equal and the other vector is effective, False otherwise.
     *          | result == (other != null
     *          |            && Util.fuzzyEquals(this.getX(), other.getX(), eps)
     *          |            && Util.fuzzyEquals(this.getY(), other.getY(), eps)
     *          |            && Util.fuzzyEquals(this.getZ(), other.getZ(), eps))
     */
    public boolean isEqualTo(IntVector other) {
        return other != null && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
    }

    /**
     * Converts the vector to an array of ints
     * @return  An array with length three where each element is either x, y or z.
     *          | result.length == 3 &&
     *          | result[0] == this.getX() &&
     *          | result[1] == this.getY() &&
     *          | result[2] == this.getZ()
     */
    public int[] toIntArray() {
        return new int[] {
                X,
                Y,
                Z
        };
    }

    /**
     * Creates a new (double) vector from this intVector.
     * (if cube centres are needed don't forget to ad Lc/2)
     *
     * @return  A vector with the same values.
     *          | result.getX() == this.getX() &&
     *          | result.getY() == this.getY() &&
     *          | result.getZ() == this.getZ()
     */
    public Vector toVector() {
        return new Vector(getX(), getY(), getZ());
    }

    /**
     * Returns the x coordinate of the vector.
     */
    @Basic
    @Immutable
    public int getX() {
        return X;
    }

    /**
     * Returns the y coordinate of the vector.
     */
    @Basic @Immutable
    public int getY() {
        return Y;
    }

    /**
     * Returns the z coordinate of the vector.
     */
    @Basic @Immutable
    public int getZ() {
        return Z;
    }

    /**
     * Returns a string representation of the given vector.
     */
    @Override
    public String toString() {
        return String.format("Vector(%d, %d, %d)", getX(), getY(), getZ());
    }

    /**
     * Returns a hashcode of the vector.
     */
    @Override
    public int hashCode() {
        return (8017*getX() + 104831 * getY() + 1301077 * getZ()) + 17;
    }

    /**
     * Check whether this vector is equal to the given object.
     * @param   obj
     *          The object with which to compare the vector.
     * @return  True if the vectors have the same values.
     *          | result == obj != null &&
     *          |   obj.getClass().equals(IntVector.class) &&
     *          |   this.isEqualTo((IntVector)obj)
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(IntVector.class) && this.isEqualTo((IntVector)obj);
    }

    /**
     * Returns true if two vectors are next to each other.
     *
     * @param   vector
     *          The vector that may be next to this vector.
     * @return  True if they are next to each other.
     *          | result == Math.abs(vector.getX() - this.getX()) <= 1
     *          |   && Math.abs(vector.getY() - this.getY()) <= 1
     *          |   && Math.abs(diff.getZ() - this.getZ()) <= 1
     */
    public boolean isNextTo(IntVector vector) {
        IntVector diff = vector.subtract(this);
        return Math.abs(diff.getX()) <= 1 && Math.abs(diff.getY()) <= 1 && Math.abs(diff.getZ()) <= 1;
    }

    /**
     * Returns the distance between two vectors.
     *
     * @param   other
     *          The vector to calculate the distance to.
     * @return  The distance.
     *          | result == other.subtract(this).norm()
     */
    public double distance(IntVector other) {
        return other.subtract(this).norm();
    }
}
