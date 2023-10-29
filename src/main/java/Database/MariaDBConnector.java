package Database;

import Simulate.Body;
import Simulate.Planet;
import Simulate.Star;
import Simulate.Vector3D;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

public class MariaDBConnector {

    private static Connection connection = null;
    private static Statement statement = null;
    private static String url = "jdbc:mariadb://localhost:3306/celestial_systems";
    private static String username = "java_editor";
    private static String pwd = "mouse_friendly_cave_glue"; //this can be stored in plaintext because security is not an issue and the user
                                                            //only has limited commands.
    public static void openConnection() {
        //System.out.println("Hello world!");

        try{
            Class.forName("org.mariadb.jdbc.Driver");

            connection = DriverManager.getConnection(url, username, pwd);

            statement = connection.createStatement();
        }
        catch (Exception e){
            System.out.printf("error creating mariaDB connection: " + e);
        }
    }

    private static ResultSet makeQuery(String query){

        //makes a new result set, tries to query the database and puts the result in the resultSet, stops;
        ResultSet resultSet = null;
        try {
            //statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }
        catch (Exception e){
            System.out.println("error with SQL query: " + e);
        }
        return resultSet;
    }

    private static int noOfEntries(String tableName){
        int ans = -1;

        try {
            ResultSet result = statement.executeQuery("select count (*) from "+tableName);
            while(result.next()) {
                ans = result.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("error with finding no of entries of "+tableName);
            throw new RuntimeException(e);
        }
        return ans;
    }

    public static String[] getSystems(){
        //System.out.println("finding length");
        int length = noOfEntries("system");
        //System.out.println("found length");
        String[] ans = new String[length]; //make a string array of length no of systems
        //System.out.println("making query");
        ResultSet result = makeQuery("select * from system");
        try {
            int counter = 0;
            while (result.next()) {
                ans[counter] = result.getInt(1) + "    " + result.getString(2);
            }
        }
        catch (Exception e){
            System.out.println("error with getting systems: "+e);
        }
        return ans;
    }

    public static ArrayList<Body> getBodiesOfSystem(int id){
        System.out.println("get bodies of system called");
        ArrayList<Body> bodies = new ArrayList<>();

        try{
            // 1:bodyID 2:name 3:mass 4:radius 5:illumination 6:type 7-9:pos 10-12:vel
            ResultSet bodyDetails = makeQuery("select body.bodyID, body.name, mass, radius, illumination, body.type, posX, posY, posZ, velX, velY, velZ from body, linker, system, velocity, position where system.systemID = linker.systemID and system.systemID = "+id+" and body.bodyID = linker.bodyID and position.posID = linker.posID  and velocity.velID  = linker.velID;");
            while(bodyDetails.next()){
                Vector3D pos = new Vector3D(bodyDetails.getDouble(7),bodyDetails.getDouble(8),bodyDetails.getDouble(9));
                Vector3D vel = new Vector3D(bodyDetails.getDouble(10),bodyDetails.getDouble(11),bodyDetails.getDouble(12));
                String type = bodyDetails.getString(6);
                //System.out.println("making body at pos "+pos + " w vel "+vel);
                if(type == "planet"){
                    bodies.add(new Planet(pos, vel, bodyDetails.getString(2),bodyDetails.getDouble(3),bodyDetails.getDouble(4),true));
                }
                else if (type == "star"){
                    bodies.add(new Star(pos, vel, bodyDetails.getString(2),bodyDetails.getDouble(3),bodyDetails.getDouble(4),true,bodyDetails.getDouble(5)));
                }
                else{
                    bodies.add(new Body(pos, vel, bodyDetails.getString(2),bodyDetails.getDouble(3),bodyDetails.getDouble(4),true));
                }
                System.out.println(bodies.size());
            }
        }
        catch (Exception e){
            System.out.println("error fetching bodies: "+e);
        }
        return bodies;
    }

    public static String getSystemName(int id){
        System.out.println("finding name for system id "+id);
        String name = "name not found";
        try {

            ResultSet result = makeQuery("select name from system where systemID = "+id);
            while (result.next()) {

                name = result.getString(1);
            }
        }
        catch (Exception e){
            System.out.println("error with getting system name: "+e);
        }
        return name;
    }

    public static void closeConnection(){
        try {
            connection.close();
        }
        catch (Exception e){
            System.out.println("error closing connection: "+e);
        }
    }
}
