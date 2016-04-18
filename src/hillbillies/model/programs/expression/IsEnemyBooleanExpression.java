package hillbillies.model.programs.expression;

import hillbillies.model.Task;

/**
 * Created by florian on 14/04/2016.
 */
public class IsEnemyBooleanExpression implements BooleanExpression {

    private final UnitExpression enemy;


    public IsEnemyBooleanExpression(UnitExpression enemy) {
        this.enemy = enemy;
    }

    @Override
    public Boolean getValue(Task task) {
        return task.getAssignedUnit().getFaction() != enemy.getValue(task).getFaction();
    }
}
