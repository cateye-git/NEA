package Simulate;

public class Vector3D {
    private double[] components = {0,0,0}; // initialise double array of coefficients

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name = "Unnamed";    //    initialise the name to be 'Unnamed'

    public double[] getAllComponents(){
        return components;
    }   //return the array of doubles
    public double getComponent(int index){
        if(index >= 0 && index <= 2) {
            return components[index];
        }
        else{
            throw new RuntimeException("cannot access coefficient "+index+" of 3D Vector "+name);
        }
    }   // check that the index requested actually exists, and if so return the
        // component at that index
    public void setAllComponents(double x, double y, double z){
        components[0] = x;
        components[1] = y;
        components[2] = z;
    }   //set the components all at once from individual components
    public void setAllComponents(double[] newComponents){
        if(newComponents.length == 2){
            int counter = 0;
            for(double newComponent : newComponents){
                components[counter] = newComponent;
                counter++;
            }
        }
        else{
            throw new RuntimeException("Cannot input double array not of length 3 to 3D Vector "+name);
        }
    }   // set the components from a double array, first checking that the array is of the right length
    public void setComponent(int index, double value){
        if(index >= 0 && index <= 2) {
            components[index] = value;
        }
        else{
            throw new RuntimeException("cannot change coefficient "+index+" of 3D Vector "+name + " because it has 3 coefficients");
        }
    }   //set a component at the index specified, if the index is valid

    @Override
    public String toString(){
        String ref = "vector "+ name+ " ("+getComponent(0)+", "
                +getComponent(1)+ ", "+ getComponent(2)+")";
        return ref;
    }   //  return the name and all the components when the item is printed

    public String returnComponentsAsString(){
        return "("+components[0] + ", "+components[1]+", "+components[2]+")";
    }

    public Vector3D(double x, double y, double z){
        components[0] = x;
        components[1] = y;
        components[2] = z;
    }   // initialisation of the components in constructor

    public Vector3D(double x, double y, double z, String name){
        components[0] = x;
        components[1] = y;
        components[2] = z;
        this.name = name;
    } // initialisation of components and name in constructor

    public static Vector3D add(Vector3D v1, Vector3D v2){
        v1 = nullTo0(v1);// if any Vectors are supplied which are null, change that to an
        v2 = nullTo0(v2);// empty new Vector
        Vector3D vToReturn = new Vector3D(0,0,0);// make a new vector
        for(int counter = 0;counter <= 2;counter++){ // set each component to be the sum of the relevant components
            vToReturn.setComponent(counter, v1.getComponent(counter) + v2.getComponent(counter));
        }
        return vToReturn;
    }// make a new vector which is the sum of two vectors
    //  by making a new vector and then setting each component to be the sum of the corresponding components
    //  from the other function

    public Vector3D addVector(Vector3D v1){
        v1 = nullTo0(v1);// if any Vectors are supplied which are null, change that to an empty new Vector
        Vector3D returnVector = new Vector3D(0,0,0);
        for(int counter = 0;counter <= 2;counter++){ // set each component to be the sum of the relevant components
            returnVector.setComponent(counter, v1.getComponent(counter) + getComponent(counter));
        }
        return returnVector;
    }
    public static double getMagnitude(Vector3D vec){
        vec = nullTo0(vec);
        double magnitude = 0;
        for(double component : vec.getAllComponents()){// sum the squares of the components
            magnitude += component*component;
        }
        magnitude = Math.sqrt(magnitude);// square root that sum of squares
        return magnitude;
    } // return the magnitude of the vector, calculated as the root of the sum of the squares of the components
    public double getMagnitude(){
        double magnitude = 0;
        for(double component : getAllComponents()){// sum the squares of the components
            magnitude += component*component;
        }
        magnitude = Math.sqrt(magnitude);// square root that sum of squares
        return magnitude;
    }

    public static Vector3D getUnitVector(Vector3D vec){
        vec = nullTo0(vec);
        Vector3D unitVector = new Vector3D(0,0,0, "unit "+vec.getName()); // make a new vector

        if(getMagnitude(vec) != 0){
            //if this isn't true, then 0,0,0 has been passed in
            //so just skip over everything and return 0
            double mag = getMagnitude(vec); // get the magnitude of the old vector and save it
            int counter = 0;
            for (double component : vec.getAllComponents()) { // loop through each component of the old vector
                unitVector.setComponent(counter, component / mag); // set that component of the new vector to be the
                counter++; // old one divided by the magnitude
            }

        }
        return unitVector;
    } // return a unit vector of the vector given by dividing each component by the magnitude and putting it in a new object

    public Vector3D getUnitVector(){
        nullTo0(this);
        Vector3D unitVector = new Vector3D(0,0,0, "unit "+getName()); // make a new vector
        double mag = getMagnitude();
        if(mag != 0){
            //if this isn't true, then 0,0,0 has been passed in
            //so just skip over everything and return 0
            int counter = 0;
            for (double component : getAllComponents()) { // loop through each component of the old vector
                unitVector.setComponent(counter, component / mag); // set that component of the new vector to be the
                counter++; // old one divided by the magnitude
            }
        }
        return unitVector;
    }


    public static Vector3D multiply(Vector3D vec, double multiplier){
        vec = nullTo0(vec);
        Vector3D returnVector = new Vector3D(0,0,0, vec.getName()); // make a new vector
        // set that vector to direction
        int counter = 0;
        for(double component : vec.getAllComponents()){ // loop through each component of the old vector
            returnVector.setComponent(counter,component*multiplier); // set that component of the new vector to be the
            counter++; // old one multiplied by the multoploier
        }
        return returnVector;
    }
    public Vector3D multiply(double multiplier){
        Vector3D returnVector = new Vector3D(0,0,0);
        try{
            nullTo0(this);
            int counter = 0;
            for(double component : getAllComponents()){ // loop through each component of the old vector
                returnVector.setComponent(counter,component*multiplier); // set that component of the new vector to be the
                counter++; // old one multiplied by the multoploier
            }
        }
        catch (Exception e){
            System.out.println(e);

        }

        return returnVector;
    }

    public static double getDistance(Vector3D vec1, Vector3D vec2){
        vec1 = nullTo0(vec1);
        vec2 = nullTo0(vec2);
        double distance = 0;
        int counter = 0;
        for(double v2Component : vec2.getAllComponents()){// sum the squares of the distance between the components
            distance += Math.pow(v2Component - vec1.getComponent(counter),2);
            counter++;
        }
        distance = Math.sqrt(distance);
        return distance;
    }// get the distance between two vectors by finding the magnitude of the position vector between them

    public double getDistance(Vector3D vec1){
        vec1 = nullTo0(vec1);
        double distance = 0;
        int counter = 0;
        for(double v1Component : vec1.getAllComponents()){// sum the squares of the distance between the components
            distance += Math.pow(v1Component - getComponent(counter),2);
            counter++;
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    private static Vector3D nullTo0(Vector3D vectToConvert){
        // to convert a vector from null to a new vector if necessary
        Vector3D returnVector = new Vector3D(0,0,0);
        // try getting the name, if this doesn't work give name 'Unnamed'
        try{
            returnVector.setName(vectToConvert.getName());
        }
        catch (NullPointerException e){
            returnVector.setName("Unnamed");
        }

        //try setting components, any null components will be set to 0
        try {
            vectToConvert.getAllComponents();
            for (int counter = 0; counter < vectToConvert.getAllComponents().length; counter++) {
                try {
                    returnVector.setComponent(counter, vectToConvert.getComponent(counter));
                } catch (NullPointerException e) {
                    returnVector.setComponent(counter, 0);
                }
            }
        }
        catch (NullPointerException e){
            returnVector.setAllComponents(0,0,0);
        }


        return returnVector;
    }

    public String convertToCSVEntry(){
        String ref = getComponent(0)+","+getComponent(1)+","+getComponent(2);
        return ref;
    }
    public static Vector3D getDirection(Vector3D to, Vector3D from){
        to = nullTo0(to);
        from = nullTo0(from);

        Vector3D returnVector = Vector3D.add(to,Vector3D.multiply(from,-1));
        return Vector3D.getUnitVector(returnVector);
    }

    public Vector3D getDirection(Vector3D to){
        to = nullTo0(to);

        Vector3D returnVector = new Vector3D(0,0,0);
        int counter = 0;
        for(double component : to.getAllComponents()){
            returnVector.setComponent(0, (component - getComponent(counter)*-1));
        }
        return  returnVector.getUnitVector();
    }

    public Vector3D returnCopy(){
        Vector3D copy = new Vector3D(components[0],components[1],components[2]);
        copy.setName(name);
        return copy;
    }
}