package hillbillies.model.util;

/**
 * Class that provides various useful methods.
 */
public final class Util {
    /**
     * Clams the value x between min and max.
     *
     * @param   x
     *          The value to clamp.
     * @param   min
     *          The minimal value of the result.
     * @param   max
     *          The maximal value of the result.
     *
     * @return  x clamped between min and max, inclusively.
     *          | if (x > max) then result == max
     *          | else if (x < min) then result == min
     *          | else result == x
     */
    public static int clamp(int x, int min, int max) {
        if (x > max)
            return max;
        if (x < min)
            return min;
        return x;
    }


    /**
     * Returns an integer between the given min and max.
     *
     * @param   min
     *          The given minimum for the random integer.
     * @param   max
     *          The given maximum for the given integer.
     *
     * @return  An integer larger than or equal to min and smaller than max.
     *          | result == min + randomInt(max - min)
     */
    public static int randomExclusive(int min, int max) {
        return min + randomInt(max - min);
    }


    /**
     * Returns an random integer between 0 and the given max.
     *
     * @param   max
     *          The upper boundary for the random integer.
     *
     * @return  Returns a random integer larger than or equal to 0 and smaller than the given max.
     *          | result == Math.floor(Math.random()*max)
     */
    public static int randomInt(int max) {
        return (int) Math.floor(Math.random()*max);
    }


}
