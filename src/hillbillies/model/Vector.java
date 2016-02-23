package hillbillies.model;

import be.kuleuven.cs.som.annotate.Value;
import ogp.framework.util.Util;

//TODO: add comments

/**
 * Basic Vector class
 */
@Value
public class Vector {
    public interface MapFunc {
        double func(double val);
    }
    private final double X;
    private final double Y;
    private final double Z;

    public Vector(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }
    public Vector(double[] coords) {
        if (coords.length != 3)
            throw new IllegalArgumentException("invalid coords");
        X = coords[0];
        Y = coords[1];
        Z = coords[2];
    }

    public Vector multiply(double scalar) {
        return new Vector(getX()*scalar, getY()*scalar, getZ()*scalar);
    }

    public Vector divide(double divisor) {
        return new Vector(getX()/divisor, getY()/divisor, getZ()/divisor);
    }

    public Vector add(double constant) {
        return new Vector(getX() + constant, getY()+constant, getZ()+constant);
    }

    public Vector substract(double constant) {
        return new Vector(getX() - constant, getY()-constant, getZ()-constant);
    }

    public Vector add(Vector other) {
        return new Vector(getX() + other.getX(), getY() + other.getY(), getZ() + other.getZ());
    }

    public Vector substract(Vector other) {
        return new Vector(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    public double dot(Vector other) {
        return  getX()*other.getX() + getY()*other.getY() + getZ()*other.getZ();
    }

    public double norm() {
        return Math.sqrt(this.dot(this));
    }

    public boolean isEqualsTo(Vector other, double eps) {
        return other != null && Util.fuzzyEquals(getX(), other.getX(), eps)
                && Util.fuzzyEquals(getY(), other.getY(), eps)
                && Util.fuzzyEquals(getZ(), other.getZ(), eps);
    }

    public double[] toDoubleArray() {
        return new double[] {
                X,
                Y,
                Z
        };
    }

    public Vector map(MapFunc f) {
        return new Vector(f.func(getX()), f.func(getY()), f.func(getZ()));
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }

    public void print() {
        System.out.println(String.format("(%f, %f, %f)", getX(), getY(), getZ()));
    }
}