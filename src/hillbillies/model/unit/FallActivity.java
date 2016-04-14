package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;
import hillbillies.model.World;

/**
 * The activity for falling, is a subclass of MoveActivity.
 */
class FallActivity extends MoveActivity {

    /**
     * Initializes the fall activity.
     *
     * @param   unit
     *          The unit who is falling.
     *
     * @post    The units speed will be 3.
     *          | (new unit).getSpeedScalar() == 3
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit);
     *
     */
    FallActivity(Unit unit) {
        super(unit);

        speed = new Vector(0, 0, -3.0);
        target = unit.getPosition(); // we use target as the starting position
    }

    /**
     * Updates the falling activity for a given time step.
     *
     * @param   dt
     *          The time step to update with.
     *
     * @post    Moves the unit down with the falling speed, if the new position is above a solid cube or the world ground,
     *          the unit will take damage equal to 10 times the amount off cubes he has traveled.
     *          Else the unit will keep falling and his position will be set to the new position.
     *
     * @effect  Sets the position of the unit to the new position.
     *          | unit.setPosition(newPosition)
     */
    @Override
    void advanceTime(double dt) throws IllegalArgumentException {
        Vector newPosition = unit.getPosition().add(this.speed.multiply(dt));

        IntVector newCube = newPosition.toIntVector();
        //if (newCube.getZ() == 0 || World.isSolid(unit.getWorld().getCubeType(newCube.add(0, 0, -1)))) {
        if ((newCube.getZ() == 0 || World.isSolid(unit.getWorld().getCubeType(newCube.add(0, 0, -1))))
                && (newPosition.getZ() - Math.floor(newPosition.getZ())) <= World.Lc/2.0) {

            int diffZ = target.toIntVector().substract(newCube).getZ();
            unit.setPosition(new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
                    Math.floor(unit.getPosition().getZ()) + World.Lc / 2.0));
            unit.deduceHitPoints(10*diffZ);
            unit.finishCurrentActivity();
        } else {
            unit.setPosition(newPosition);
        }
    }

    /**
     * Returns whether the unit can switch activities, which always returns false.
     */
    @Override @Basic
    boolean canSwitch(Class<? extends Activity> newActivity) {
        return false;
    }

    /**
     * Resumes the falling activity, which is impossible.
     *
     * @throws  IllegalStateException
     *          Always throws.
     */
    @Override
    void resume() throws IllegalStateException {
        throw new IllegalStateException("can't resume falling because can't interrupt falling");
    }
}
