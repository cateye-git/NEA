package com.example.nea;

public class CreatorDataStorage {

    public static int getSystemID() {
        return systemID;
    }

    public static void setSystemID(int systemID) {
        CreatorDataStorage.systemID = systemID;
    }

    private static int systemID = -1;


}
