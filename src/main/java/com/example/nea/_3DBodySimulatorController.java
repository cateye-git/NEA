package com.example.nea;

import Simulate.Body;
import Simulate.FileOperations;
import Simulate.Simulator;
import Simulate.Vector3D;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class _3DBodySimulatorController implements Initializable {

    @FXML
    private ComboBox<Body> selectBody;

    public void closeSim(ActionEvent event){
        Simulator.endSimulation();
    }

    HelloApplication hi;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){ //called when the scene is initialised
        Stage stage = new Stage();
        hi = new HelloApplication();
        try {
            hi.runThing(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(Body body : Simulator.getBodies()){
            selectBody.getItems().add(body);
        }
        selectBody.setOnAction(this::selectBodyToFollow);
        selectBody.setOnMousePressed(this::updateBodies);
        selectBody.setValue(Simulator.getBodies().get(0));
    }

    private void updateBodies(MouseEvent mouseEvent) {
        selectBody.getItems().remove(0,selectBody.getItems().size()); //remove all elements
        for(Body body : Simulator.getBodies()){
            selectBody.getItems().add(body);
        }
    }

    private void selectBodyToFollow(ActionEvent event){
        //update items in selection box
        int id = selectBody.getValue().getSimulationID();
        //Simulator.getBodies().indexOf(body);
        hi.getNewFollowPos(id);
    }
}
