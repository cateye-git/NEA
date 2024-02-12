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

    /*
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button resimulateButton;
     */
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

    public void resimulatePressed(ActionEvent event) throws IOException{
        //update interloper

        System.out.println("wzzzzzzzzzzzzzzzzzz" + Simulator.getBodies());
        Simulator.updateInterloperMass();
        //rerun simulation
        Simulator.restart();

        com.example.nea.FXMLLoader.changeInExistingWindow(event,"3DBodySimulator.fxml");
        Simulator.ensureBodiesHasBeenReset();
    }

    public void exitPressed(){
        //close application
        MariaDBConnector.closeConnection();
        javafx.application.Platform.exit();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("wefuahfaiefuqhiefqew" + Simulator.getBodies());
        oldMassField.setText(String.valueOf(Simulator.getInterloper().getMass()));
        sigValueField.setText(String.valueOf(Simulator.getInterloperSignificance()));
        newMassField.setText(String.valueOf(Simulator.getProposedNewMass()));
        double[] pos = Simulator.getInterloper().getPosition().getAllComponents();
        String posString = "("+returnsf(pos[0],3,0) + ", "+returnsf(pos[1],3,0)+", "+returnsf(pos[2],3,0)+")";
        interloperStartPosField.setText(posString);
        System.out.println("8q9ry2347298t72" + Simulator.getBodies());
    }


    public static String returnsf(double num, int sf, int exponent){
        try {
            if (Math.abs(num) >= 10) {
                String ans = returnsf(num / 10, sf, exponent + 1);
                return ans;
            } else if (Math.abs(num) < 1) {
                String ans = returnsf(num * 10, sf, exponent - 1);
                return ans;
            } else {
                return Math.round(num * Math.pow(10, sf - 1)) / Math.pow(10, sf - 1) + "e" + exponent;
            }
        }
        catch (StackOverflowError e){
            return String.valueOf(num);
        }
    }
   // private statis String returnsf(double num, int sf, int exponent)
}
