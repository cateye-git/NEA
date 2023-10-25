package Simulate;

public class Star extends Body{
    public double getIllumination() {
        return illumination;
    }

    public void setIllumination(double illumination) {
        this.illumination = illumination;
    }

    private double illumination;

    public Star(double posX, double posY, double posZ, double velX, double velY, double velZ, String name, double mass, double radius, boolean significant, double illumination) {
        super(posX, posY, posZ, velX, velY, velZ, name, mass, radius, significant);
        this.illumination = illumination;
    }

    public Star(Vector3D position, Vector3D velocity, String name, double mass, double radius, boolean significant, double illumination) {
        super(position, velocity, name, mass, radius, significant);
        this.illumination = illumination;
    }
    public String convertToCSVEntry(){
        String ref = super.convertToCSVEntry() + "," + illumination;
        return ref;
    }
}
