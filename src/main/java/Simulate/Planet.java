package Simulate;

public class Planet extends Body{

    public double getHabitability() {
        return habitability;
    }

    public void setHabitability(double habitability) {
        this.habitability = habitability;
    }

    private double habitability;
    //not defined in constructor as is not known
    public Planet(double posX, double posY, double posZ, double velX, double velY, double velZ, String name, double mass, double radius, boolean significant) {
        super(posX, posY, posZ, velX, velY, velZ, name, mass, radius, significant);
    }

    public Planet(Vector3D position, Vector3D velocity, String name, double mass, double radius, boolean significant) {
        super(position, velocity, name, mass, radius, significant);
    }

    public String convertToCSVEntry(){
        String ref = super.convertToCSVEntry() + "," + habitability;
        return ref;
    }

    //if it collides with a sun, we remove its habitability because it just wont be livable
}
