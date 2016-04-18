package hillbillies.model.programs.statement;

import hillbillies.model.Task;

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
        return (this.currentStmtIdx == this.statements.size() - 1) && getCurrentStatement().isDone(task);
    }

    @Override
    public void execute(Task task) {
        if (getCurrentStatement().isDone(task))
            currentStmtIdx++;

        getCurrentStatement().execute(task);

    }

    private Statement getCurrentStatement() {
        return this.statements.get(currentStmtIdx);
    }
}
