package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Interface for statements.
 */
public interface Statement {

    /**
     * reset the statement progress
     */
    void reset();

    boolean isDone(Task task);

    /**
     * execute one step of the statement.
     *
     * @throws  TaskErrorException
     *          When the task has an unrecoverable error.
     * @throws  TaskInterruptException
     *          When the task needs to be interrupted and another unit may execute it.
     * @throws  BreakException
     *          When a while loop breaks, shouldn't happen in an valid program.
     */
    void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException;

    /**
     * Checks if the Statement is valid, returns the break checker it was passed.
     */
    default BreakChecker checkValid(BreakChecker breakChecker) {
        return breakChecker;
    }
}










