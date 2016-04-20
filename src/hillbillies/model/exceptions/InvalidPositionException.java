package hillbillies.model.exceptions;

import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

/**
 * Exception indicating an invalid position.
 */
public class InvalidPositionException extends RuntimeException {
    public InvalidPositionException(IntVector pos) {
        super("Invalid position: " + pos.toString());
    }

    public InvalidPositionException(Vector pos) {
        super("Invalid position: " + (pos == null ? "<null>" : pos.toString()));
    }

    public InvalidPositionException(String message, Vector pos) {
        super(message + pos.toString());
    }

    public InvalidPositionException(String message, IntVector pos) {
        super(message + pos.toString());
    }
}
