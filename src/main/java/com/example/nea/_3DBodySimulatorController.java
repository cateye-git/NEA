package com.example.nea;

import Simulate.Body;
import Simulate.Simulator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class _3DBodySimulatorController implements Initializable {

    @FXML
    private ComboBox<Body> selectBody;

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
    }

    public void selectBodyToFollow(ActionEvent event){
        Body body = selectBody.getValue();
        //Simulator.getBodies().indexOf(body);
        hi.getNewFollowPos(body);
    }
}
