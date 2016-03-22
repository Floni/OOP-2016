package hillbillies.model;


import hillbillies.model.Vector.IntVector;
import hillbillies.model.Vector.Vector;

public abstract class GameObject {

    protected Vector position;
    protected final int weight;
    protected final World world;

    // TODO: precond in world??
    public GameObject(World world, IntVector location) {
        position = location.toVector().add(World.Lc/2);
        this.weight = (int)Math.round(Math.random()*41 + 10);
        this.world = world;
    }


    public Vector getPosition() {
        return this.position;
    }

    public void destruct() {

    }

    public void advanceTime(double dt) {
        // TODO: set cube in world
        IntVector cubePos = getPosition().toIntVector();
        if (!(cubePos.getZ() == 1) && !World.isSolid(world.getCubeType(cubePos.add(0, 0, -1)))) { //TODO: fix floating rock
            setPosition(getPosition().add(0, 0, -3 * dt)); //TODO speed constant and stuff
            //
        }
    }

    public void setPosition(Vector pos) {
        this.position = pos;
    }

    public int getWeight() {
        return this.weight;
    }
}
