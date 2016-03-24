package hillbillies.model;


import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;

public abstract class GameObject {
    private static final double FALL_SPEED = -3.0;

    private Vector position;
    private final int weight;
    private final World world;
    private boolean falling;


    protected GameObject(World world, IntVector location) {
        this.world = world;
        setPosition(location.toVector().add(World.Lc/2));
        this.weight = (int)Math.floor(Math.random()*41 + 10);
        this.falling = false;
    }


    public Vector getPosition() {
        return this.position;
    }

    void terminate() {

    }

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

    public void setPosition(Vector pos) throws IllegalArgumentException {
        if (!world.isValidPosition(pos.toIntVector()) || World.isSolid(world.getCubeType(pos.toIntVector())))
            throw new IllegalArgumentException("invalid position");
        this.position = pos;
    }

    public int getWeight() {
        return this.weight;
    }
}
