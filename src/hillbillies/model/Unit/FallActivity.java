package hillbillies.model.Unit;

import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import hillbillies.model.World;

/**
 * Created by timo on 3/17/16.
 *
 */
class FallActivity extends MoveActivity {

    public FallActivity(Unit unit) {
        super(unit);

        speed = new Vector(0, 0, -3.0);
        target = unit.getPosition(); // we use target as the starting position
    }

    @Override
    void advanceTime(double dt) {
        Vector newPosition = unit.getPosition().add(this.speed.multiply(dt));

        IntVector newCube = newPosition.toIntVector();
        if (newCube.getZ() == 0 || World.isSolid(unit.world.getCubeType(newCube.add(0, 0, -1)))) {
            int diffZ = target.toIntVector().substract(newCube).getZ();
            unit.deduceHitPoints(10*diffZ);
            unit.finishCurrentActivity();
        }
        unit.setPosition(newPosition);
    }

    @Override
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return false;
    }
}
