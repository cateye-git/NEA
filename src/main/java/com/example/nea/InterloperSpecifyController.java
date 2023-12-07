package com.example.nea;

import Simulate.Body;
import Simulate.Simulator;
import Simulate.Vector3D;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class InterloperSpecifyController{
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField posX;
    @FXML
    private TextField posY;
    @FXML
    private TextField posZ;

    @FXML
    private TextField velX;
    @FXML
    private TextField velY;
    @FXML
    private TextField velZ;

    @FXML
    private TextField mass;
    @FXML
    private TextField radius;
    @FXML
    private TextField name;



    public void finishSpecifyingClick(ActionEvent event) throws IOException {
        Vector3D position = new Vector3D(Double.valueOf(posX.getText()),Double.valueOf(posY.getText()),Double.valueOf(posZ.getText()));
        Vector3D velocity = new Vector3D(Double.valueOf(velX.getText()),Double.valueOf(velY.getText()),Double.valueOf(velZ.getText()));
        Body body = new Simulate.Body(position,velocity,name.getText(),Double.valueOf(mass.getText()),Double.valueOf(radius.getText()),false);

        Simulator.setInterloper(body);

        com.example.nea.FXMLLoader.changeInExistingWindow(event,"3DBodySimulator.fxml");
        /*
        root = FXMLLoader.load(getClass().getResource("3DBodySimulator.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */

        //send info to set interloper as body made

        //convert what should be double into double
        //this is trickier than it seems because the user may type something like 3.67e17 meaning 3.67x10^17

        /*
        TextField[] convToDoubles = {posX,posY,posZ,velX,velY,velY,mass,radius};
        double[] results = new double[8];
        int counter = 0;
        for(TextField text: convToDoubles){
            String textStr = text.getText();
            if(textStr.contains("e")){
                // the user is trying to make a x10 to the
                int posOfE = textStr.indexOf("e");
                //everything left of that should be saved as a double, then multiplied by 10 the num to the right:
                double no = Double.valueOf(textStr.substring(0,posOfE));
                for(int i = 0;i< Double.valueOf(textStr.substring(posOfE+1));i++){
                    no = no * 10;
                }
                results[counter] = no;
            }
            else{
                results[counter] = Double.valueOf(text.getText());
            }
            counter++;
        }
        */


    }
}
