package hillbillies.model.programs.exceptions;

/**
 * Thrown when a task has to be interrupted because of an error (Unreachable position, no neighbours, ..).
 */
public class TaskInterruptException extends RuntimeException {
    private static final long serialVersionUID = -7219898873238481853L;

    public TaskInterruptException(String message) {
        super(message);
    }
}
