package hillbillies.model;



public abstract class GameObject {

    protected Vector position;
    protected final int weight;

    // TODO: precond in world
    public GameObject(int x, int y, int z, int weight) {
        position = new Vector(x + World.Lc/2, y + World.Lc/2, z + World.Lc/2);
        this.weight = weight;
    }


    public Vector getPosition() {
        return this.position;
    }


    public void setPosition(double x, double y, double z) {
        position = new Vector(x,y,z);
    }

    public void finalize() {

    }

}
