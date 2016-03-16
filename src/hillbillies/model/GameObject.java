package hillbillies.model;



public abstract class GameObject {

    protected Vector position;



    public Vector getPosition() {
        return this.position;
    }


    public void setPosition(double x, double y, double z) {
        position = new Vector(x,y,z);
    }



}
