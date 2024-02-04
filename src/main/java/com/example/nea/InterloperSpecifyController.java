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
  //  private Stage stage;
   // private Scene scene;
   // private Parent root;

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
        if(InputValidator.validateDouble(posX.getText()) &&InputValidator.validateDouble(posY.getText()) &&InputValidator.validateDouble(posZ.getText()) &&
                InputValidator.validateDouble(velX.getText()) &&InputValidator.validateDouble(velY.getText()) &&InputValidator.validateDouble(velZ.getText()) &&
                InputValidator.validateDoubleMin0(mass.getText()) &&InputValidator.validateDoubleMin0(radius.getText()) &&InputValidator.validateString(name.getText())
         && !InputValidator.isDouble0(mass.getText()))
        {
            Vector3D position = new Vector3D(Double.valueOf(posX.getText()), Double.valueOf(posY.getText()), Double.valueOf(posZ.getText()));
            Vector3D velocity = new Vector3D(Double.valueOf(velX.getText()), Double.valueOf(velY.getText()), Double.valueOf(velZ.getText()));
            Body body = new Simulate.Body(position, velocity, name.getText(), Double.valueOf(mass.getText()), Double.valueOf(radius.getText()), false);

            Simulator.setInterloper(body);

            com.example.nea.FXMLLoader.changeInExistingWindow(event, "3DBodySimulator.fxml");
        }
    }
}
