package hillbillies.part3.programs.expression;

import hillbillies.model.Faction;
import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.unit.Unit;

/**
 * Created by florian on 14/04/2016.
 */
public class EnemyUnitExpression implements UnitExpression {
    @Override
    public Unit getValue(Task task) {
        Faction fac = task.getAssignedUnit().getFaction();
        World world = task.getAssignedUnit().getWorld();
        //TODO: wat als er geen units in andere factions zitten?
        return world.getUnits().stream().filter(u -> u.getFaction() != fac).findAny().orElse(task.getAssignedUnit());
    }
}
