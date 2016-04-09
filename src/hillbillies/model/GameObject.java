package hillbillies.model;


import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;

public abstract class GameObject {
    private static final double FALL_SPEED = -3.0;

    private Vector position;
    private final int weight;
    private final World world;
    private boolean falling;

    /**
     * Creates a new gameobject in a given world an at a given location.
     *
     * @param   world
     *          The world in which the gameobject needs to be created.
     * @param   location
     *          The location at which the gameobject needs to be created.
     *
     * @post    The position is set to the middle of the cube of the given position.
     * @post    The weight of the object is random between 10 and 50.
     *
     */
    protected GameObject(World world, IntVector location) {
        this.world = world;
        setPosition(location.toVector().add(World.Lc/2));
        this.weight = (int)Math.floor(Math.random()*41 + 10);
        this.falling = false;
    }

    /**
     * Returns the position of the gameobject.
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
        if (!(cubePos.getZ() == 0 || World.isSolid(world.getCubeType(cubePos.add(0, 0, -1))))
                || (getPosition().getZ() - Math.floor(getPosition().getZ())) > World.Lc/2) {
            setPosition(getPosition().add(0, 0, FALL_SPEED * dt));
            falling = true;
            world.removeCubeObject(this);
        } else {
            if (falling) {
                setPosition(new Vector(getPosition().getX(), getPosition().getY(), getPosition().toIntVector().getZ() + World.Lc/2));
                world.addCubeObject(this);
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
     * @throws  IllegalArgumentException
     *          The position is not valid or the position is a solid cube.
     */
    public void setPosition(Vector pos) throws IllegalArgumentException {
        if (!world.isValidPosition(pos.toIntVector()) || World.isSolid(world.getCubeType(pos.toIntVector())))
            throw new IllegalArgumentException("invalid position");
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
