package hillbillies.model.Unit;

import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;

import java.util.Set;

/**
 * Created by timo on 3/17/16.
 *
 */
class NoneActivity extends Activity {
    public NoneActivity(Unit unit) {
        super(unit);
    }

    @Override
    void advanceTime(double dt) {
        if (!unit.lastActivity.equalsClass(NoneActivity.class)) {
            unit.finishCurrentActivity(); // we still have an interrupted activity
        } else if (unit.isDefaultEnabled()) {
            int random = (int)Math.floor(Math.random()*3);
            switch (random) {
//                    case 3: // move to random location
//                        int[] randLoc = new int[3];
//                        for (int i = 0; i < 3; i++) {
//                            randLoc[i] = (int)Math.floor(Math.random()*World.X_MAX);
//                        }
//                        moveTo(randLoc);
//                        break;
                case 0: // work
                    unit.work();
                    break;
                case 1: // rest
                    unit.rest();
                    break;
                case 2: //attack
                    Set<Unit> units = unit.world.getUnits();
                    for (Unit other : units) {
                        if (other.getFaction() != unit.getFaction()) {
                            if (unit.canAttack(other)) {
                                unit.attack(unit);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return unit.isAlive();
    }
}
