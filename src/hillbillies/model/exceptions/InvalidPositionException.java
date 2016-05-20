package hillbillies.model.exceptions;

import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

/**
 * Exception indicating an invalid position.
 */
public class InvalidPositionException extends RuntimeException {
    /**
     * Create an exception indicating that the given position is invalid.
     *
     * @param   pos
     *          The invalid Position.
     *
     * @effect  Initialize the exception.
     *          | super("Invalid position: " + (pos == null ? "<null>" : pos.toString()))
     */
    public InvalidPositionException(IntVector pos) {
        super("Invalid position: " + (pos == null ? "<null>" : pos.toString()));
    }

    /**
     * Create an exception indicating that the given position is invalid.
     *
     * @param   pos
     *          The invalid Position.
     *
     * @effect  Initialize the exception.
     *          | super("Invalid position: " + (pos == null ? "<null>" : pos.toString()))
     */
    public InvalidPositionException(Vector pos) {
        super("Invalid position: " + (pos == null ? "<null>" : pos.toString()));
    }

    /**
     * Create an exception indicating that the given position is invalid with the given message.
     *
     * @param   message
     *          The message to display.
     * @param   pos
     *          The invalid Position.
     *
     * @effect  Initialize the exception.
     *          | super(message + (pos == null ? "<null>" : pos.toString()))
     */
    public InvalidPositionException(String message, Vector pos) {
        super(message + (pos == null ? "<null>" : pos.toString()));
    }

    /**
     *
     * @param   message
     *          The message to display.
     * @param   pos
     *          The invalid Position.
     *
     * @effect  Initialize the exception.
     *          | super(message + (pos == null ? "<null>" : pos.toString()))
     */
    public InvalidPositionException(String message, IntVector pos) {
        super(message + (pos == null ? "<null>" : pos.toString()));
    }
}
