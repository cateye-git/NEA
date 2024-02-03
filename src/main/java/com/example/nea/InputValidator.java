package com.example.nea;

public class InputValidator {

    public static boolean validateString(String in){
        boolean valid = true;
        if(in.contains("\"") || in.contains("'") || in.contains("\\")){
            valid = false;
        }
        return valid;
    }
    public static boolean validateStringExcludePeriod(String in){
        boolean valid = true;
        if(in.contains("\"") || in.contains("'") || in.contains("\\") || in.contains(".")){
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
    public static boolean validateDoubleMin0(String in){
        boolean valid = true;
        try{
            double num = Double.valueOf(in);
            if(num < 0){
                valid = false;
            }
        }
        catch (Exception e){
            valid = false;
        }
        return valid;
    }

}
