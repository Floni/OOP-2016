package hillbillies.model.Unit;

import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;

/**
 * Created by timo on 3/17/16.
 */
class WorkActivity extends Activity {

    private double workTimer = 0;
    private IntVector location;

    WorkActivity(Unit unit, IntVector location) {
        super(unit);
        this.workTimer  = 500.0 / unit.getStrength();
        this.location = location;
        Vector diff = location.toVector().add(World.Lc/2).subtract(unit.getPosition());
        unit.setOrientation(Math.atan2(diff.getY(), diff.getX()));
    }


    @Override
    void advanceTime(double dt) {
        workTimer -= dt;
        if (workTimer <= 0) {
            workTimer = 0;
            finishWork();
            unit.finishCurrentActivity();
        }
    }

    private void finishWork() {
        if (unit.isCarryingLog() || unit.isCarryingBoulder()){ //BOULDER OR LOG
            if (!World.isSolid(unit.world.getCubeType(location))) {
                unit.dropCarry(location);
                unit.addXp(10);
            }
        } else if (unit.world.getCubeType(location) == World.WORKSHOP && unit.world.getLogs(location).size() >= 1 &&
                unit.world.getBoulders(location).size() >= 1) {
            unit.world.consumeBoulder(location);
            unit.world.consumeLog(location);
            unit.setWeight(unit.getWeight() + 1);
            unit.setToughness(unit.getToughness() + 1);
            unit.addXp(10);
        } else if (unit.world.getBoulders(location).size() >= 1) {
            unit.pickUpBoulder(unit.world.getBoulders(location).iterator().next());
            unit.addXp(10);
        } else if (unit.world.getLogs(location).size() >= 1) {
            unit.pickUpLog(unit.world.getLogs(location).iterator().next());
            unit.addXp(10);
        } else if (unit.world.getCubeType(location) == World.TREE) {
            unit.world.breakCube(location);
            unit.addXp(10);
        } else if (unit.world.getCubeType(location) == World.ROCK) {
            unit.world.breakCube(location);
            unit.addXp(10);
        }
    }

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return true;
    }

    @Override
    public void resume() {
        unit.finishCurrentActivity();
    }
}