package Simulate;

import java.util.ArrayList;

public class VectorMaker {
    public static void main(String[] args) {
        /*
        Vector3D v1 = new Vector3D(1,2,3,"Vector 1");
        Vector3D v2 = new Vector3D(4,5,6,"Vector 2");
        System.out.println(v1);
        System.out.println(v2);

        Vector3D v3 = Vector3D.add(v1,v2);

        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);

        Vector3D v3Unit = Vector3D.getUnitVector(v3);
        System.out.println(v3Unit);
        System.out.println(Vector3D.getMagnitude(v3Unit));

         */

        //Body earth = new Body(0,0,0,0,0,0,"Earth",5.97e24,10);
       // Body moon = new Body(1000,0,0,0,0,0, "Moon",6.24e21,1000, true);
   //     Body otherBody = new Body(-1000,0,0,0,0,0,"otherBody",6.24e21,1040, true);
      //  Simulator.startUp(3);

  //      ArrayList<Body> bodies = new ArrayList<>();
       // bodies.add(earth);
      //  bodies.add(moon);
     //   bodies.add(otherBody);

        Simulator.startUp(3, true);
        for(Body body : Simulator.getBodies()){
            System.out.println(body);
        }




        //Simulator.checkCollisions(3);
        for(Body body : Simulator.getBodies()){
            System.out.println(body);
        }
        for(int counter = 0;counter < 100;counter++){
          //  PlanetSystem.RK4(bodies, 0.01f);

        }

    }
}
