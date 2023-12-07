package Database;

import Simulate.Body;
import Simulate.Planet;
import Simulate.Star;
import Simulate.Vector3D;
import org.w3c.dom.ls.LSOutput;

import javax.xml.transform.Result;
import java.io.IOException;
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


        makeQuery("insert into position (posX, posY, posZ) values (0,0,0);");
    }

    private static ResultSet makeQuery(String query){

        //makes a new result set, tries to query the database and puts the result in the resultSet, stops;
        ResultSet resultSet = null;
        try {
            //statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }
        catch (Exception e){
            System.out.println("error with SQL query "+query+": " + e);
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
                //System.out.println("line 77 MariaDBConnector: " +ans[counter]);
                counter++;
            }
        }
        catch (Exception e){
            System.out.println("error with getting systems: "+e);
        }
        return ans;
    }

    public static ArrayList<Body> getBodiesOfSystem(int id){
        //System.out.println("get bodies of system called");
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

    public static void copySystem(int id){
        try {
            String systemName = "";
            //so I need to copy the system, give it a new ID, and also make all of the new references which
            //connect the components of the existing system to the new one as well

            //so first I need to get the components of the original system
            ResultSet systemDetails = makeQuery("select * from system where system.systemID = " + id + ";");
            //this SHOULD only respond with 1 system, if not then the database has 2 entries with the same ID
            //          if so, throw an error because that means something's gone very wrong
            int noOfResults = 0;
            while (systemDetails.next()) {
                System.out.println(systemDetails.getInt(1));
                noOfResults += 1;
                systemName = systemDetails.getString(2);
            }
            if(noOfResults > 1){
                throw  new RuntimeException("more than 1 system with ID "+id);
            }
            //  now I know what the system name is that I need, so let's make a new system:
            makeQuery("insert into system (name) values (\""+systemName+"\");");

            //so now I have the actual system copied with a new ID, I need to copy all of the positions
            //and velocities and link them to the new system.

            // so to get all of the positions and velocities of the system as well as the bodyID:
            //System.out.println("line 142 mariaDBConnector now connecting the linkers together");
            ResultSet linkingDetails = makeQuery("select linker.bodyID, position.posID, velocity.velID, posX, posY" +
                    ", posZ, velX, velY, velZ from linker, position, velocity where position.posID = linker.posID and" +
                    " velocity.velID = linker.velID and linker.systemID = "+id+";");

            int greatestID = getHighestID("system", "systemID");
            //System.out.println("the highest systemID is "+greatestID);

            while(linkingDetails.next()){
                //splitting into what needs to go into linker, pos and vel:
                int[] posEntity = {linkingDetails.getInt(4),
                        linkingDetails.getInt(5),linkingDetails.getInt(6)};//pos x y z
                int[] velEntity = {linkingDetails.getInt(7),
                        linkingDetails.getInt(8),linkingDetails.getInt(9)};//vel x y z

                //and we make the linker later

                System.out.println("the positions to insert are:");
                for (int a : posEntity){
                    System.out.println(a);
                }

                System.out.println("putting the new position in");
                makeQuery("insert into position (posX, posY, posZ) values ("+posEntity[0]+","+posEntity[1]+","
                                                +posEntity[2]+");");
                System.out.println("putting the new velocity in");
                makeQuery("insert into velocity (velX, velY, velZ) values ("+velEntity[0]+","+velEntity[1]+","
                        +velEntity[2]+");");

                //for the sysID of linkerEntity, it needs to be the ID of the system which you just made, which
                //will be the highest one. Lucky we have a function for that
                // we also need to know the ID of the position and velocity we just made, which are also the highest by chance:

                int highestPosID = getHighestID("position","posID");
                //System.out.println("the current highest pos ID is "+highestPosID);
                int highestVelID = getHighestID("velocity","velID");
                //System.out.println("the current highest vel ID is "+highestVelID);
                //System.out.println("putting the new linker in");
                int[] linkerEntity = {greatestID,linkingDetails.getInt(1),highestPosID,
                        highestVelID}; // bodyID, new sysID, new posID, new velID

                //now to execute the queries:
                makeQuery("insert into linker values ("+linkerEntity[1]+","+linkerEntity[0]+","+linkerEntity[2]+","
                        +linkerEntity[3]+");");
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
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

    public static void deleteSystem(int id){
        //remove the system
        //remove the connections from bodies to that system
        //remove any bodies which don't have any connections to any systems (because they're no longer being used)

        //we also need to delete the relevant positions and velocities (and the links themselves):
        makeQuery("delete position,velocity,linker from position, velocity, linker where linker.posID = position.posID and linker.velID = velocity.velID and linker.systemID = "+id+";");
        //removing system:
        makeQuery("delete from system where systemID = "+id+";");


        //now for the hard part
        //so remove all bodies which don't have any connections to any systems
        //so this requires looping through each body in the system, and checking that at least one linker entity
        //has the bodyID of that body
        //so getting all the bodies:
        ResultSet bodyIDs = makeQuery("select body.bodyID from body;");
        ResultSet bodyIDFromLinkerQueryResult = makeQuery("select bodyID from linker order by bodyID desc;");
        int highestUsedId = -1;
        try {

            //so first we need a list of all of the body IDs from the linker table:
            ArrayList<Integer> bodyIDLinkerArray = new ArrayList<>();
            int lastID = -1;
            while(bodyIDFromLinkerQueryResult.next()){
                //so loop through all of the bodyIDs from the linker table and add them to a list
                //the thing is this would yield a lot of repeats, significantly increasing the time taken to execute all queries
                //as we run through this list several times
                //therefore we don't need repeats
                //this is why I have ordered by the bodyID, so we can look at the last ID and only add the current ID to the list if
                //it is different to the past one
                int currentID = bodyIDFromLinkerQueryResult.getInt(1);

                //System.out.println("line 195 MariaDBConnector testing body from linker of ID "+currentID);

                if(currentID != lastID){
                    bodyIDLinkerArray.add(currentID);
                    if(currentID > highestUsedId){
                        highestUsedId = currentID;
                    }
                    System.out.println("line 200 MariaDBConnector it was different to the last ID which is "+lastID);
                }
                lastID = currentID;
            }
            //now we have a list of all of the bodyIDs used with linkers.


            while (bodyIDs.next()) {
                int currentID = bodyIDs.getInt(1);
                System.out.println("line 213 mariaDBConnector looking at body of ID "+currentID);
                //now we have the ID of the body, and all the IDs from the linker, so just loop through
                //and remove the body if it is not needed
                boolean isBeingUsed = false;
                for(int bodyIDgiven : bodyIDLinkerArray){
                    System.out.println("line 218 mariaDBConnector testing whether it is the same as "+bodyIDgiven);
                    if(bodyIDgiven == currentID){
                        isBeingUsed = true;
                        System.out.println("line 221 mariaDBConnector it was!");
                    }
                }

                if(!isBeingUsed){
                    //now we need to remove the body with the current ID
                    makeQuery("delete from body where bodyID = "+currentID+";");
              //      System.out.println("line 228 mariaDBConnector removing body of ID "+currentID);
                }
            }

            //after doing all this, I need to change the autoIncrement angle for the bodies so that I don't get random
            //gaps between bodyIDs

            //to do that, I need to set the autoIncrement to the highest remaining bodyID:
            makeQuery("alter table body AUTO_INCREMENT = "+(highestUsedId+1));
            //unfortunately for the highest used systemID I can't just find it without using other commands, and I don't
            //want to give this user more commands than possible, so I need to loop through all the systems and find the number

            String[] systems = getSystems();
            int autoIncrementValueSystem = systems.length + 1;
            //if there were 2 systems, I want the autoIncrement to be set to 3
            //length of systems is going to be 2
            System.out.println("line 242 mariaDBConnector setting autoIncrement of system to "+autoIncrementValueSystem);
            makeQuery("alter table system AUTO_INCREMENT = "+autoIncrementValueSystem);
        }catch (Exception e){
            System.out.println("line 245 mariaDBConnector exception in getting all IDs relating to system "+id);
        }
    }

    private static int getHighestID(String table, String IDcolumnName){
        try {
            ResultSet highest = makeQuery("SELECT "+IDcolumnName+" FROM "+table+" ORDER BY "+IDcolumnName+" DESC LIMIT 0, 1");
            highest.next();
            return highest.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("line 283 MariaDBConnector getting highest ID error: " + e);
        }
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
