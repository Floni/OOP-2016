package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.expression.BooleanExpression;

/**
 * Class for While loop.
 */
public class WhileStatement implements Statement {

    private final Statement body;
    private final BooleanExpression cond;
    private Boolean condition;

    public WhileStatement(BooleanExpression condition, Statement body) {
        this.body = body;
        this.cond = condition;
    }
    
    @Override
    public void reset() {
        condition = null;
        this.body.reset();
    }

    @Override
    public boolean isDone(Task task) {
        return condition != null && !condition;
    }

    @Override
    public void execute(Task task) throws TaskErrorException, TaskInterruptException {
        if (condition == null)
            condition = cond.getValue(task);

        try {
            if (condition) {
                if (!body.isDone(task))
                    body.execute(task);

                if (body.isDone(task))
                    this.reset();
            }
        } catch (BreakException stmt) {
            this.condition = false;
        }
    }

    @Override
    public BreakChecker checkValid(BreakChecker breakChecker) {
        breakChecker.enterWhile();
        body.checkValid(breakChecker);
        breakChecker.exitWhile();
        return breakChecker;
    }

}
