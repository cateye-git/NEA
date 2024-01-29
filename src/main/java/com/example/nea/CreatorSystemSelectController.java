package com.example.nea;

import Database.MariaDBConnector;
import Interfaces.CRUDInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatorSystemSelectController implements Initializable, CRUDInterface {

    @FXML
    private ListView<DataStore> SelectSystemForSim;

    private final String editorName = "CreatorEditor.fxml";
    @FXML
    private Label errorLabel;
    private int currentlySelectedItem = -1;

    public void onMainMenuClick(ActionEvent event) throws IOException {
        //return to the main menu
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"mainMenuView.fxml");
    }

    public void addNew(ActionEvent event){
        //make a new system with name "unnamed", and send to the editor with that system
        int id = MariaDBConnector.addNewSystem();
        CreatorDataStorage.setSystemID(id);
        //send to editor with that ID
        try {
            com.example.nea.FXMLLoader.changeInExistingWindow(event,editorName);
        } catch (IOException e) {
            throw new RuntimeException("there was a problem with switching to the editor: "+e);
        }
    }

    public void editSelected(ActionEvent event){
        if(currentlySelectedItem == -1){
            //user has not yet selected a system
            errorLabel.setText("please select a system");
        }
        else{
            //send to editor with ID of the one selected
            try {
                CreatorDataStorage.setSystemID(currentlySelectedItem);
                com.example.nea.FXMLLoader.changeInExistingWindow(event,editorName);
            } catch (IOException e) {
                throw new RuntimeException("there was a problem with loading that: "+e);
            }
        }

    }
    public void copySelected(ActionEvent event){
        if(currentlySelectedItem == -1){
            //user has not yet selected a system
            errorLabel.setText("please select a system");
        }
        else{
            //copy the selected system and all associated bodies in the database
            MariaDBConnector.copySystem(currentlySelectedItem);
            updateView();
        }
    }
    public void deleteSelected(ActionEvent event){
        if(currentlySelectedItem == -1){
            //user has not yet selected a system
            errorLabel.setText("please select a system");
        }
        else{
            //delete the selected system and all associated bodies in the database
            MariaDBConnector.deleteSystem(currentlySelectedItem);
            updateView();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //this will fetch all the Systems and then format them as so:
        //ID    name
//         exactly the same as in SimulatorSystemSelectController.
        updateView();
        SelectSystemForSim.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observable, DataStore oldValue, DataStore newValue) {
                //run when the system selected has changed

                //so I need to get the IDs for the systems, which is made easy with my DataStore component

                currentlySelectedItem = SelectSystemForSim.getSelectionModel().getSelectedItem().getIds()[0];
            }
        });
    }

    private void updateView(){
        SelectSystemForSim.getSelectionModel().clearSelection();
        SelectSystemForSim.getItems().clear();
        DataStore[] systems = getEntities();
        for(DataStore system : systems){
            SelectSystemForSim.getItems().add(system);
        }
    }
    public DataStore[] getEntities(){
        DataStore[] entities = MariaDBConnector.getSystems();
        return entities;
    }
}
