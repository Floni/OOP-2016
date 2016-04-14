package hillbillies.part3.programs.expression;

import hillbillies.model.Faction;
import hillbillies.model.Task;
import hillbillies.model.unit.Unit;

/**
 * Created by florian on 14/04/2016.
 */
public class FriendUnitExpression implements UnitExpression {


    @Override
    public Unit getValue(Task task) {
        Faction fac = task.getAssignedUnit().getFaction();
        //TODO: wat als er geen andere units in faction zijn?
        return fac.getUnits().stream().filter( u -> u != task.getAssignedUnit()).findAny().orElse(task.getAssignedUnit());
    }
}
