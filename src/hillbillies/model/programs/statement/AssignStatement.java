package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.BreakException;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.programs.expression.Expression;

/**
 * Class for name := expr.
 */
public class AssignStatement<T> implements Statement {
    private final String varName;
    private final Expression<T> expression;
    private boolean done;

    public AssignStatement(String name, Expression<T> expr) {
        this.varName = name;
        this.expression = expr;
        this.reset();
    }

    @Override
    public void reset() {
        done = false;
    }

    @Override
    public boolean isDone(Task task) {
        return done;
    }

    @Override
    public void execute(Task task) throws TaskErrorException, TaskInterruptException, BreakException {
        task.setVariable(varName, expression.getValue(task));
        done = true;
    }
}
