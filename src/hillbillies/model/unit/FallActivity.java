package hillbillies.model.unit;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.Terrain;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;
import hillbillies.model.World;

/**
 * The activity for falling, is a subclass of MoveActivity.
 */
class FallActivity extends Activity {
    static final Vector FALL_SPEED = new Vector(0, 0, -3.0);

    private Vector startPosition;

    /**
     * Initializes the fall activity.
     *
     * @param   unit
     *          The unit who is falling.
     *
     * @effect  Initialize the Activity with the given unit
     *          | super(unit)
     */
    FallActivity(Unit unit) throws IllegalArgumentException {
        super(unit);
    }

    /**
     * Start the falling.
     *
     * @effect  The start position will be set.
     *          | this.setStartPosition(this.getUnit().getPosition())
     */
    void startFalling() {
        this.setStartPosition(this.getUnit().getPosition()); // we use target as the starting position
        this.getUnit().setSpeed(FALL_SPEED);
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
    void advanceTime(double dt) {
        Vector newPosition = getUnit().getPosition().add(FALL_SPEED.multiply(dt));

        IntVector newCube = newPosition.toIntVector();
        //if (newCube.getZ() == 0 || World.isSolid(unit.getWorld().getCubeType(newCube.add(0, 0, -1)))) {
        if ((newCube.getZ() == 0 || Terrain.isSolid(getUnit().getWorld().getTerrain().getCubeType(newCube.add(0, 0, -1))))
                && (newPosition.getZ() - Math.floor(newPosition.getZ())) <= Terrain.Lc/2.0) {

            getUnit().setPosition(new Vector(getUnit().getPosition().getX(),
                    getUnit().getPosition().getY(), Math.floor(getUnit().getPosition().getZ()) + Terrain.Lc / 2.0));

            int diffZ = this.getStartPosition().toIntVector().subtract(newCube).getZ();
            getUnit().deduceHitPoints(10*diffZ);

            this.finishActivity();
        } else {
            getUnit().setPosition(newPosition);
        }
    }

    /**
     * Returns whether the unit can switch activities.
     *
     * @return  Always false.
     *          | !result
     */
    @Override @Basic
    boolean canSwitch() {
        return false;
    }

    @Override
    void pause() {
        // NOP
    }

    @Override
    void resume() {
        // NOP
    }

    /**
     * Resets the fall activity.
     *
     * @effect  The start position will be cleared.
     *          | this.setStartPosition(null)
     */
    @Override
    void reset() {
        this.setStartPosition(null);
    }

    /**
     * Sets the start Position
     *
     * @param   startPosition
     *          The new start position.
     *
     * @post    The start position will be set.
     *          | new.getStartPosition() == startPosition
     */
    @Model
    private void setStartPosition(Vector startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * Returns the start position, where the unit started falling.
     */
    @Basic
    private Vector getStartPosition() {
        return this.startPosition;
    }
}
