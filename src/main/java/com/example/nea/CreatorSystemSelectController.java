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

    private Stage stage;
    private Scene scene;
    private Parent root;
    private final FXMLLoader loader = new FXMLLoader();
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
        //send to editor with that ID

        try {
            //com.example.nea.FXMLLoader.changeInExistingWindow(event,"CreatorEditor.fxml");
            Object classObject = loader.changeInExistingWindowReturnController(event, editorName);

        //    CreatorEditorController controller = (CreatorEditorController) classObject.getClass().cast(classObject);
          //  controller.gettingSystem(id);
        } catch (IOException e) {
            throw new RuntimeException("there was a problem with loading that: "+e);
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
                //com.example.nea.FXMLLoader.changeInExistingWindow(event,"CreatorEditor.fxml");
                CreatorDataStorage.setSystemID(currentlySelectedItem);
                Object classObject = loader.changeInExistingWindowReturnController(event, editorName);

                //CreatorEditorController controller = (CreatorEditorController) classObject.getClass().cast(classObject);
                //controller.gettingSystem(currentlySelectedItem);
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
            System.out.println("copying id "+currentlySelectedItem);
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
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this would fetch all the Systems and then format them as so:
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
/*
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //this is run whenever the user selects a different system.

                //so now I need to change the currently selected system to that which has been selected,
                //so I need the selected system ID
                //the text is in the form "ID name" so I need to split by spaces and grab
                //all of the stuff which is before the first space
                //then convert it to an integer.
                String selectedItemString = "-1 failure";
                try{
                 //selectedItemString = SelectSystemForSim.getSelectionModel().getSelectedItem();}
             //   catch (Exception e){
             //   }
                String[] stringParts = selectedItemString.split(" ", 2);
                int idOfSelected = Integer.valueOf(stringParts[0]);
                currentlySelectedItem = idOfSelected;


            }
                */
        });
    }

    private void updateView(){
        SelectSystemForSim.getSelectionModel().clearSelection();
        SelectSystemForSim.getItems().clear();
        DataStore[] systems = getEntities();
        for(DataStore system : systems){
            System.out.println("line 114 CreatorSelectSystem: " +system);
            SelectSystemForSim.getItems().add(system);
        }
    }
    public DataStore[] getEntities(){
        DataStore[] entities = MariaDBConnector.getSystems();
        return entities;
    }
}
