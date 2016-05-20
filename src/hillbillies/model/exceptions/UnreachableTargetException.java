package hillbillies.model.exceptions;

/**
 * Exception indicating an unreachable target.
 */
public class UnreachableTargetException extends RuntimeException {
    /**
     * Create an exception indicating that the target is unreachable.
     *
     * @effect  Initialize the exception.
     *          | super("Unable to reach target position")
     */
    public UnreachableTargetException() {
        super("Unable to reach target position");
    }
}
