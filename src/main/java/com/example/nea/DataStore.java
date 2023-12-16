package com.example.nea;

public class DataStore {
    private int[] ids;
    private String dataToStore;

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public void setDataToStore(String dataToStore) {
        this.dataToStore = dataToStore;
    }

    @Override
    public String toString(){
        return dataToStore;
    }

    public DataStore(int id, String dataToStore){
        this.ids = new int[1];
        this.ids[0] = id;

        this.dataToStore = dataToStore;
    }

    public DataStore(int[] id, String dataToStore){
        this.ids = id;

        this.dataToStore = dataToStore;
    }
}
