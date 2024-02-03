package Database;

import Simulate.Body;
import Simulate.Planet;
import Simulate.Star;
import Simulate.Vector3D;
import com.example.nea.CreatorDataStorage;
import com.example.nea.DataStore;
import org.w3c.dom.ls.LSOutput;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MariaDBConnector {

    private static Connection connection = null;
    private static Statement statement = null;
    private static final String url = "jdbc:mariadb://localhost:3306/celestial_systems";
    private static final String username = "java_editor";
    private static final String pwd = "mouse_friendly_cave_glue"; //this can be stored in plaintext because security is not an issue and the user
                                                            //only has limited commands.
    public static void openConnection() {
        try{
            Class.forName("org.mariadb.jdbc.Driver");

            connection = DriverManager.getConnection(url, username, pwd);

            statement = connection.createStatement();
        }
        catch (Exception e){
           // System.out.printf("error creating mariaDB connection: " + e);
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
            //System.out.println("error with SQL query "+query+": " + e);
            //just don't do anything
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
            //System.out.println("error with finding no of entries of "+tableName);
            throw new RuntimeException("error finding number of entries of "+tableName+": "+e);
        }
        return ans;
    }

    public static DataStore[] getSystems(){
        int length = noOfEntries("system");
        DataStore[] ans = new DataStore[length]; //make a string array of length no of systems
        ResultSet result = makeQuery("select * from system;");
        try {
            int counter = 0;
            while (result.next()) {
                int sysID = result.getInt(1);
                String sysName = result.getString(2);

                ans[counter] = new DataStore(sysID,sysName);
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
            ResultSet bodyDetails = makeQuery("select body.bodyID, body.name, mass, radius, illumination, body.type, " +
                    "posX, posY, posZ, velX, velY, velZ from body, linker, system, velocity, position where system.systemID" +
                    " = linker.systemID and system.systemID = "+id+" and body.bodyID = linker.bodyID and position.posID = " +
                    "linker.posID  and velocity.velID  = linker.velID;");
            while(bodyDetails.next()){
                Vector3D pos = new Vector3D(bodyDetails.getDouble(7),bodyDetails.getDouble(8),
                        bodyDetails.getDouble(9));
                Vector3D vel = new Vector3D(bodyDetails.getDouble(10),bodyDetails.getDouble(11),
                        bodyDetails.getDouble(12));
                String type = bodyDetails.getString(6);
                //System.out.println("making body at pos "+pos + " w vel "+vel);
                if(type == "planet"){
                    bodies.add(new Planet(pos, vel, bodyDetails.getString(2),bodyDetails.getDouble(3),
                            bodyDetails.getDouble(4),true));
                }
                else if (type == "star"){
                    bodies.add(new Star(pos, vel, bodyDetails.getString(2),bodyDetails.getDouble(3),
                            bodyDetails.getDouble(4),true,bodyDetails.getDouble(5)));
                }
                else{
                    bodies.add(new Body(pos, vel, bodyDetails.getString(2),bodyDetails.getDouble(3),
                            bodyDetails.getDouble(4),true));
                }
            }
        }
        catch (Exception e){
        }
        return bodies;
    }

    public static DataStore[] getBodyDataFromSystem(int id){
        //the length component of this is a little harder - I need the length of the result of the query that
        //I will execute:
        ResultSet set = makeQuery("select bodyID from linker where linker.systemID = "+id+";");
        int length = 0;
        try {
            while(set.next()){
                length++;
            }
           // System.out.println("Length is "+length+" line 133 creatorEditorController");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DataStore[] bodyStuff = new DataStore[length];
        try{
            // 1:bodyID 2:name 3:mass 4:radius 5:illumination 6:type 7-9:pos 10-12:vel
            ResultSet bodyDetails = makeQuery("select body.bodyID, linker.posID, linker.velID,posX, posY,posZ,velX,velY,velZ, " +
                    "body.name from body, linker, position, velocity where body.bodyID = linker.bodyID and " +
                    "linker.systemID = "+id+" and velocity.velID = linker.velID and position.posID = linker.posID;");
            int counter = 0;
            while(bodyDetails.next()){
                Vector3D pos = new Vector3D(bodyDetails.getInt(4),bodyDetails.getInt(5),bodyDetails.getInt(6));
                Vector3D vel = new Vector3D(bodyDetails.getInt(7),bodyDetails.getInt(8),bodyDetails.getInt(9));

                int[] ids = {bodyDetails.getInt(1),bodyDetails.getInt(2),bodyDetails.getInt(3)};
                String prettyString = bodyDetails.getString(10) + pos.returnComponentsAsString()+" "+vel.returnComponentsAsString();

                DataStore result = new DataStore(ids,prettyString);
                bodyStuff[counter] = result;
                counter++;
            }
        }
        catch (Exception e){
            System.out.println("error fetching bodies: "+e);
        }
        return bodyStuff;
    }

    private static void addNewBody(int id, String name, double mass, double radius, double illumination, String type){
        makeQuery("insert into body values ("+id+",\""+name+"\","+mass+","+radius+","+illumination+",\""+type+"\");");
    }
    private static void addNewLinker(int bodyID, int sysID, int posID, int velID){
        makeQuery("insert into linker values ("+bodyID+","+sysID+","+posID+","+velID+");");
    }
    public static void addNewBodyToSystem(String name, double mass, double radius, double illumination, String type, Vector3D pos, Vector3D vel,int sysID){
        //make the new IDs:
        int newPosID = getHighestID("position","posID") + 1;
        int newVelID = getHighestID("velocity", "velID") + 1;
        int newBodyID = getHighestID("body","bodyID") + 1;

        addNewPos(newPosID,pos.getComponent(0),pos.getComponent(1),pos.getComponent(2));
        addNewVel(newVelID,vel.getComponent(0),vel.getComponent(1),vel.getComponent(2));
        addNewBody(newBodyID,name,mass,radius,illumination,type);
        //now we need to add the linker object
        addNewLinker(newBodyID,sysID, newPosID, newVelID);
    }
    public static DataStore[] getAllBodies(){
        DataStore[] bodyStuff = new DataStore[noOfEntries("body")];
        try{
            // 1:bodyID 2:name 3:mass 4:radius 5:illumination 6:type 7-9:pos 10-12:vel
            ResultSet bodyDetails = makeQuery("select bodyID, name from body;");
            int counter = 0;
            while(bodyDetails.next()){
                bodyStuff[counter] = new DataStore(bodyDetails.getInt(1),bodyDetails.getString(2));
                counter++;
            }
        }
        catch (Exception e){
            System.out.println("error fetching bodies: "+e);
        }

        return bodyStuff;
    }

    private static int addNewPos(int posID, double posX, double posY, double posZ){//returns the ID of the position made
       // int posID = getHighestID("position","posID")+1;
        makeQuery("insert into position values ("+posID+","+posX+","+posY+","+posZ+");");
        return posID;
    }
    private static int addNewVel(int velID, double velX, double velY, double velZ){//returns the ID of the velocity made
     //   int velID = getHighestID("velocity","velID")+1;
        makeQuery("insert into velocity values ("+velID+","+velX+","+velY+","+velZ+");");
        return velID;
    }


    public static void copySystemBodyLink(int bodyID, int sysID, int posID, int velID){
        //make the new IDs:
        //I wouldn't normally do this because SQL likes to do this itself and why should I ruin its fun
        //but I will need these IDs so that I can add the new linker object
        int newPosID = getHighestID("position","posID") + 1;
        //System.out.println("newPosID: "+newPosID);
        int newVelID = getHighestID("velocity", "velID") + 1;

        //then we need to get the data to copy

        try {
            Vector3D pos = getPos(posID);
            Vector3D vel = getVel(velID);

            //ok we have the data to copy now, so we need to actually copy it.
            //first make the new position and velocity:
            makeQuery("insert into position values ("+newPosID+", "+pos.getComponent(0)+","+pos.getComponent(1)+","+pos.getComponent(2)+");");
            makeQuery("insert into velocity values ("+newVelID+", "+vel.getComponent(0)+","+vel.getComponent(1)+","+vel.getComponent(2)+");");
            //we don't need to spawn in a new body because we are just making a new linker
            //so all that is left is to make said linker:
            makeQuery("insert into linker values ("+bodyID+","+sysID+","+newPosID+","+newVelID+");");
            //and we are done.
        }
        catch (Exception e){
          //  System.out.println("problemt at line 190 mariaDB connector copying a system body link: "+e);
        }

    }
    public static void copySystemBodyLink(int bodyID, int sysID, double posX,double posY, double posZ, double velX, double velY, double velZ){
        //make the new IDs:
        int newPosID = getHighestID("position","posID") + 1;
        int newVelID = getHighestID("velocity", "velID") + 1;

        //then we need to get the data to copy

        try {
            //ok we have the data to copy now, so we need to actually copy it.
            //first make the new position and velocity:
            makeQuery("insert into position values ("+newPosID+", "+posX+","+posY+","+posZ+");");
            makeQuery("insert into velocity values ("+newVelID+", "+velX+","+velY+","+velZ+");");
            //we don't need to spawn in a new body because we are just making a new linker
            //so all that is left is to make said linker:
            makeQuery("insert into linker values ("+bodyID+","+sysID+","+newPosID+","+newVelID+");");
            //and we are done.
        }
        catch (Exception e){
            //System.out.println("problem at line 234 mariaDB connector copying a new system body link: "+e);
        }

    }//for if there isn't a position and velocity yet

    public static Vector3D getPos(int posID) throws Exception{
        ResultSet posDets = makeQuery("select posX, posY, posZ from position where posID = "+posID+";");
        Vector3D result = new Vector3D(0,0,0);
        while (posDets.next()){
            result.setComponent(0,posDets.getDouble(1));
            result.setComponent(1,posDets.getDouble(2));
            result.setComponent(2,posDets.getDouble(3));
        }
        return result;
    }

    public static void updateSysName(int sysID, String name){
        makeQuery("update system set name = \""+name+"\" where systemID = "+sysID+";");
    }
    public static Vector3D getVel(int velID) throws Exception{
        ResultSet velDets = makeQuery("select velX, velY, velZ from velocity where velID = "+velID+";");
        Vector3D result = new Vector3D(0,0,0);
        while (velDets.next()){
            result.setComponent(0,velDets.getDouble(1));
            result.setComponent(1,velDets.getDouble(2));
            result.setComponent(2,velDets.getDouble(3));
        }
        return result;
    }
    public static Body getBody(int bodyID) throws Exception{
        ResultSet bodyDets = makeQuery("select * from body where bodyID = "+bodyID+";");
        Body returnBody = new Body(0,0,0,0,0,0,"unnamed",0,0,false);
        while (bodyDets.next()){
            //bodyID  name  mass  radius  illumination  type
            String type = bodyDets.getString("type");
            String name = bodyDets.getString(2);
            //int id = bodyDets.getInt(1);
            double mass = bodyDets.getDouble(3);
            double radius = bodyDets.getDouble(4);

           // System.out.println(type);
            if(type.equals("star")){
                double illumination = bodyDets.getInt(5);
                returnBody = new Star(0,0,0,0,0,0,name,mass,radius,true,illumination);
                //System.out.println("star");
            }
            else if (type.equals("planet")){
                returnBody = new Planet(0,0,0,0,0,0,name,mass,radius,true);
               // System.out.println("planet");
            }
            else{
                returnBody = new Body(0,0,0,0,0,0,name,mass,radius,true);
            }
        }
        return returnBody;
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
                noOfResults += 1;
                systemName = systemDetails.getString(2);
            }
            if(noOfResults > 1){
                throw new RuntimeException("more than 1 system with ID "+id); //this shouldn't be the case because the
                //                                                              database will error if it happens
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

            int greatestSysID = getHighestID("system", "systemID"); // so this should get the ID that we just made

            while(linkingDetails.next()){
                //splitting into what needs to go into linker, pos and vel:
                int[] posEntity = {linkingDetails.getInt(4),
                        linkingDetails.getInt(5),linkingDetails.getInt(6)};//pos x y z
                int[] velEntity = {linkingDetails.getInt(7),
                        linkingDetails.getInt(8),linkingDetails.getInt(9)};//vel x y z
                //and we make the linker later


                makeQuery("insert into velocity (velX, velY, velZ) values ("+velEntity[0]+","+velEntity[1]+","
                        +velEntity[2]+");");
                //unfortunately, for the position, I've managed to name the table as some function or keyword so if my
                //query contains 'position' and then a bracket, it will throw an error
                //this means that I need to put in the ID unfortunately
                //therefore I need the highest ID + 1:
                int highestPosID = getHighestID("position","posID") + 1;
                makeQuery("insert into position values ("+highestPosID+","+posEntity[0]+","+posEntity[1]+","
                        +posEntity[2]+");");

                //for the sysID of linkerEntity, it needs to be the ID of the system which you just made, which
                //will be the highest one. Lucky we have a function for that
                // we also need to know the ID of the position and velocity we just made, which are also the highest by chance:

                //System.out.println("the current highest pos ID is "+highestPosID);
                int highestVelID = getHighestID("velocity","velID");
                //System.out.println("the current highest vel ID is "+highestVelID);
                //System.out.println("putting the new linker in");
                int[] linkerEntity = {greatestSysID,linkingDetails.getInt(1),highestPosID,
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
        String name = "name not found";
        try {

            ResultSet result = makeQuery("select name from system where systemID = "+id);
            while (result.next()) {

                name = result.getString(1);
            }
        }
        catch (Exception e){
           // System.out.println("error with getting system name: "+e);
        }
        return name;
    }


    public static void deleteSystem(int id){
        //remove the system
        //remove the connections from bodies to that system
        //remove any bodies which don't have any connections to any systems (because they're no longer being used)

        //we also need to delete the relevant positions and velocities (and the links themselves):
        makeQuery("delete position,velocity,linker from position, velocity, linker where linker.posID = position.posID and" +
                " linker.velID = velocity.velID and linker.systemID = "+id+";");
        //removing system:
        makeQuery("delete from system where systemID = "+id+";");

        removeUnusedBodies();

    }

    private static void removeUnusedBodies() {
        //so remove all bodies which don't have any connections to any systems
        //so this requires looping through each body in the system, and checking that at least one linker entity
        //has the bodyID of that body
        //so getting all the bodies:
        ResultSet bodyIDs = makeQuery("select body.bodyID from body;");
        ResultSet bodyIDFromLinkerQueryResult = makeQuery("select bodyID from linker order by bodyID desc;");
        try {
            //so first we need a list of all of the body IDs from the linker table:
            ArrayList<Integer> bodyIDLinkerArray = new ArrayList<>();
            int lastID = -1;
            while (bodyIDFromLinkerQueryResult.next()) {
                //so loop through all of the bodyIDs from the linker table and add them to a list
                //the thing is this would yield a lot of repeats, significantly increasing the time taken to execute all queries
                //as we run through this list several times
                //therefore we don't need repeats
                //this is why I have ordered by the bodyID, so we can look at the last ID and only add the current ID to the list if
                //it is different to the past one
                int currentID = bodyIDFromLinkerQueryResult.getInt(1);
                if (currentID != lastID) {
                    bodyIDLinkerArray.add(currentID);
                }
                lastID = currentID;
            }
            //now we have a list of all of the bodyIDs used with linkers.

            while (bodyIDs.next()) {
                int currentID = bodyIDs.getInt(1);
                //now we have the ID of the body, and all the IDs from the linker, so just loop through
                //and remove the body if it is not needed
                boolean isBeingUsed = false;
                for (int bodyIDgiven : bodyIDLinkerArray) {
                    if (bodyIDgiven == currentID) {
                        isBeingUsed = true;
                    }
                }

                if (!isBeingUsed) {
                    //now we need to remove the body with the current ID
                    makeQuery("delete from body where bodyID = " + currentID + ";");
                }
            }

            //after doing all this, I need to change the autoIncrement angle for the bodies so that I don't get random
            //gaps between bodyIDs

            //to do that, I need to set the autoIncrement to the highest remaining bodyID:
            int highestUsedId = getHighestID("body", "bodyID");
            makeQuery("alter table body AUTO_INCREMENT = " + (highestUsedId + 1));
            //unfortunately for the highest used systemID I can't just find it without using other commands, and I don't
            //want to give this user more commands than possible, so I need to loop through all the systems and find the number

            int noSystems = noOfEntries("system");
            int autoIncrementValueSystem = noSystems + 1;
            //if there were 2 systems, I want the autoIncrement to be set to 3
            //length of systems is going to be 2
            makeQuery("alter table system AUTO_INCREMENT = " + autoIncrementValueSystem);
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }

    private static int getHighestID(String table, String IDcolumnName){
        int id = -1;
        try {
            ResultSet highest = makeQuery("SELECT "+IDcolumnName+" FROM "+table+" ORDER BY "+IDcolumnName+" DESC LIMIT 0, 1");
            highest.next();
            id = highest.getInt(1);
        } catch (SQLException e) {
            //throw new RuntimeException("line 283 MariaDBConnector getting highest ID error: " + e);

        }
        return id;
    }

    public static void closeConnection(){
        try {
            connection.close();
        }
        catch (Exception e){
            //probably connection isn't open
        }
    }

    public static int addNewSystem() {
        //actually make the new system
        makeQuery("insert into system (name) values (\"new system\");");
        //get the ID of that

        int idToReturn = getHighestID("system","systemID");
        return idToReturn;
    }

    public static void deleteBodyFromSystem(int bodyID,int sysID, int posID, int velID) {
        //so we need to delete the position, velocity and linker
        //delete from the linker first so that no elements are relying on it.
        //i could uniquely identify the link with the pos and vel IDs, but this isnt very robust so i use
        //the body and system
        makeQuery("delete from linker where bodyID = "+bodyID+" and systemID = "+sysID+" and posID = "+posID+" and velID = "+velID+";");
        makeQuery("delete from velocity where velID = "+velID+";");
        makeQuery("delete from position where posID = "+posID+";");
        //then change the autoIncrement:
        makeQuery("alter table velocity auto_increment = "+getHighestID("velocity","velID")+1+";");
        makeQuery("alter table position auto_increment = "+getHighestID("position","posID")+1+";");
        //now remove the rest of the bodies that aren't needed
        removeUnusedBodies();
    }

    public static void updateVelocity(int velID, double velX, double velY, double velZ) throws Exception{
        makeQuery("update velocity set velX = "+velX+", velY = "+velY+", velZ = "+velZ+" where velID = "+velID+";");
    }
    public static void updatePosition(int posID, double posX, double posY, double posZ) throws Exception{
        makeQuery("update position set posX = "+posX+", posY = "+posY+", posZ = "+posZ+" where posID = "+posID+";");
    }

    public static void updateAllPosAndVel(int bodyID, double posX, double posY, double posZ,double velX, double velY, double velZ) throws  Exception{
        makeQuery("update position,velocity, linker set velX = "+velX+", velY = "+velY+", " +
                "velZ = "+velZ+", posX = "+posX+", posY = "+posY+", posZ = "+posZ+" where linker.posID = position.posID" +
                " and linker.velID = velocity.velID and linker.bodyID = "+bodyID+"; ");
    }

    public static void updateBody(int bodyID, double mass, double radius, double illumination, String name, String type){
        makeQuery("update body set mass = "+mass+", radius = "+radius+", illumination = "+illumination+", name = \""+name+"\", " +
                "type = \""+type+"\" where bodyID = "+bodyID+";");
    }

    public static void editInstanceOfBody(int posID, int velID, int bodyID, int systemID, double mass,
                                          double radius, double illumination, String name, String type)
    {
        int newBodyID = getHighestID("body","bodyID") + 1;
        //make a new body with these settings
        makeQuery("insert into body values ("+newBodyID+",\""+name+"\","+mass+","+radius+","+illumination+",\""+type+"\");");
        //delete the linker which linked the old body to the system
        makeQuery("delete from linker where bodyID = "+bodyID+" and systemID = "+systemID+" and posID = "+posID+" and velID = "+ velID +";");
        //add a new linker which links the new body to the system with the same velocity and position
        makeQuery("insert into linker values ("+newBodyID+","+systemID+","+posID+","+velID+");");
        //update the SelectedBody field in CreatorDataStorage
        CreatorDataStorage.setBodyID(newBodyID);
    }
}
