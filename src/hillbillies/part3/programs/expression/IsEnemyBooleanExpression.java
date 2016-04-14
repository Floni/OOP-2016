package hillbillies.part3.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.unit.Unit;

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
