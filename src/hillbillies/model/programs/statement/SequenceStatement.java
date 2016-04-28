package hillbillies.model.programs.statement;

import hillbillies.model.Task;

import java.util.List;

/**
 * Class for a sequence of statements.
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
        return currentStmtIdx >= statements.size() || // we have executed all statements.
                (currentStmtIdx == statements.size() - 1
                        && getCurrentStatement().isDone(task)); // or we are at the last stmt and it is done
    }

    @Override
    public void execute(Task task) {
        while (currentStmtIdx < statements.size() && getCurrentStatement().isDone(task) )
            currentStmtIdx++;
        if (currentStmtIdx < statements.size())
            getCurrentStatement().execute(task);

    }

    @Override
    public BreakChecker checkValid(BreakChecker breakChecker) {
        statements.forEach(s -> s.checkValid(breakChecker));
        return breakChecker;
    }

    private Statement getCurrentStatement() {
        return this.statements.get(currentStmtIdx);
    }
}
