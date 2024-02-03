package com.example.nea;

import Database.MariaDBConnector;
import Interfaces.CRUDInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatorEditorController implements Initializable, CRUDInterface {
    @FXML
    private ListView<DataStore> SelectBody;
    @FXML
    private TextField nameField;
    private int[] idsOfSelectedItem = {-1,-1,-1}; //bodyID, posID, velID
    private int idOfSystemToEdit;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //fetch all of the systems
        gettingSystem();
        SelectBody.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observableValue, DataStore s, DataStore t1) {
                //this is run whenever the user selects a different system.

                //so now I need to change the currently selected system to that which has been selected,
                //because of my DataStore class this is really easy because I can literally just fetch it

                DataStore selectedItem = SelectBody.getSelectionModel().getSelectedItem();
                if(selectedItem == null){
                    selectedItem = SelectBody.getItems().get(0);
                }
                idsOfSelectedItem = selectedItem.getIds();
                CreatorDataStorage.setBodyID(idsOfSelectedItem[0]);
                CreatorDataStorage.setPosID(idsOfSelectedItem[1]);
                CreatorDataStorage.setVelID(idsOfSelectedItem[2]);
            }
        });

    }
    public void gettingSystem(){
        idOfSystemToEdit = CreatorDataStorage.getSystemID();
        updateView();
    }
    //this is the same as in CreatorSystemSelectController.
    private void updateView(){
        SelectBody.getSelectionModel().clearSelection();
        SelectBody.getItems().clear();
        //except this gets the bodies of that system
        DataStore[] bodies = getEntities();
        for(DataStore bodyDets : bodies){
            //bodyDetID in form: bodyID,
            SelectBody.getItems().add(bodyDets);
        }
    }
    public DataStore[] getEntities(){
        return MariaDBConnector.getBodyDataFromSystem(idOfSystemToEdit);
    }

    @FXML
    private void updateName(ActionEvent e){
        if(InputValidator.validateString(nameField.getText())) {
            MariaDBConnector.updateSysName(idOfSystemToEdit, nameField.getText());
        }
    }


    @FXML
    public void deleteSelected(ActionEvent e){
        if(checkCurrentlySelectedNeg() == false) {
            //delete the linkage between the Body and the system as well as the position and velocity
            MariaDBConnector.deleteBodyFromSystem(idsOfSelectedItem[0], idOfSystemToEdit, idsOfSelectedItem[1], idsOfSelectedItem[2]);
            //then delete any bodies that have no connections
            //this is now done internally by the connector
        }
        updateView();
    }
    @FXML
    public void copySelected(ActionEvent e){
        //if selecting something
        if(checkCurrentlySelectedNeg() == false){
            //then make new position and velocity, and link the body to the system with that velocity
            MariaDBConnector.copySystemBodyLink(idsOfSelectedItem[0],idOfSystemToEdit,idsOfSelectedItem[1],idsOfSelectedItem[2]);
        }
        updateView();
    }

    private boolean checkCurrentlySelectedNeg(){
        boolean isNeg = false;
        for(int id : idsOfSelectedItem){
            if(id == -1){
                isNeg = true;
            }
        }
        return isNeg;
    }
    @FXML
    private void onMainMenuClick(ActionEvent e){
        try {
            FXMLLoader.changeInExistingWindow(e, "mainMenuView.fxml");
        } catch (IOException ex) {
            throw new RuntimeException("there was a problem with returning to the menu from EditorController ln 105: " + ex);
        }
    }
    @FXML
    public void editSelected(ActionEvent e){
//check that we have selected something:
        if(checkCurrentlySelectedNeg() == false){
            //then send the user to the Body Editor
            try {
                FXMLLoader.changeInExistingWindow(e,"EditBody.fxml");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @FXML
    public void addNew(ActionEvent e){
        //send the user to the AddExistingBodyController page
        //I will also need to give the page my ID so that it can return it to me when it loads me back up
            try {
                FXMLLoader.changeInExistingWindow(e, "AddExistingBody.fxml");

               // AddExistingBodyController controller = AddExistingBodyController.class.cast(classObj);
               // controller.gettingSystem(idOfSystemToEdit);
            } catch (Exception ex) {
                throw new RuntimeException("there was a problem with loading the controller at ln 122 CreatorEditorController: "+ex);
            }

    }
}
