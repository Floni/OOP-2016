package hillbillies.model;


import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;
import jdk.nashorn.internal.ir.annotations.Immutable;

public abstract class GameObject {
    private static final double FALL_SPEED = -3.0;

    protected Vector position;
    protected final int weight;
    protected final World world;
    protected boolean falling;


    public GameObject(World world, IntVector location) {
        position = location.toVector().add(World.Lc/2);
        this.weight = (int)Math.round(Math.random()*41 + 10);
        this.world = world;
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
                || (getPosition().getZ() - Math.floor(getPosition().getZ())) > 0.5) {
            setPosition(getPosition().add(0, 0, FALL_SPEED * dt));
            falling = true;
            world.removeCubeObject(this);
        } else {
            if (falling)
                world.addCubeObject(this);
            falling = false;
        }
    }

    public void setPosition(Vector pos) throws IllegalArgumentException {
        if (!world.isValidPosition(pos.toIntVector()) || World.isSolid(world.getCubeType(pos.toIntVector())))
            throw new IllegalArgumentException("invalid position");
        this.position = pos;
    }

    @Immutable
    public int getWeight() {
        return this.weight;
    }
}
