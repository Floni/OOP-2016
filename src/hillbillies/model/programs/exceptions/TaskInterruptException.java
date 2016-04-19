package hillbillies.model.programs.exceptions;

/**
 * Thrown when a task has to be interrupted because of an error (Unreachable position, no neighbours, ..).
 */
public class TaskInterruptException extends RuntimeException {
    public TaskInterruptException(String message) {
        super(message);
    }
}
