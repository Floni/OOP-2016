package hillbillies.model.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;

/**
 * Class for is_friend expression.
 */
public class IsFriendBooleanExpression implements BooleanExpression {

    private final UnitExpression friend;


    public IsFriendBooleanExpression(UnitExpression friend) {
        this.friend = friend;
    }

    @Override
    public Boolean getValue(Task task) throws TaskInterruptException, TaskErrorException {
        return task.getAssignedUnit().getFaction() == friend.getValue(task).getFaction();
    }
}
