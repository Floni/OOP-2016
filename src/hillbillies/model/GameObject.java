package hillbillies.model;


import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.vector.IntVector;
import hillbillies.model.vector.Vector;

/**
 * Class for general GameObject (Boulder or Log).
 *
 * @invar   The world must be effective
 *          | this.world != null.
 * @invar   The position must be valid
 *          | this.world.isValidPosition(this.getPosition())
 * @invar   The weight must be between 10 and 50 inclusive.
 *          | this.getWeight() >= 10 && this.getWeight() <= 50
 *
 */
public abstract class GameObject {
    private static final double FALL_SPEED = -3.0;

    private Vector position;
    private final int weight;
    private final World world;
    private boolean falling;

    /**
     * Creates a new gameObject in a given world an at a given location.
     *
     * @param   world
     *          The world in which the gameobject needs to be created.
     * @param   location
     *          The location at which the gameobject needs to be created.
     *
     * @post    The position is set to the middle of the cube of the given position.
     * @post    The weight of the object is random between 10 and 50.
     *
     * @effect  The position is set
     *          | new.setPosition(location.toVector().add(Terrain.Lc/2))
     *
     */
    protected GameObject(World world, IntVector location) throws InvalidPositionException {
        this.world = world;
        setPosition(location.toVector().add(Terrain.Lc/2));
        this.weight = (int)Math.floor(Math.random()*41 + 10);
        this.falling = false;
    }

    /**
     * Returns the world this gameObject belongs to.
     */
    @Basic @Immutable
    private World getWorld() {
        return this.world;
    }

    /**
     * Returns the position of the gameObject.
     */
    @Basic
    public Vector getPosition() {
        return this.position;
    }

    /**
     * Updates the state of the game object.
     *
     * @param   dt
     *          The time step that is taken.
     *
     * @post    If the object is not above a solid cube or the world ground,
     *          then it falls at the falling speed.
     */
    public void advanceTime(double dt) {
        IntVector cubePos = getPosition().toIntVector();
        if (!(cubePos.getZ() == 0 || Terrain.isSolid(getWorld().getTerrain().getCubeType(cubePos.add(0, 0, -1))))
                || (getPosition().getZ() - Math.floor(getPosition().getZ())) > Terrain.Lc / 2) {
            setPosition(getPosition().add(0, 0, FALL_SPEED * dt));
            falling = true;
            getWorld().getTerrain().removeObjectFromCube(this); // remove from terrain but keep in world.
        } else {
            if (falling) {
                setPosition(new Vector(getPosition().getX(), getPosition().getY(), getPosition().toIntVector().getZ() + Terrain.Lc / 2));
                getWorld().getTerrain().addObjectToCube(this); // add to terrain.
            }
            falling = false;
        }
    }


    /**
     * Sets the position of the game object.
     *
     * @param   pos
     *          The position to which the object should be moved.
     *
     * @post    The new position of the object will be the given position.
     *
     * @throws  InvalidPositionException
     *          The position is not valid or the position is a solid cube.
     */
    public void setPosition(Vector pos) throws InvalidPositionException {
        if (!getWorld().getTerrain().isValidPosition(pos.toIntVector())
                || Terrain.isSolid(getWorld().getTerrain().getCubeType(pos.toIntVector())))
            throw new InvalidPositionException(pos);
        this.position = pos;
    }

    /**
     * Returns the weight of the game object.
     */
    @Immutable @Basic
    public int getWeight() {
        return this.weight;
    }
}
