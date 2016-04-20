package hillbillies.model.programs.statement;

import hillbillies.model.Task;
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
     *          ...
     * @throws  TaskInterruptException
     *          ....
     */
    void execute(Task task) throws TaskErrorException, TaskInterruptException;

    /**
     *
     */
    void isValid(BreakChecker breakChecker);
}










