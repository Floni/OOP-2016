package hillbillies.part3.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.unit.Unit;
import hillbillies.part3.programs.expression.UnitExpression;

/**
 * Created by timo on 4/14/16.
 */
public class AttackStatement implements Statement {

    private final UnitExpression unitExpr;

    public AttackStatement(UnitExpression target) {
        this.unitExpr = target;
    }

    @Override
    public void reset() {
        // NOP
    }

    @Override
    public boolean isDone(Task task) {
        return !task.getAssignedUnit().isAttacking();
    }

    @Override
    public void execute(Task task) {
        task.getAssignedUnit().attack(this.unitExpr.getValue(task));
        task.await();
    }
}
