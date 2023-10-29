package com.example.nea;

import Database.MariaDBConnector;
import Simulate.Simulator;
import Simulate.Vector3D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignificanceValueController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button resimulateButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField oldMassField;
    @FXML
    private TextField sigValueField;
    @FXML
    private TextField newMassField;
    @FXML
    private TextField interloperStartPosField;


    /*
    public void showValues(double oldMass, double sigVal, double newMass){
        oldMassField.setText(String.valueOf(oldMass));
        sigValueField.setText(String.valueOf(sigVal));
        newMassField.setText(String.valueOf(newMass));
    }

     */



    public void resimulatePressed(ActionEvent event) throws IOException{
        //update interloper
        Simulator.updateInterloperMass();
        //rerun simulation
        Simulator.restart();

        root = FXMLLoader.load(getClass().getResource("3DBodySimulator.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void exitPressed(){
        //close application
        MariaDBConnector.closeConnection();
        javafx.application.Platform.exit();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        oldMassField.setText(String.valueOf(Simulator.getInterloper().getMass()));
        sigValueField.setText(String.valueOf(Simulator.getInterloperSignificance()));
        newMassField.setText(String.valueOf(Simulator.getProposedNewMass()));
        double[] pos = Simulator.getInterloper().getPosition().getAllComponents();
        String posString = "("+returnsf(pos[0],3,0) + ", "+returnsf(pos[1],3,0)+", "+returnsf(pos[2],3,0)+")";
        interloperStartPosField.setText(posString);
    }

    /*
    private String returnsf(double num, int sf){
        String strNum = String.valueOf(num);
        int exponent = 0;

        String ref = "";
        if(num >= 10) {
            //find the order
            while(num >= 10){
                num /= 10;
                exponent++;
            }
        }
        else{
            while(num < 1){
                num *= 10;
                exponent--;
            }
        }
        for(int counter = 1; counter < sf;counter++){
            ref += strNum.toCharArray()[counter-1];
            if(counter == 1){
                ref += ".";
            }
        }
        ref += "e"+exponent;
        return ref;
    }
     */
    private static String returnsf(double num, int sf, int exponent){
        if(Math.abs(num) >= 10){
            String ans = returnsf(num/10, sf, exponent+1);
            return ans;
        }
        else if(Math.abs(num) < 1){
            String ans = returnsf(num*10, sf, exponent-1);
            return ans;
        }
        else{
            return Math.round(num * Math.pow(10,sf-1))/Math.pow(10,sf-1)+"e"+exponent;
        }
    }
}
