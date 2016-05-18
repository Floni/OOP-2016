package hillbillies.model.exceptions;

/**
 * Exception indicating an unreachable target.
 */
public class UnreachableTargetException extends RuntimeException {
    public UnreachableTargetException() {
        super("Unable to reach target position");
    }
}
