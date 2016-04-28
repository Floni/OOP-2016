package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.expression.BooleanExpression;

/**
 * Class for if e then ... [else ...] fi
 *
 */
public class IfStatement implements Statement {

    private final BooleanExpression condition;
    private final Statement trueStmt;
    private final Statement falseStmt;

    private Boolean cond;

    public IfStatement(BooleanExpression condition, Statement trueStmt, Statement falseStmt) {
        this.condition = condition;
        this.trueStmt = trueStmt;
        this.falseStmt = falseStmt;
    }

    @Override
    public void reset() {
        trueStmt.reset();
        if (falseStmt != null)
            falseStmt.reset();
        cond = null;
    }

    @Override
    public boolean isDone(Task task) {
        return cond != null && (cond ? trueStmt.isDone(task) : falseStmt == null || falseStmt.isDone(task));
    }

    @Override
    public void execute(Task task) {
        if (cond == null)
            cond = condition.getValue(task);

        if (cond && !trueStmt.isDone(task)) {
            trueStmt.execute(task);
        } else if (!cond && falseStmt != null && !falseStmt.isDone(task)) {
            falseStmt.execute(task);
        }
    }

    @Override
    public BreakChecker checkValid(BreakChecker breakChecker) {
        trueStmt.checkValid(breakChecker);
        if (falseStmt != null)
            falseStmt.checkValid(breakChecker);
        return breakChecker;
    }
}
