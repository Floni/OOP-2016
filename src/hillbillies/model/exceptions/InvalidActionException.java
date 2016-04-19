package hillbillies.model.exceptions;

/**
 * Exception for dealing with invalid actions.
 */
public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String message) {
        super(message);
    }
}
