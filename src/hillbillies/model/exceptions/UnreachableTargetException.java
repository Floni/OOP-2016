package hillbillies.model.exceptions;

/**
 * Exception indicating an unreachable target.
 */
public class UnreachableTargetException extends RuntimeException {
    private static final long serialVersionUID = -9180903895768853822L;

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
