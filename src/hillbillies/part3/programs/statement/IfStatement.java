package hillbillies.part3.programs.statement;

import hillbillies.model.Task;
import hillbillies.part3.programs.expression.BooleanExpression;

/**
 * Created by timo on 4/15/16.
 */
public class IfStatement implements Statement {

    private enum State {
        COND,
        FALSE,
        TRUE
    }

    private final BooleanExpression condition;
    private final Statement trueStmt;
    private final Statement falseStmt;

    private State state;

    public IfStatement(BooleanExpression condition, Statement trueStmt, Statement falseStmt) {
        this.condition = condition;
        this.trueStmt = trueStmt;
        this.falseStmt = falseStmt;
        this.state = State.COND;
    }

    @Override
    public void reset() {
        if (state == State.TRUE)
            trueStmt.reset();
        else if (state == State.FALSE && falseStmt != null)
            falseStmt.reset();
        state = State.COND;
    }

    @Override
    public boolean isDone(Task task) {
        return state != State.COND && (state == State.TRUE ? trueStmt.isDone(task) : (falseStmt == null || falseStmt.isDone(task)));
    }

    @Override
    public void execute(Task task) {
        switch (state) {
            case COND:
                state = condition.getValue(task) ? State.TRUE : State.FALSE;
                break;
            case TRUE:
                trueStmt.execute(task);
                break;
            case FALSE:
                if (falseStmt != null)
                    falseStmt.execute(task);
                break;
        }
    }
}
