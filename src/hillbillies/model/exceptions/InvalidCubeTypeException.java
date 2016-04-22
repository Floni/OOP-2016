package hillbillies.model.exceptions;

/**
 * Class for indicating an invalid cube type.
 */
public class InvalidCubeTypeException extends RuntimeException {
    public InvalidCubeTypeException(int type) {
        super("Invalid cube type: " + Integer.toString(type));
    }
}
