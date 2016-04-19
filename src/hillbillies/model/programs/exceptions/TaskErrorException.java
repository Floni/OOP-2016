package hillbillies.model.programs.exceptions;

/**
 * Thrown when a task has a fatal error. (invalid position).
 */
public class TaskErrorException extends RuntimeException {
    public TaskErrorException(String message) {
        super(message);
    }
}
