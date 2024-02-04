package Simulate;

import Database.MariaDBConnector;
import com.example.nea.DataStore;
import com.example.nea.SignificanceValueController;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class testingClass {
    static private double significanceCuttoff = 1e5;
    static private double significanceMultiplier = 0.6;
    public static void main(String[] args) throws Exception {


        /*S
        DataStore[] store = MariaDBConnector.getBodyDataFromSystem(-1);
        for(DataStore data : store){
            System.out.println("bodyID " +data.getIds()[0] + " posID " + data.getIds()[1] + " velID "+data.getIds()[2] + " " + data);
        }

         */

        double interloperSignificance = 1e100; //this returns a number which corresponds to the significance;
        //now we need to find a new mass of the interloper based on this significance
        // proposedNewMass = interloper.getMass() / ((interloperSignificance/significanceCuttoff)*significanceMultiplier); LEGACY
        if(interloperSignificance == 0){
            interloperSignificance = 0.01; // to avoid NaN
        }
        if(interloperSignificance < 0){
            interloperSignificance = -interloperSignificance;
        }
        double significanceEffect = interloperSignificance / significanceCuttoff;
        significanceEffect = Math.pow(significanceEffect, significanceMultiplier);
        double proposedNewMass = 1e6 * significanceEffect;

        System.out.println(significanceEffect);
        System.out.println(proposedNewMass);
    }
}
