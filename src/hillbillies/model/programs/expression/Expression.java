package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Interface providing Expression.
 */
public interface Expression<T> {

    /**
     * Calculate the value of this expression.
     *
     * @param   task
     *          The task executing the expression.
     * @return  The value of the expression.
     *
     * @throws  TaskInterruptException
     *          When the task needs to be interrupted, the unit will stop executing the task.
     * @throws  TaskErrorException
     *          When the task has a fatal error and can't be executed.
     */
    T getValue(Task task) throws TaskInterruptException, TaskErrorException;

    default Expression<T> getRead(String variable) {
        throw new IllegalStateException("invalid variable creation"); // TODO: better exception
    }
}
