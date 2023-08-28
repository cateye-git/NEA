package Simulate;

import java.util.ArrayList;
import java.util.Random;

public class Simulator {        //will not let me set it to static???
    // this is the class which will:

    // take in which system to add
    // store system and interlopers

    private static ArrayList<Body> originalBodies; //this is to be set when the system is selected and at no other times.

    private static int simulationID = 0;

    private static ArrayList<Body> bodies;
    private static int sysID;
    private static Body interloper;
    private static boolean isInterloper = true;

    private static int getNewSimID(){
        simulationID++;
        return simulationID;
    }

    public static void startUp(int id){
        sysID = id;
        originalBodies = getSystemData(sysID);
        bodies = getSystemData(sysID);
    }

    public static void setInterloper(Body inte) {
        // this is called by the GUI when an interloper is selected
        interloper = inte;
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
        RK4(dt);
        checkCollisions();
    }


    private static ArrayList<Body> getSystemData(int systemID){

        //select all bodies from the required system, name is done in separate subroutine

        //test data
        ArrayList<Body> bodies = new ArrayList<>();

        Body earth = new Body(0,0,6372000,0,0,0,"earth",6e24,6371000, true);
        Body earth2 = new Body(0,0,-6372000,0,0,0,"htrae",6e24,6371000, true);
        Body earth3 = new Body(6e99,0,0,1e3,0,0,"3",6e21,637100, true);
    //    Body earth4 = new Body(-63712000,0,0,-1e3,0,0,"4",6e24,6371000, true);
        earth.setSimulationID(0);
        earth2.setSimulationID(1);
        earth3.setSimulationID(2);
    //    earth4.setSimulationID(3);

        bodies.add(earth);
        bodies.add(earth2);
        bodies.add(earth3);
      //  bodies.add(earth4);


        /*
        Body earth = new Body(0,0,100,1e3,0,0,"earth",20,200, true);
        Body earth2 = new Body(0,0,-100,1e3,0,0,"eart2h",20,200, true);
        earth.setSimulationID(getNewSimID());
        earth2.setSimulationID(getNewSimID());

        bodies.add(earth);
        bodies.add(earth2);

         */

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

    private static double G = 6.674e-11;// gravitational constant needed to calculate accurate positions

    private static Vector3D[] getAccelerationOfTwoBodies(Vector3D b1Pos, Vector3D b2Pos, double b1Mass, double b2Mass){
        double dist = Vector3D.getDistance(b1Pos, b2Pos);
        // use this to find the magnitude of the force on each body from F = Gm1m2/r^2, which will be the same for both
        double massProduct = b1Mass * b2Mass;
        double force = (dist/Math.pow(dist, 3)) * massProduct;
        force = force * G;

        // we then need to find the position vector between body A and body B
        double[] body1PosArray = b1Pos.getAllComponents();
        double[] body2PosArray = b2Pos.getAllComponents();
        Vector3D posVectorB1to2 = new Vector3D(body2PosArray[0]-body1PosArray[0],body2PosArray[1]-body1PosArray[1],body2PosArray[2]-body1PosArray[2]); // get position vector from body 1 to body 2
        Vector3D unitPosVector1to2 = Vector3D.getUnitVector(posVectorB1to2); // make a unit vector for this.
        Vector3D unitPosVector2to1 = Vector3D.multiply(unitPosVector1to2, -1); // get the opposite for the other force.

        double accelerationMag1 = force / b1Mass; //    get acceleration on body 1 from force
        Vector3D acceleration1 = Vector3D.multiply(unitPosVector1to2, accelerationMag1); // use the unit vector and the calculated acceleration mag to find acceleration

        double accelerationMag2 = force / b2Mass; //     get acceleration on body 2 from force
        Vector3D acceleration2 = Vector3D.multiply(unitPosVector2to1, accelerationMag2);

        //  System.out.println("a1 = "+acceleration1 + " a2 = "+acceleration2);

        Vector3D[] accs = {acceleration1, acceleration2};
        return accs;
    }// gets the acceleration on two bodies based on their position and masses

    private static Vector3D[] getAllAccelerations(Vector3D[] positions, Double[] masses){
        // the addAccelerationOnOf subroutine takes in two bodies, body 1 and 2, and adds the acceleration
        // of two input bodies due to the force on each other.
        // Therefore, if I input bodies 1 and 2, I do not need to input bodies 2 and 1 as this would be doubling up
        // the acceleration.

        // if I had 4 bodies, 1 2 3 4, then I would need to end up with:
        // on 1, effects of 2,3,4
        // on 2, effects of 1,3,4
        // on 3, effects of 1,2,4
        // on 4, effects of 1,2,3
        // to get each pair of accelerations once,
        // these commands would be needed:
        //1 on 2
        //1 on 3
        //1 on 4

        //2 on 3
        //2 on 4

        //3 on 4

        // the pattern here is that planet needs to be done in ascending order
        // the subroutine should then be called with that body and then all the bodies above it

        //before all of this, a new acceleration array must be made to put the accelerations in
        Vector3D[] accelerations = new Vector3D[positions.length];

        int currentBody = 0;//  start at the first body
        for(Vector3D position: positions){ // for each body
            for(int iterator = currentBody+1;iterator < positions.length;iterator++){// from the next body to the end:
                // get the accelerations of those two bodies
                Vector3D[] currentTwoAccelerations = getAccelerationOfTwoBodies(position, positions[iterator], masses[currentBody],masses[iterator]);
                // and add them to the accelerations already calculated
                accelerations[currentBody] = Vector3D.add(accelerations[currentBody], currentTwoAccelerations[0]);
                accelerations[iterator] = Vector3D.add(accelerations[iterator], currentTwoAccelerations[1]);
            }
            //have now done all accelerations for that body
            currentBody++;
        }

        if(accelerations.length == 1){
            accelerations[0] = new Vector3D(0,0,0);
        }

        return accelerations;
    }

    private static void RK4(double dt){
        int numBodies = bodies.size();

        // it is calculated as so:
        // for each planet:
        // ks1 = acceleration
        // kv1 = velocity

        // ks2 = acceleration from (position + kv1 * dt/2)
        // kv2 = velocity + ks2 * dt/2

        // ks3 = acceleration from (position + kv2*dt/2)
        // kv3 = velocity + ks2*dt/2

        // ks4 = acceleration from (position + ks3 * dt)
        // kv4 = velocity + ks3*dt

        // s += dt/6 * (kv1 + 2kv2 + 2kv3 + kv4)
        // v = dt/6 * (ks1 + 2ks2 + 2ks3 + ks4)

        // so first we need to set the coefficients:
        Vector3D[][] kv = new Vector3D[4][bodies.size()]; //velocity coefficients
        Vector3D[][] ks = new Vector3D[4][bodies.size()]; //displacement coefficients:

        //first coefficients:
        // ks1: (and kv1)
        // get positions and masses
        Vector3D[] positions = new Vector3D[numBodies];
        Double[] masses = new Double[numBodies];
        for(int counter = 0; counter < numBodies;counter++){
            Body currentBody = bodies.get(counter);
            positions[counter] = currentBody.getPosition();// record relevant position and mass
            masses[counter] = currentBody.getMass();
            kv[0][counter] = currentBody.getVelocity(); // set first kv to velocity
        }
        ks[0] = getAllAccelerations(positions,masses); // pass those positions and masses to calc acceleration

        ks[2] = getAllAccelerations(Vector3DArrayOperations.addVectors(positions,Vector3DArrayOperations.multiplyVectors(kv[1],dt/2)),masses); // acc of(position + kv1 * dt/2)
        kv[2] = Vector3DArrayOperations.addVectors(kv[0],Vector3DArrayOperations.multiplyVectors(ks[1],dt/2)); // velocity + ks1 * dt/2

        ks[3] = getAllAccelerations(Vector3DArrayOperations.addVectors(positions,Vector3DArrayOperations.multiplyVectors(kv[2],dt)),masses); // acc of(position + kv2 * dt)
        kv[3] = Vector3DArrayOperations.addVectors(kv[0],Vector3DArrayOperations.multiplyVectors(ks[2],dt)); // velocity + ks2 * dt

        // use kv and ks to predict new position and velocity

        int counter = 0;
        Vector3D[] sumOfDisplacementCoefficients = new Vector3D[numBodies];
        Vector3D[] sumOfVelocityCoefficients = new Vector3D[numBodies];

        // apply correct multipliers:
        ks[1] = Vector3DArrayOperations.multiplyVectors(ks[1], 2);
        ks[2] = Vector3DArrayOperations.multiplyVectors(ks[2], 2);

        kv[1] = Vector3DArrayOperations.multiplyVectors(kv[1], 2);
        kv[2] = Vector3DArrayOperations.multiplyVectors(kv[2], 2);
        for(Body body : bodies){
            //get dt/6 * ks1+2ks2+2ks3+ks4
            sumOfDisplacementCoefficients[counter]= Vector3D.add(Vector3D.add(Vector3D.add(ks[0][counter], ks[1][counter]),ks[2][counter]),ks[3][counter]);
            sumOfDisplacementCoefficients[counter] = Vector3D.multiply(sumOfDisplacementCoefficients[counter], dt/6);

            //get dt/6 * kv1+2kv2+2kv3+kv4
            sumOfVelocityCoefficients[counter]= Vector3D.add(Vector3D.add(Vector3D.add(kv[0][counter], kv[1][counter]),kv[2][counter]),kv[3][counter]);
            sumOfVelocityCoefficients[counter] = Vector3D.multiply(sumOfVelocityCoefficients[counter], dt/6);

            // use these to set the new position and velocity of the bodies with
            // s += dt/6 * (kv1 + 2kv2 + 2kv3 + kv4)
            // v += dt/6 * (ks1 + 2ks2 + 2ks3 + ks4)

            //System.out.println(sumOfVelocityCoefficients[0]);
            body.setVelocity(Vector3D.add(body.getVelocity(),sumOfDisplacementCoefficients[counter]));
            body.setPosition(Vector3D.add(body.getPosition(),sumOfVelocityCoefficients[counter]));
            counter++;

            //  System.out.println(body.getPosition());
        }
    }

    public static void checkCollisions(){                                           //!!! TURN TO PRIVATE
        // given that we can treat all bodies as spheres, the easiest way to check for any collisions is to look at
        // whether the distance between any two planets is less than the sum of their radii.
        // if so, a collision has occured

        // to do this, we need to loop through each set of two planets once, as with finding all accelerations.
        // we then need to take the radii of those planets and the distance, and check that
        // they aren't colliding

     //   int currentBody = 0;//  start at the first body
        for(int currentBody = 0;currentBody < bodies.size();currentBody++){ // for each body
            Body body = bodies.get(currentBody);
            for(int iterator = currentBody+1;iterator < bodies.size();iterator++){// from the next body to the end:
                Body otherBody = bodies.get(iterator);
                //  System.out.println("testing collision between "+body.getName() + " and "+otherBody.getName());
                // get sum of radii
                double sumOfRadii = body.getRadius() + otherBody.getRadius();
                // get distance between them
                double dist = Vector3D.getDistance(body.getPosition(), otherBody.getPosition());

                if(dist <= sumOfRadii){
                    //then a collision has occured
                    // the result of the collision will depend on velocity and mass of the planet
                    System.out.println("collision between "+body.getName()+" and "+otherBody.getName());

                    //Rnew = cube root(R1^3 + R2^3)
                    double newRadius = Math.pow((Math.pow(body.getRadius(),3) + Math.pow(otherBody.getRadius(),3)),0.333d);
                    System.out.println("r1 = "+body.getRadius()+" r2 = "+otherBody.getRadius()+" so rNew = "+newRadius);
                    //Mnew = m1 + m2
                    double newMass = body.getMass() + otherBody.getMass();
                    System.out.println("m1 = "+body.getMass()+" m2 = "+otherBody.getMass()+" so mNew = "+newMass);

                    //get momentum of old bodies
                    Vector3D momentumCurrent = Vector3D.multiply(body.getVelocity(),body.getMass());
                    Vector3D momentumOther = Vector3D.multiply(otherBody.getVelocity(),otherBody.getMass());

                    //use this to find momentum of new body
                    Vector3D MVnew = Vector3D.add(momentumCurrent,momentumOther);
                    //and so velocity vector
                    Vector3D Vnew = Vector3D.multiply(MVnew, 1/newMass);

                    // the new position should be at the centre of mass between the two objects
                    // this is done by doing (Vfrom 1 to another) * (m1/(m1+m2))
                    Vector3D posBtoA = Vector3D.add(body.getPosition(), Vector3D.multiply(otherBody.getPosition(),-1));// vector from otherBody to body
                //    System.out.println("pos from1 to other is" + posBtoA);
                    double ratio = body.getMass() / (otherBody.getMass() + body.getMass());
                 //   System.out.println("ratio is "+ratio);
                    posBtoA = Vector3D.multiply(posBtoA, ratio);// multiply by ratio
                    Vector3D newPos = Vector3D.add(otherBody.getPosition(), posBtoA); // adding to position of otherBody to give the centre of mass
                  //  System.out.println("old pos1: "+body.getPosition()+" old pos2: "+otherBody.getPosition()+" new pos: "+newPos);


                    Body newBody = new Body(newPos, Vnew, "collision between "+body.getName()+" and "+otherBody.getName(), newMass, newRadius, true);
                    newBody.setSimulationID(getNewSimID());

                    System.out.println(newRadius + " " + newBody);
                    bodies.remove(iterator);
                    bodies.remove(currentBody);
                    bodies.add(newBody);
                }

            }
            currentBody++;
            //have now done all collisions for that body
        }


    }
}
