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

    // TODO: randint, inclusive and exclusive.
}
