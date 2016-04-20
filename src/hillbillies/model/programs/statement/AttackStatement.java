package hillbillies.model.programs.statement;

import hillbillies.model.Task;
import hillbillies.model.programs.expression.UnitExpression;

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

    @Override
    public void isValid(BreakChecker breakChecker) {
        // NOP
    }
}
