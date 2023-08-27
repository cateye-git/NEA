package Simulate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileOperations {
    private FileWriter writer;  // a variable with the scope of the object which is used by several methods

    // this is a class which will be instantiated by the PlanetSystem class at runtime to handle outputting to CSV files

    public void openOutputFileHandle(String fileName){
        if(fileName.contains(".")){
            //  cannot contain . because that could lead to errors where the filename has ended
            throw new RuntimeException("filename cannot contain '.'");
        }
        else if(writer != null){
            // cannot open a file handle if one already exists
            throw new RuntimeException("file handle already exists");
        }
        else{
            try{// has to be in a try catch statement for Java to allow it
                writer = new FileWriter(fileName);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }   // tries to open a fileWriter to the file with the name given

    public void closeOutputFileHandle(){
        if(writer == null){
            throw new RuntimeException("file handle doesnt exist");// have to open one first
        }
        else{
            try {
                writer.close();//   again, has to be in a try catch clause so that Java will allow it
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }// tries to close the fileWriter

    private void writeLineOfFile(String[] data){
        String whatToWrite = "";
        for(String item : data){
            whatToWrite += item + ".";// puts full stop between different data items
        }
        try {
            writer.write(whatToWrite);// tries to write it to the file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }// takes in an array of data and writes it to the file

    public void writeSnapshot(double t, ArrayList<Body> bodies){
        String[] data = new String[1 + bodies.size()];
        data[0] = "t="+t+".";// starts with the time of the snapshot
        int counter = 1;
        for(Body body : bodies){// looks through all bodies
            data[counter] = body.convertToCSVEntry()+".";// each body has a method which converts its values to CSV
            counter++;// so the subroutine adds the output of these methods with '.'s in between
        }
        writeLineOfFile(data);// writes the result
    }// gets all values needed in CSV form from all bodies and writes to the file

    public void writeFirstLine(boolean hasInterloper, int endTime, int systemID, String sysName){
        String ref = "";
        if(hasInterloper) {
            ref = "WITHOUTINTERLOPER,";
        }
        else{
            ref = "INTERLOPER,";//make clear to user what simulation this was by converting from true/false
        }
        ref += sysName + "," + systemID + "," + endTime;// adds the rest of the needed data to the file
        try {// tries to write it
            writer.write(ref);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }// writes the first line of the file which contains data about the simulation
}