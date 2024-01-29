package com.example.nea;

public class CreatorDataStorage {
    public static int getSystemID() {
        return systemID;
    }
    public static void setSystemID(int systemID) {
        CreatorDataStorage.systemID = systemID;
    }
    public static int getBodyID() {
        return bodyID;
    }
    public static void setBodyID(int bodyID) {
        System.out.println("body ID is: "+bodyID);
        CreatorDataStorage.bodyID = bodyID;
    }
    public static int getPosID() {
        System.out.println("pos ID is: "+posID);
        return posID;
    }
    public static void setPosID(int posID) {
        CreatorDataStorage.posID = posID;
    }
    public static int getVelID() {
        System.out.println("vel ID is: "+velID);
        return velID;
    }
    public static void setVelID(int velID) {
        CreatorDataStorage.velID = velID;
    }

    private static int systemID = -1;
    private static int bodyID = -1;
    private static int posID = -1;
    private static int velID = -1;


}
