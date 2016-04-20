package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Created by timo on 4/19/16.
 */
public class BreakStatement extends RuntimeException implements Statement {
    @Override
    public void reset() {

    }

    @Override
    public boolean isDone(Task task) {
        return false;
    }

    @Override
    public void execute(Task task) throws TaskErrorException, TaskInterruptException {
        throw new BreakException();
    }

    @Override
    public void isValid(BreakChecker breakChecker) {
        breakChecker.testBreak();
    }
}
