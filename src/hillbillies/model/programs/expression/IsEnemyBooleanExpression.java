package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for is_enemy expression
 */
public class IsEnemyBooleanExpression implements BooleanExpression {

    private final UnitExpression enemy;

    public IsEnemyBooleanExpression(UnitExpression enemy) {
        this.enemy = enemy;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return task.getAssignedUnit().getFaction() != enemy.getValue(task).getFaction();
    }
}
