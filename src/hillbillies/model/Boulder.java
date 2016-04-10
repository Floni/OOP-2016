package hillbillies.model;

import hillbillies.model.Vector.IntVector;

/**
 * Created by timo on 3/14/16.
 *
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
    public Boulder(World world, IntVector vector) {
        super(world, vector);
    }
}
