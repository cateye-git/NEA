package com.example.nea;

public class InputValidator {

    public static boolean validateString(String in){
        boolean valid = true;
        if(in.contains("\"") || in.contains("'") || in.contains("\\")){
            valid = false;
        }
        return valid;
    }
    public static boolean validateDouble(String in){
        boolean valid = true;
        try{
            Double.valueOf(in);
        }
        catch (Exception e){
            valid = false;
        }
        return valid;
    }
}
