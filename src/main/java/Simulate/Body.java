package Simulate;

public class Body implements Cloneable {
    public int getSimulationID() {
        return simulationID;
    }

    public void setSimulationID(int simulationID) {
        this.simulationID = simulationID;
    }

    //getters for position and velocity
    private int simulationID;
    public Vector3D getPosition() {
        return position;
    }
    public Vector3D getVelocity() {
        return velocity;
    }
    private Vector3D position;
    private Vector3D velocity;
    //getters and setters for name and mass
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getMass() {
        return mass;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }

    // setter for vectors in component form
    public void setPosition(double posX, double posY, double posZ) {
        position = new Vector3D(posX, posY, posZ, "Position");
    }
    public void setVelocity(double velX, double velY, double velZ) {
        velocity = new Vector3D(velX, velY, velZ, "Velocity");
    }
    //setter for vectors in vector form
    public void setPosition(Vector3D position) {
        this.position = position;
    }
    public void setVelocity(Vector3D velocity){
        this.velocity = velocity;
    }

    private String name= "Unnamed";
    private double mass;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    private double radius;

    public boolean isSignificant() {
        return significant;
    }

    public void setSignificant(boolean significant) {
        this.significant = significant;
    }

    private boolean significant; // whether body should be outputted in CSV file

    // initialising
    public Body(double posX, double posY, double posZ, double velX, double velY, double velZ, String name, double mass, double radius, boolean significant){
        position = new Vector3D(posX, posY, posZ, "Position");
        velocity = new Vector3D(velX, velY, velZ, "Velocity");
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.significant = significant;
    }
    public Body(Vector3D position, Vector3D velocity, String name, double mass, double radius, boolean significant){
        this.position = position;
        this.velocity = velocity;
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.significant = significant;
    }

    @Override
    public String toString(){
        String ref = "body "+name+", mass "+mass+" and "+position+" and "+ velocity;
        return ref;
    }   //  return the name, mass and vectors when the item is printed

    public String convertToCSVEntry(){
        String ref = name+","+position.convertToCSVEntry()+","+velocity.convertToCSVEntry()+","+radius+","+mass;
        return ref;
    }

    public Body returnCopy(){
        Body returnBody = new Body(position.returnCopy(),velocity.returnCopy(),String.valueOf(name),mass, radius, significant);
        return returnBody;
    }

    public Object clone(){
        try{
            return super.clone();
        }catch(Exception e){
            return null;
        }
    }
}
