
package com.example.nea;

import Database.MariaDBConnector;
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
    private ListView<DataStore> SelectSystemForSim;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private int currentlySelectedItem = -1;

    public void onMainMenuClick(ActionEvent event) throws IOException {
        //return to the main
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"mainMenuView.fxml");
        /*
        root = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
         */
    }

    public void onSelectButtonCLicked(ActionEvent event) throws IOException{
        if(currentlySelectedItem != -1){
            //so if user has actually selected an item:
            //send System selected to the correct place

            //Make a new instantiation of the fileOperations class for the simulator
            FileOperations fileOps = new FileOperations();
            //give it to the simulator
            Simulator.setFileOps(fileOps);
            //start the simulator with the currently selected system (without an interloper for now).


            //load the menu for selecting interlopers
            com.example.nea.FXMLLoader.changeInExistingWindow(event, "InterloperTypeSelect.fxml");
            Simulator.startUp(currentlySelectedItem,false,stage);
            /*
            root = FXMLLoader.load(getClass().getResource("InterloperTypeSelect.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            //add the CSS stylesheet
            scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
            stage.setScene(scene);
            Simulator.startUp(currentlySelectedItem, false, stage);
            //set our Stage to this new scene
            stage.show();
            */

        }
        //if the currentlySelectedItem = -1, the default value, then
        //the user hasn't selected a system so do nothing.
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this fetches all the Systems and formats them

        DataStore[] systems = MariaDBConnector.getSystems();
        //result is in form: systemID(int) name(String)
        for(DataStore system : systems){
            SelectSystemForSim.getItems().add(system);
        }


        SelectSystemForSim.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observableValue, DataStore s, DataStore t1) {
                //this means that whenever an item is selected it gets the index of that item in the list
                //which just happens to be the SystemID. How convenient!
                //currentlySelectedItem = SelectSystemForSim.getSelectionModel().getSelectedIndex() + 1;

                //get the ID from the string of the selection
                currentlySelectedItem = SelectSystemForSim.getSelectionModel().getSelectedItem().getIds()[0];
            }
        });
    }
}

