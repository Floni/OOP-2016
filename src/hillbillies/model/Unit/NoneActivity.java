package hillbillies.model.Unit;

import hillbillies.model.Vector.IntVector;
import hillbillies.model.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            int random = (int)Math.floor(Math.random()*4);
            switch (random) {
                case 3: // move
                        IntVector randPos;
                        do {
                            randPos = new IntVector(Math.random()*unit.world.X_MAX,
                                    Math.random()*unit.world.Y_MAX,
                                    Math.random()*unit.world.Z_MAX);
                            if (unit.isValidPosition(randPos)) {
                                try {
                                    unit.moveTo(randPos);
                                    break;
                                } catch (IllegalArgumentException ignored) {}
                            }
                        } while (true);
                        break;
                case 0: // work
                    List<IntVector> neighbours = World.getNeighbours(unit.getPosition().toIntVector())
                            .filter(v -> unit.world.isValidPosition(v)).collect(Collectors.toList());
                    unit.workAt(neighbours.get((int)(Math.random() * neighbours.size())));
                    break;
                case 1: // rest
                    unit.rest();
                    break;
                case 2: //attack
                    Set<Unit> units = unit.world.getUnits();
                    for (Unit other : units) {
                        if (other.getFaction() != unit.getFaction()) {
                            if (unit.canAttack(other) && !other.getCurrentActivity().equalsClass(FallActivity.class)) {
                                unit.attack(other);
                                break;
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

    @Override
    public void resume() {

    }
}
