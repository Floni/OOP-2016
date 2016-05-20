package hillbillies.model.exceptions;

/**
 * Exception for dealing with invalid actions.
 */
public class InvalidActionException extends RuntimeException {

    private static final long serialVersionUID = -8043897882900826336L;

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
