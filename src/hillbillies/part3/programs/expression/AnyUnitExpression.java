package hillbillies.part3.programs.expression;

import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.unit.Unit;

/**
 * Created by florian on 14/04/2016.
 */
public class AnyUnitExpression implements UnitExpression {
    @Override
    public Unit getValue(Task task) {
        World world = task.getAssignedUnit().getWorld();
        //TODO: wat als deze unit de enige is?
        return world.getUnits().stream().filter(u -> u != task.getAssignedUnit()).findAny().orElse(task.getAssignedUnit());
    }
}
