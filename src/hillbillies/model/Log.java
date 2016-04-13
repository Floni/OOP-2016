package hillbillies.model;

import hillbillies.model.vector.IntVector;

/**
 * Class for boulder, there is no difference between a boulder and a log.
 */
public class Log extends GameObject {

    /**
     * Create a new Log with the given position in the given world (used for bounds).
     * @param   world
     *          The world in which the Log is created.
     * @param   vector
     *          The position of the new Log.
     *
     * @effect  Init the GameObject
     *          | super(world, vector)
     */
    public Log(World world, IntVector vector) {
        super(world, vector);
    }
}
