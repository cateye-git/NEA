package Simulate;

import Database.MariaDBConnector;
import com.example.nea.DataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class testingClass {
    public static void main(String[] args) throws Exception {
        MariaDBConnector.openConnection();
        MariaDBConnector.editInstanceOfBody(99,47,86,2,2, 87, 0, "bad name",
                "planet");
        MariaDBConnector.closeConnection();



        /*S
        DataStore[] store = MariaDBConnector.getBodyDataFromSystem(-1);
        for(DataStore data : store){
            System.out.println("bodyID " +data.getIds()[0] + " posID " + data.getIds()[1] + " velID "+data.getIds()[2] + " " + data);
        }

         */
    }
}
