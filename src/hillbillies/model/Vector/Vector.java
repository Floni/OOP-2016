package hillbillies.model.Vector;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Value;
import ogp.framework.util.Util;

/**
 * Basic Vector class
 */
@Value
public class Vector {
    public static final Vector ZERO = new Vector(0, 0, 0);
    public static final Vector IDENT = new Vector(1, 1, 1);

    private final double X;
    private final double Y;
    private final double Z;

    /**
     * Create a new vector with the given x, y and z coordinate.
     * @param   x
     *          The x coordinate
     * @param   y
     *          The y coordinate
     * @param   z
     *          The z coordinate
     * @post    The given coordinates are used
     *          | new.getX() == x && new.getY() == y && new.getZ() == z
     */
    public Vector(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }

    /**
     * Create a new vector with the given coordinates
     * @param   coords
     *          x, y and z coordinates in a double array
     * @post    The given coordinates are used
     *          | new.getX() == coords[0] && new.getY() == coords[1] && new.getZ() == coords[2]
     * @throws  IllegalArgumentException
     *          The given array must have length three
     *          | coords.length != 3
     */
    public Vector(double[] coords) {
        if (coords.length != 3)
            throw new IllegalArgumentException("invalid coords");
        X = coords[0];
        Y = coords[1];
        Z = coords[2];
    }

    public Vector(int[] cubePosition) {
        this(cubePosition[0], cubePosition[1], cubePosition[2]);
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
    public Vector multiply(double scalar) {
        return new Vector(getX()*scalar, getY()*scalar, getZ()*scalar);
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
    public Vector divide(double divisor) {
        return new Vector(getX()/divisor, getY()/divisor, getZ()/divisor);
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
    public Vector add(double constant) {
        return new Vector(getX() + constant, getY()+constant, getZ()+constant);
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
    public Vector add(double dx, double dy, double dz) {
        return new Vector(getX() + dx, getY()+dy, getZ()+dz);
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
    public Vector add(Vector other) {
        return new Vector(getX() + other.getX(), getY() + other.getY(), getZ() + other.getZ());
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
    public Vector subtract(Vector other) {
        return new Vector(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    /**
     * Calculates the dot product of 2 vectors
     * @param   other
     *          | The other vector for the dot product
     * @return  The dot product
     *          | result == (this.getX()*other.getX() + this.getY()*other.getY() + this.getZ()*other.getZ())
     */
    public double dot(Vector other) {
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
     * @param   eps
     *          | The maximum possible difference between each coordinats
     * @return  True if the two vectors are equal and the other vector is effective, False otherwise.
     *          | result == (other != null
     *          |            && Util.fuzzyEquals(this.getX(), other.getX(), eps)
     *          |            && Util.fuzzyEquals(this.getY(), other.getY(), eps)
     *          |            && Util.fuzzyEquals(this.getZ(), other.getZ(), eps))
     */
    public boolean isEqualTo(Vector other, double eps) {
        return other != null && Util.fuzzyEquals(getX(), other.getX(), eps)
                && Util.fuzzyEquals(getY(), other.getY(), eps)
                && Util.fuzzyEquals(getZ(), other.getZ(), eps);
    }

    /**
     * Converts the vector to an array of doubles
     * @return  An array with length three where each element is either x, y or z.
     *          | result.length == 3 &&
     *          | result[0] == this.getX() &&
     *          | result[1] == this.getY() &&
     *          | result[2] == this.getZ()
     */
    public double[] toDoubleArray() {
        return new double[] {
                X,
                Y,
                Z
        };
    }

    public IntVector toIntVector() {
        return new IntVector(this);
    }

    /**
     * Returns the x coordinate of the vector.
     */
    @Basic @Immutable
    public double getX() {
        return X;
    }

    /**
     * Returns the y coordinate of the vector.
     */
    @Basic @Immutable
    public double getY() {
        return Y;
    }

    /**
     * Returns the z coordinate of the vector.
     */
    @Basic @Immutable
    public double getZ() {
        return Z;
    }

    /**
     * Returns a string representation of the vector.
     */
    @Override
    public String toString() {
        return String.format("Vector(%f, %f, %f)", getX(), getY(), getZ());
    }


    /**
     * Returns a hashcode of the vector.
     */
    @Override
    public int hashCode() {
        return (int)(8017*getX() + 104831 * getY() + 1301077 * getZ()) + 17;
    }

    /**
     * Checks whether the vector is equal to the given object.
     * Values are compared with a precision of 1e-6, for different precisions use isEqualTo(other).
     * @param   obj
     *          The object to compare to.
     *
     * @return  True if the object is a effective vector and the values are equal.
     *          | result == obj != null &&
     *          |   obj.getClass().equals(Vector.class) &&
     *          |   this.isEqualTo((Vector)obj, 1e-6);
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(Vector.class) && this.isEqualTo((Vector)obj, 1e-6);
    }
}