package hillbillies.model.Unit;

import hillbillies.model.Vector;
import hillbillies.model.World;

/**
 * Created by timo on 3/17/16.
 */
class FallActivity extends MoveActivity {

    public FallActivity(Unit unit) {
        super(unit);

        speed = new Vector(0, 0, -3.0);
        target = unit.getPositionVector(); // we use target as the starting position
    }

    @Override
    void advanceTime(double dt) {
        Vector newPosition = unit.getPositionVector().add(this.speed.multiply(dt));

        int[] newCube = World.getCubePosition(newPosition.toDoubleArray());
        if (newCube[2] == 0 || World.isSolid(unit.world.getCubeType(newCube[0], newCube[1], newCube[2]-1))) {
            int diffZ = (int)Math.floor(target.substract(newPosition).getZ());
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
