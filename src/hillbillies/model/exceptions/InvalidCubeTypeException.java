package hillbillies.model.exceptions;

/**
 * Class for indicating an invalid cube type.
 */
public class InvalidCubeTypeException extends RuntimeException {
    /**
     * Create an invalidCubeTypeException with the given cube type.
     *
     * @param   type
     *          The id of the cube type that is invalid.
     *
     * @effect  Initialize the exception.
     *          | super("Invalid cube type: " + Integer.toString(type))
     */
    public InvalidCubeTypeException(int type) {
        super("Invalid cube type: " + Integer.toString(type));
    }
}
