package hillbillies.model.exceptions;

/**
 * Exception for dealing with invalid actions.
 */
public class InvalidActionException extends RuntimeException {
    /**
     * Create a new exception.
     *
     * @param   message
     *          | The message for this exception.
     *
     * @effect  Initialize super.
     *          | super(message)
     */
    public InvalidActionException(String message) {
        super(message);
    }
}
