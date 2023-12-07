package com.example.nea;

import Simulate.Body;
import Simulate.Simulator;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class _3DBodySimulatorController implements Initializable {

    @FXML
    private ComboBox<Body> selectBody;

    @FXML
    private Slider dtSlider;
    @FXML
    private Slider camSpeedSlider;
    @FXML
    private ProgressBar percentage;
    private AnimationTimer timer;

    Simulator3DClass Simulation;


    public void closeSim(ActionEvent event) throws Exception {
        //Simulator.endSimulation();
        Simulation.stopAll();
        timer.stop();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { //called when the scene is initialised
        Stage stage = new Stage();
        Simulation = new Simulator3DClass();
        try {
            Simulation.main3D(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Body body : Simulator.getBodies()) {
            selectBody.getItems().add(body);
        }
        selectBody.setOnAction(this::selectBodyToFollow);
        selectBody.setOnMousePressed(this::updateBodies);
        selectBody.setValue(Simulator.getBodies().get(0));

        dtSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double dtValue = dtSlider.getValue();
                Simulation.changedtValue(dtValue);
            }
        });

        camSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double camSpeedValue = camSpeedSlider.getValue();
                Simulation.changeCamSpeedValue(camSpeedValue);
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long timeStamp) {
                if(Simulator.getQuitTime() == 0){
                    percentage.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                }
                else{
                    percentage.setProgress(Simulation.getTimeElapsed() / Simulator.getQuitTime());
                }
            };
        };
        timer.start();

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
        Simulation.getNewFollowPos(id);
    }
}
