package Simulate;

import Database.MariaDBConnector;
import com.example.nea.SimulatorControllerLoad;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Simulator {        //this should be static class, alas java has other plans and will not let me

    private static int noCollisionsNoInter = 0;
    private static int noCollisionsInter = 0;
    // this is the class which will:

    // take in which system to add
    // store system and interlopers
    private static Stage stage;

    private static SimulatorControllerLoad loader;
    private static double interloperSignificance = 0;

    private static double quittingTime = 0;

    public static double getQuitTime(){
        return quittingTime;
    }
    private static ArrayList<Body> lastSnapNoInterloper = new ArrayList<>();

    private static String stageOfRunning; //can be: "runningWithInterloper", "runWithoutInterloper", "neverInterloper"
    public static String getStageOfRunning() {
        return stageOfRunning;
    }

    private static ArrayList<Body> originalBodies; //this is to be set when the system is selected and at no other times.

    //private static int simulationID = 0;

    private static ArrayList<Body> bodies;
    public static Body interloperCopy;

   // public static void setBodies(ArrayList<Body> newBodies){//remove after testing
   //     bodies = newBodies;
   // }

    private static int sysID;

    private static final double G = 6.674e-11;// gravitational constant needed to calculate accurate positions

    public static double getInterloperSignificance() {
        return interloperSignificance;
    }

    public static Body getInterloper() {
        return interloper;
    }

    public static double getProposedNewMass() {
        return proposedNewMass;
    }

    public static void updateInterloperMass(){
        interloper.setMass(proposedNewMass);
    }

    private static Body interloper;
    private static boolean interloperIsSelected = false;
    private static boolean interloperInSimulation= false;

    private static FileOperations fileOps;

    private static double proposedNewMass = 0;

    public static void setFileOps(FileOperations fileOperations){
        fileOps = fileOperations;
    }

    public static void startUp(int id, boolean withInterloper, Stage prevStage){
        stage = prevStage;
        sysID = id;
        originalBodies = getSystemData();
        bodies = getSystemData();
        interloperInSimulation = withInterloper;
        fileOps.openOutputFileHandle(getSystemName()+interloperIsSelected);
    }

    public static void restart(){
        fileOps = new FileOperations();
        System.out.println("interloperInSimulation at restart 106 simulator = "+interloperIsSelected);



//          contents moved to beginFileOutput
    }

    public static void beginFileOutput(){
        fileOps.openOutputFileHandle(getSystemName()+interloperIsSelected); //this shouldn't be here because it's overwriting the current (useful) file
        fileOps.writeFirstLine(interloperIsSelected, sysID,getSystemName());
    }

    public static void setInterloper(Body inter) {
        stageOfRunning = "runWithoutInterloper";
        // this is called by the GUI when an interloper is selected
        interloper = inter;
        interloperCopy = interloper.returnCopy();
        interloperIsSelected = true;
        fileOps.writeFirstLine(false, sysID,getSystemName());    // why are these here?
     //   if(interloperInSimulation){
        Body interloperCopy = (Body) interloper.clone();
        bodies.add(interloperCopy);
     //   interloperIsSelected = true;
      //  }
    }
    public static void setRandomInterloper(){
        //when the user asks for random interloper
        interloper = getRandomInterloper(bodies);
        interloperCopy = interloper.returnCopy();
     //   interloperCopy.setName("interloper copy");
        interloperIsSelected = true;
        fileOps.writeFirstLine(false, sysID,getSystemName());    // why are these here?

        Body interloperCopy = (Body) interloper.clone();
        bodies.add(interloperCopy);
      //  interloperIsSelected = true;
    }
    public static void noInterloper(){
        interloperIsSelected = false;
        fileOps.writeFirstLine(false, sysID,getSystemName());
        stageOfRunning = "neverInterloper";
    }

    public static ArrayList<Body> getBodies(){
        return bodies;
    }

    public static void updateBodies(double dt, double time){
       // System.out.println("interloper is "+interloper + " w radius "+ interloper.getRadius());
        RK4(dt);
        checkCollisions(time);
        setHabitabilities(bodies);
    }


    public static ArrayList<Body> getSystemData(){
        //select all bodies from the required system, name is done in separate subroutine
        ArrayList<Body> bods = new ArrayList<>();
        try{
            bods = MariaDBConnector.getBodiesOfSystem(sysID);
        }
        catch (Exception e){
            throw new RuntimeException("problem with fetching bodies at simulator level: "+e);
        }
        return bods;
    }
    public static String getSystemName(){

        //get the name of the system with this ID from the database for storing to the CSV file
        return MariaDBConnector.getSystemName(sysID);
    }

    public static void writeSnapshot(double time){
        fileOps.writeSnapshot(time,bodies);
    }
    private static ArrayList<Body> makeNewBodyList(){
        ArrayList<Body> newBodies = new ArrayList<>();
        for(Body body:originalBodies){
            newBodies.add(body.returnCopy());
        }
      //  System.out.println("originalBodies: "+originalBodies);
        return newBodies;
    }

    static private double significanceCuttoff = 1e5;
    static private double significanceMultiplier = 0.6;
    public static void endSimulation(double time){
        if(interloper != null) {
            interloper = interloperCopy.returnCopy();
        }

        //System.out.println("at end, interloperIsSelected = "+interloperIsSelected+" inSim = "+interloperInSimulation);
       // System.out.println(interloperCopy);
        loader = new SimulatorControllerLoad(stage);
        fileOps.closeOutputFileHandle();

        //there are 3 possibilities in this scenario:
        // 1: the user just wants to leave
        // 2: the user has run without the interloper and now wants to run with it
        // 3: the user has run with the interloper and now wants a critical mass

        //in 2 and 3, we need to store data about the system that has just been run.
        if(interloperIsSelected == false){
            //then the user has finished altogether and just wants to leave.
            //so just boot up the main menu
            loader.load("MainMenuView.fxml","Main Menu");
        }
        else if(interloperInSimulation){
            //then the user wants an updated critical mass
            //first we need to calculate the significance
            interloperSignificance = findSignificance(); //this returns a number which corresponds to the significance;
            noCollisionsInter = 0;
            //now we need to find a new mass of the interloper based on this significance
           // proposedNewMass = interloper.getMass() / ((interloperSignificance/significanceCuttoff)*significanceMultiplier); LEGACY leave to show for tech.sol.
            if(interloperSignificance == 0){
                interloperSignificance = 0.01; // to avoid NaN
            }
            if(interloperSignificance < 0){
                interloperSignificance = -interloperSignificance;
            }
        //    interloperInSimulation = true;


            double significanceEffect = interloperSignificance / significanceCuttoff;
            significanceEffect = Math.pow(significanceEffect, significanceMultiplier);
            proposedNewMass = interloper.getMass()/significanceEffect;
            interloperCopy.setMass(proposedNewMass);
            //then we show it to the user:

            stageOfRunning = "runningWithInterloper";
            loader.load("SignificanceValue.fxml", "Controls");
            bodies = makeNewBodyList();
            bodies.add(interloper);
            restart();

        }
        else{
            interloperInSimulation = true;
            //then the user has selected an interloper, and it hasn't been done yet, so we will play the simulation again with the interloper.

            //first we need the quitting time so that we know when to quit next time
            quittingTime = time;
            //make a copy of the last position of all the bodies
            for(Body body : getBodies()){
                lastSnapNoInterloper.add(body.returnCopy());
            }
            //now reset the bodies to the original and add the interloper
            bodies = makeNewBodyList();
            bodies.add(interloper);
            loader.load("3DBodySimulator.fxml", "3D body");


            stageOfRunning = "runningWithInterloper";

            restart();

        }

       // System.out.println(interloper);
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

        //the position = the unit vector * (random between furthest out and double that)
        double distAway = largestPos * (1 + random.nextFloat());
        Vector3D interloperPosition = Vector3D.multiply(unitRandom, distAway);
        Vector3D interloperVelocity = interloperPosition.multiply(-1e-7);

        //to get the correct radius, we will need the density and the mass of the interloper
        //this will allow us to find the volume, which we can use to find the radius

        //the density of moon rock is about 2400 kg/m^3 on average, so this is what we will use
        double density = 2400;
        // density = mass/volume so therefore volume = mass/density
        double interloperVolume = interloperMass / density;
        // volume = (4/3)(pi)(r^3) therefore r = (v/(4pi/3))^1/3
        double interloperRadius = Math.pow(interloperVolume/(4*3.14159265359/3),0.333f);
        interloperPosition.setName("pos");
        interloperVelocity.setName("vel");

        if(Double.isNaN(interloperRadius)){
            interloperRadius = 0;
        }
        if(Double.isNaN(interloperMass)){
            interloperMass = 0;
        }


      //  Vector3D interloperVelocity = new Vector3D(0,0,0,"vel");

        Body interloper = new Body(interloperPosition,interloperVelocity, "Interloper",interloperMass, interloperRadius, false);
       // System.out.println("random interloper is: "+interloper);
        //System.out.println("interloper made is "+interloper);
        return interloper;
    }


    private static Vector3D[] getAccelerationOfTwoBodies(Vector3D b1Pos, Vector3D b2Pos, double b1Mass, double b2Mass){
       // System.out.println(bodies);
        double dist = Vector3D.getDistance(b1Pos, b2Pos);
        // use this to find the magnitude of the force on each body from F = Gm1m2/r^2, which will be the same for both
        double massProduct = b1Mass * b2Mass;
        double force = Math.pow(dist, -2) * massProduct;
        force = force * G;
        // correct force is being generated

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
        //System.out.println(accs[0] + " " + accs[1]);
        //accelerations are working correctly for 2 bodies
        return accs;
    }// gets the acceleration on two bodies based on their position and masses

    private static Vector3D[] getAllAccelerations(Vector3D[] positions, double[] masses){
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
        try {
            for (Vector3D acc : accelerations) {
                acc.setName("acc");
            }
        }
        catch (Exception e){

        }

        if(accelerations.length == 1){
            accelerations[0] = new Vector3D(0,0,0, "acc");
        }
        return accelerations;
    }

    private static void setHabitabilities(ArrayList<Body> bodies){

        //for each planet, look at all the stars and do an I/r^2 calculation to find the habitability
        for(Body body : bodies){
            if(body instanceof Planet){
                double newHabitability = 0;
                for(Body otherBody : bodies){
                    if(otherBody instanceof Star){
                        double distSquared = Math.pow(Vector3D.getDistance(otherBody.getPosition(), body.getPosition()),2);
                        newHabitability += ((Star) otherBody).getIllumination()/distSquared;
                    }
                }

                ((Planet) body).setHabitability(newHabitability);
            }
        }
    }

    private static boolean canRK4(double dt){
        boolean valid = true;
        if(dt < 0){
            valid = false;
        }
        ArrayList<Vector3D> positions = new ArrayList<>();
        for(Body body : bodies){
            Vector3D bodyPos = body.getPosition();
            if(body.getMass() <= 0){
                valid = false;
            }
            for(Vector3D pos : positions){
                if(bodyPos.getComponent(0) == pos.getComponent(0) &&
                        bodyPos.getComponent(1) == pos.getComponent(1) &&
                        bodyPos.getComponent(2) == pos.getComponent(2)){
                    valid = false;
                }
            }
            positions.add(bodyPos);
        }
        return valid;
    }
    private static void RK4(double dt){
        if(!canRK4(dt)){
            //nothing will happen
        }
        else {
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
            double[] masses = new double[numBodies];
            //calculting kv[0]
            for (int counter = 0; counter < numBodies; counter++) {
                Body currentBody = bodies.get(counter);
                positions[counter] = currentBody.getPosition();// record relevant position and mass
                masses[counter] = currentBody.getMass();
                kv[0][counter] = currentBody.getVelocity(); // set first kv to velocity
            }
            ks[0] = getAllAccelerations(positions, masses); // pass those positions and masses to calc acceleration

            ks[1] = getAllAccelerations(Vector3DArrayOperations.addVectors(positions, Vector3DArrayOperations.multiplyVectors(kv[0], dt / 2)), masses);
            kv[1] = Vector3DArrayOperations.addVectors(kv[0], Vector3DArrayOperations.multiplyVectors(ks[0], dt / 2));

            ks[2] = getAllAccelerations(Vector3DArrayOperations.addVectors(positions, Vector3DArrayOperations.multiplyVectors(kv[1], dt / 2)), masses); // acc of(position + kv1 * dt/2)
            kv[2] = Vector3DArrayOperations.addVectors(kv[0], Vector3DArrayOperations.multiplyVectors(ks[1], dt / 2)); // velocity + ks1 * dt/2

            ks[3] = getAllAccelerations(Vector3DArrayOperations.addVectors(positions, Vector3DArrayOperations.multiplyVectors(kv[2], dt)), masses); // acc of(position + kv2 * dt)
            kv[3] = Vector3DArrayOperations.addVectors(kv[0], Vector3DArrayOperations.multiplyVectors(ks[2], dt)); // velocity + ks2 * dt

            // use kv and ks to predict new position and velocity

            int counter = 0;
            Vector3D[] sumOfDisplacementCoefficients = new Vector3D[numBodies];
            Vector3D[] sumOfVelocityCoefficients = new Vector3D[numBodies];

            // apply correct multipliers:
            ks[1] = Vector3DArrayOperations.multiplyVectors(ks[1], 2);
            ks[2] = Vector3DArrayOperations.multiplyVectors(ks[2], 2);

            kv[1] = Vector3DArrayOperations.multiplyVectors(kv[1], 2);
            kv[2] = Vector3DArrayOperations.multiplyVectors(kv[2], 2);
            for (Body body : bodies) {
                //get dt/6 * ks1+2ks2+2ks3+ks4
                sumOfDisplacementCoefficients[counter] = Vector3D.add(Vector3D.add(Vector3D.add(ks[0][counter], ks[1][counter]), ks[2][counter]), ks[3][counter]);
                sumOfDisplacementCoefficients[counter] = Vector3D.multiply(sumOfDisplacementCoefficients[counter], dt / 6);

                //get dt/6 * kv1+2kv2+2kv3+kv4
                sumOfVelocityCoefficients[counter] = Vector3D.add(Vector3D.add(Vector3D.add(kv[0][counter], kv[1][counter]), kv[2][counter]), kv[3][counter]);
                sumOfVelocityCoefficients[counter] = sumOfVelocityCoefficients[counter].multiply(dt / 6);

                body.setVelocity(body.getVelocity().addVector(sumOfDisplacementCoefficients[counter]));
                body.setPosition(body.getPosition().addVector(sumOfVelocityCoefficients[counter]));
                body.getVelocity().setName("vel");
                body.getPosition().setName("pos");
                counter++;
            }
        }
    }

    private static double COMcoefficient = 1e-10;
    private static double velCoefficient = 1e-3;
    private static double collisionCoefficient = 1e6;
    private static double habitabilityCoefficient = 1e3;
    private static double findSignificance(){
        double significance = 0;
        //this subroutine is in charge of finding the significance of the interloper. First it needs some values
        //the first value is some average of how much the planets have moved.
        //the best way of doing this would be to find the centre of mass of the whole system
        //this is because the centre of mass will not move at all when bodies combine.
        Vector3D noInterloperCOM = new Vector3D(0,0,0);
        Vector3D withInterloperCOM = new Vector3D(0,0,0);

        Vector3D noInterloperSumOfVelocities = new Vector3D(0,0,0);
        Vector3D withInterloperSumOfVelocities = new Vector3D(0,0,0);

        double noInterloperHabitability = 0;
        double withInterloperHabitability = 0;

        double origSize = 0;
        double newSize = 0;

        double sumOfOldMass = 0;
        double sumOfNewMass = 0;

        for(Body body : lastSnapNoInterloper){
            if(body.isSignificant()) {
                //System.out.println("looking at "+body);
                origSize++;
                noInterloperCOM = noInterloperCOM.addVector(body.getPosition().multiply(body.getMass()));
                noInterloperSumOfVelocities = noInterloperSumOfVelocities.addVector(body.getVelocity());
                sumOfOldMass += body.getMass();
                if (body instanceof Planet) {
                    noInterloperHabitability += ((Planet) body).getHabitability();
                }
            }
        }

        for(Body body : bodies){
            if(body.isSignificant()) {
                //System.out.println("looking at "+body);
                newSize++;
                withInterloperCOM = withInterloperCOM.addVector(body.getPosition().multiply(body.getMass()));
                withInterloperSumOfVelocities = withInterloperSumOfVelocities.addVector(body.getVelocity());
                if (body instanceof Planet) {
                    withInterloperHabitability += ((Planet) body).getHabitability();
                }
                sumOfNewMass += body.getMass();
            }
        }
        noInterloperSumOfVelocities.multiply(1/origSize);
        withInterloperSumOfVelocities.multiply(1/newSize);

        noInterloperCOM = noInterloperCOM.multiply(1/sumOfOldMass);
        withInterloperCOM = withInterloperCOM.multiply(1/sumOfNewMass);

        //we do the same thing to the velocity to make it a bit more 'normalised':
        noInterloperSumOfVelocities = noInterloperSumOfVelocities.multiply(1/sumOfOldMass);
        withInterloperSumOfVelocities = withInterloperSumOfVelocities.multiply(1/sumOfNewMass);

        double COMChange = Vector3D.getDistance(noInterloperCOM, withInterloperCOM);
        System.out.println("COM change = "+COMChange);
        double velChange = Vector3D.getDistance(noInterloperSumOfVelocities,withInterloperSumOfVelocities);
        System.out.println("VEL change = "+velChange);
     //   double noCollisions = Math.abs(origSize - newSize); //this is the no of collisions
        double noCollisions = Math.abs(noCollisionsInter - noCollisionsNoInter);
        System.out.println(" w interloper coll = "+noCollisionsInter+ " w/out = "+noCollisionsNoInter);
        System.out.println("noCollisions = "+noCollisions);
        double habitabilityChange = Math.abs(withInterloperHabitability - noInterloperHabitability);
        System.out.println("habitability change = " + habitabilityChange);

        significance += COMChange * COMcoefficient + velChange * velCoefficient + noCollisions +
                noCollisions * collisionCoefficient + habitabilityChange * habitabilityCoefficient;

        return significance;
    }

    private static void checkCollisions(double time){
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
                // get sum of radii
                double sumOfRadii = body.getRadius() + otherBody.getRadius();
                // get distance between them
                double dist = body.getPosition().getDistance(otherBody.getPosition());

                if(dist <= sumOfRadii){
                    //then a collision has occured
                    // the result of the collision will depend on velocity and mass of the planet
                   // System.out.println("COLLISION between "+body.getName()+" and "+otherBody.getName());

                    //Rnew = cube root(R1^3 + R2^3)
                    double newRadius = Math.pow((Math.pow(body.getRadius(),3) + Math.pow(otherBody.getRadius(),3)),0.333d);
                    //Mnew = m1 + m2
                    double newMass = body.getMass() + otherBody.getMass();
                    //get momentum of old bodies
                    Vector3D momentumCurrent = body.getVelocity().multiply(body.getMass());
                    Vector3D momentumOther = otherBody.getVelocity().multiply(otherBody.getMass());

                    //use this to find momentum of new body
                    Vector3D MVnew = momentumCurrent.addVector(momentumOther);
                    //and so velocity vector
                    //Vector3D Vnew = Vector3D.multiply(MVnew, 1/newMass);
                    Vector3D Vnew = MVnew.multiply(1/newMass);

                    // the new position should be at the centre of mass between the two objects
                    // this is done by doing (Vfrom 1 to another) * (m1/(m1+m2))
                    Vector3D posBtoA = Vector3D.add(body.getPosition(), Vector3D.multiply(otherBody.getPosition(),-1));// vector from otherBody to body
                //    System.out.println("pos from1 to other is" + posBtoA);
                    double ratio = body.getMass() / (otherBody.getMass() + body.getMass());
                 //   System.out.println("ratio is "+ratio);
                    posBtoA = posBtoA.multiply(ratio);// multiply by ratio
                    Vector3D newPos = Vector3D.add(otherBody.getPosition(), posBtoA); // adding to position of otherBody to give the centre of mass
                  //  System.out.println("old pos1: "+body.getPosition()+" old pos2: "+otherBody.getPosition()+" new pos: "+newPos);
                    Body newBody = null;
                    if(body instanceof Star || otherBody instanceof Star) {
                        double newLuminosity = 0;
                        if (body instanceof Star) {//   for a more accurate model I could add the loss in kinetic energy, but this is only a qualatitave measure
                            newLuminosity += ((Star) body).getIllumination();
                        }
                        if (otherBody instanceof Star) {
                            newLuminosity += ((Star) otherBody).getIllumination();
                        }
                        newBody = new Star(newPos,Vnew, "collision between "+body.getName()+" and "+otherBody.getName(), newMass, newRadius, true, newLuminosity);
                    }
                    //else if because if a planet collides with a star, it will be unlivable
                    else if(body instanceof Planet || otherBody instanceof Planet){
                        newBody = new Planet(newPos, Vnew, "collision between "+body.getName()+" and "+otherBody.getName(), newMass, newRadius, true);
                    }
                    else{
                        newBody = new Body(newPos, Vnew, "collision between "+body.getName()+" and "+otherBody.getName(), newMass, newRadius, true);
                    }

                    if(interloperInSimulation){
                        noCollisionsInter++;
                    }
                    else{
                        noCollisionsNoInter++;
                    }
                    bodies.remove(iterator);
                    bodies.remove(currentBody);
                    bodies.add(newBody);

                    fileOps.writeCollision(time, body, otherBody);

                    currentBody = bodies.size(); //end the loop so that no more collisions occur given that bodies has changed
                    iterator = bodies.size();
                }

            }
            currentBody++;
            //have now done all collisions for that body
        }


    }

   // public static void setLastSnapNoInterloper(ArrayList<Body> bodies){
   //     lastSnapNoInterloper = bodies;
   // }

    public static void ensureBodiesHasBeenReset(){
       // System.out.println("resetting bodies");
        bodies = makeNewBodyList();
        if(interloperInSimulation){
            bodies.add(interloperCopy.returnCopy());
        }
    }
}
