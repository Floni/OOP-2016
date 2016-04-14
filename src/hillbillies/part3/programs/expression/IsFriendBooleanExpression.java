package hillbillies.part3.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.unit.Unit;

/**
 * Created by florian on 14/04/2016.
 */
public class IsFriendBooleanExpression implements BooleanExpression {

    private final UnitExpression friend;


    public IsFriendBooleanExpression(UnitExpression friend) {
        this.friend = friend;
    }

    @Override
    public Boolean getValue(Task task) {
        return task.getAssignedUnit().getFaction() == friend.getValue(task).getFaction();
    }
}
