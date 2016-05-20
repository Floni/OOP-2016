package hillbillies.model.programs.exceptions;

/**
 * Thrown when a task has a fatal error. (invalid position).
 */
public class TaskErrorException extends RuntimeException {
    private static final long serialVersionUID = -7545431442657818161L;

    public TaskErrorException(String message) {
        super(message);
    }
}
