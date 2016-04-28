package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for break.
 */
public class BreakStatement implements Statement {
    @Override
    public void reset() {

    }

    @Override
    public boolean isDone(Task task) {
        return false;
    }

    @Override
    public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
        throw new BreakException();
    }

    @Override
    public BreakChecker checkValid(BreakChecker breakChecker) {
        breakChecker.testBreak();
        return breakChecker;
    }
}
