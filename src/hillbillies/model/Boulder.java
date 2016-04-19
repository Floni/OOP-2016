package hillbillies.model;

import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.vector.IntVector;

/**
 * Class for boulder, there is no difference between a boulder and a log.
 */
public class Boulder extends GameObject {

    /**
     * Create a new Boulder with the given position in the given world (used for bounds).
     * @param   world
     *          The world in which the Boulder is created.
     * @param   vector
     *          The position of the new Boulder.
     *
     * @effect  Init the GameObject
     *          | super(world, vector)
     */
    public Boulder(World world, IntVector vector) throws InvalidPositionException {
        super(world, vector);
    }
}
