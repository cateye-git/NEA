package com.example.nea;

import Simulate.FileOperations;
import Simulate.Simulator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SimulatorSystemSelectController implements Initializable {


    @FXML
    private ListView<String> SelectSystemForSim;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private int currentlySelectedItem = -1;

    public void onMainMenuClick(ActionEvent event) throws IOException {
        //return to the main menu
        root = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void onSelectButtonCLicked(ActionEvent event) throws IOException{
        //not currently linked to that button yet btw!
        if(currentlySelectedItem != -1){
            //so if user has actually selected an item:
            //send System selected to the correct place

            //open interloper select
            FileOperations fileOps = new FileOperations();
            Simulator.setFileOps(fileOps);
            Simulator.startUp(currentlySelectedItem);

            root = FXMLLoader.load(getClass().getResource("InterloperTypeSelect.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this would fetch all the Systems and then format them as so:
        //ID    name

        SelectSystemForSim.getItems().add("1\tmarsMoons");
        SelectSystemForSim.getItems().add("2\tmercuryMoons");
        SelectSystemForSim.getItems().add("3\tearthMoonOrbit");

        SelectSystemForSim.getItems().add("4\tmarsMoons");
        SelectSystemForSim.getItems().add("5\tmercuryMoons");
        SelectSystemForSim.getItems().add("6\tearthMoonOrbit");
        SelectSystemForSim.getItems().add("7\tmarsMoons");
        SelectSystemForSim.getItems().add("8\tmercuryMoons");
        SelectSystemForSim.getItems().add("9\tearthMoonOrbit");
        SelectSystemForSim.getItems().add("10\tmarsMoons");
        SelectSystemForSim.getItems().add("11\tmercuryMoons");
        SelectSystemForSim.getItems().add("12\tearthMoonOrbit");
        SelectSystemForSim.getItems().add("13\tmarsMoons");
        SelectSystemForSim.getItems().add("14\tmercuryMoons");
        SelectSystemForSim.getItems().add("15\tearthMoonOrbit");

        SelectSystemForSim.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //this means that whenever an item is selected it gets the index of that item in the list
                //which just happens to be the SystemID. How convenient!
                currentlySelectedItem = SelectSystemForSim.getSelectionModel().getSelectedIndex();
            }
        });
    }
}
