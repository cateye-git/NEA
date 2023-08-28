package com.example.nea;

import Simulate.Body;
import Simulate.Simulator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class _3DBodySimulatorController implements Initializable {

    @FXML
    private ComboBox<Body> selectBody;

    HelloApplication hi;

    public void closeSim(ActionEvent event) throws Exception {
        Simulator.endSimulation();
        hi.stopAll();
    }


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
        int id;
        try {
            id = selectBody.getValue().getSimulationID();
        }catch (Exception e){
            // cannot get the value because the ID does not exist.
            id = Simulator.getBodies().get(0).getSimulationID();
            //so get the first body.
        }
        //Simulator.getBodies().indexOf(body);
        hi.getNewFollowPos(id);
    }
}
