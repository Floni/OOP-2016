package hillbillies.model.programs.statement;

import hillbillies.model.Task;

import javax.swing.plaf.nimbus.State;
import java.util.List;

/**
 * Created by timo on 4/14/16.
 */
public class SequenceStatement implements Statement {

    private final List<Statement> statements;
    private int currentStmtIdx;

    public SequenceStatement(List<Statement> statements) {
        this.statements = statements;
        this.reset();
    }

    @Override
    public void reset() {
        this.currentStmtIdx = 0;
        statements.forEach(Statement::reset);
    }

    @Override
    public boolean isDone(Task task) {
        // TODO
        return currentStmtIdx >= statements.size() ||
                (currentStmtIdx == statements.size() - 1 && getCurrentStatement().isDone(task));
    }

    @Override
    public void execute(Task task) {
        while (currentStmtIdx < statements.size() && getCurrentStatement().isDone(task) )
            currentStmtIdx++;
        if (currentStmtIdx < statements.size())
            getCurrentStatement().execute(task);

    }

    @Override
    public void isValid(BreakChecker breakChecker) {
        statements.forEach(s -> s.isValid(breakChecker));
    }

    private Statement getCurrentStatement() {
        return this.statements.get(currentStmtIdx);
    }
}
