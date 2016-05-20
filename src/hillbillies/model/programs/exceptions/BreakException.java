package hillbillies.model.programs.exceptions;

/**
 * Class representing a break signal. Thrown when a while loop should be interrupted.
 */
public class BreakException extends RuntimeException {
    private static final long serialVersionUID = -4288325238771072781L;

    public BreakException() {
        super("A break was executed outside a while loop");
    }
}
