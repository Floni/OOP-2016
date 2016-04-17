package hillbillies.part3.programs.statement;

import hillbillies.model.Task;
import hillbillies.part3.programs.expression.PositionExpression;

/**
 * Created by timo on 4/14/16.
 */
public class WorkStatement implements Statement {

    private final PositionExpression position;

    public WorkStatement(PositionExpression pos) {
        this.position = pos;
    }

    @Override
    public void reset() {
        // NOP
    }

    @Override
    public boolean isDone(Task task) {
        //TODO: fix
        return !task.getAssignedUnit().isWorking();
    }

    @Override
    public void execute(Task task) {
        task.getAssignedUnit().workAt(this.position.getValue(task));
        task.await();
    }
}
