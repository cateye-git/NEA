package Database;

import java.sql.*;

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
            connection.close();
        }
        catch (Exception e){
            System.out.printf("error creating mariaDB connection: " + e);
        }
    }

    public static ResultSet makeQuery(String query){

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


    public static void closeConnection(){
        try {
            connection.close();
        }
        catch (Exception e){
            System.out.println("error closing connection: "+e);
        }
    }
}
