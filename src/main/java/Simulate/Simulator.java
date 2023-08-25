package Simulate;

import java.util.ArrayList;
import java.util.Random;

public class Simulator {        //will not let me set it to static???
    // this is the class which will:

    // take in which system to add
    // store system and interlopers

    private static ArrayList<Body> originalBodies; //this is to be set when the system is selected and at no other times.

    private static ArrayList<Body> bodies;
    private static int sysID;
    private static Body interloper;
    private static boolean isInterloper = true;

    public static void startUp(int id){
        sysID = id;
        originalBodies = getSystemData(sysID);
        bodies = getSystemData(sysID);
    }

    public static void setInterloper(Body inter) {
        // this is called by the GUI when an interloper is selected
        interloper = inter;
    }
    public static void setRandomInterloper(){
        //when the user asks for no interloper
        interloper = getRandomInterloper(bodies);
    }
    public static void noInterloper(){
        isInterloper = false;
    }

    public static ArrayList<Body> getBodies(){
        return bodies;
    }

    public static void updateBodies(double dt){
        PlanetSystem.RK4(bodies, dt);
    }

    private static ArrayList<Body> getSystemData(int systemID){

        //select all bodies from the required system, name is done in separate subroutine

        //test data
        ArrayList<Body> bodies = new ArrayList<>();
        Body earth = new Body(0,0,63712000,0,0,0,"earth",6e24,6371000, true);

        bodies.add(earth);

        return bodies;
    }
    private static String getSystemName(){

        //get the name of the system with this ID from the database for storing to the CSV file

        return "testSystem";
    }


    private static Body getRandomInterloper(ArrayList<Body> bodies){
        //to be called when the user asks for a random interloper
        // position will be a random one between the furthest body and double that distance
        // mass will be, at first, the same as the averages of the bodies
        //the velocity will be 0 so that it will be affected by other bodies even if a lot bigger or smaller
        //the radius will be the correct radius for the volume, which will be calculated further down the subroutine

        //to get the correct position, we need a random position vector
        //to do this, we just generate a random Vector3D and then take its position vector
        //when we have the position vector, multiply it by between the distance to the furthest planet and double that
        Random random = new Random();
        Vector3D randomPosition = new Vector3D(random.nextInt(),random.nextInt(), random.nextInt(),"Random pos");
        Vector3D unitRandom = Vector3D.getUnitVector(randomPosition);

        //we now need to go through each body to get the average mass and the furthest position
        // the average mass is the sum of the masses divided by the num of bodies
        // the furthest position is the position with the largest magnitude

        double largestPos = 0;
        double sumOfMass = 0;
        int noBodies = 0;

        for(Body body : bodies){//loop through each body
            noBodies++;//       add 1 to the no of bodies
            sumOfMass += body.getMass();//  add the mass of the body to the sum of the masses

            double currentPos = Vector3D.getMagnitude(body.getPosition());//    get the current magnitude of the position
            if(currentPos > largestPos){// set it to be the largest if it is
                largestPos = currentPos;
            }
        }
        double interloperMass = sumOfMass / noBodies;// mass = average = (sum of masses)/(no bodies)

        //the position = the unit vector * (1 + a random no from 0 to 1)
        Vector3D interloperPosition = Vector3D.multiply(unitRandom, 1 + random.nextFloat());
        Vector3D interloperVelocity = new Vector3D(0,0,0);

        //to get the correct radius, we will need the density and the mass of the interloper
        //this will allow us to find the volume, which we can use to find the radius

        //the density of moon rock is about 2400 kg/m^3 on average, so this is what we will use
        double density = 2400;
        // density = mass/volume so therefore volume = mass/density
        double interloperVolume = interloperMass / density;
        // volume = (4/3)(pi)(r^3) therefore r = (v/(4pi/3))^1/3
        double interloperRadius = Math.pow(interloperVolume/4*3.14159265359/3,1/3);


        Body interloper = new Body(interloperPosition,interloperVelocity, "Interloper",interloperMass, interloperRadius, false);
        return interloper;
    }
}
