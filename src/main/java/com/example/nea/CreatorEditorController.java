package com.example.nea;

import Database.MariaDBConnector;
import Interfaces.CRUDInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatorEditorController implements Initializable, CRUDInterface {
    @FXML
    private ListView<DataStore> SelectBody;
    private int[] idsOfSelectedItem = {-1,-1,-1}; //bodyID, posID, velID
    private int idOfSystemToEdit;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this would fetch all the Systems and then format them as so:
        //ID    name
//         exactly the same as in SimulatorSystemSelectController.
        //updateView();
        gettingSystem();
        SelectBody.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observableValue, DataStore s, DataStore t1) {
                //this is run whenever the user selects a different system.

                //so now I need to change the currently selected system to that which has been selected,
                //because of my DataStore class this is really easy because I can literally just fetch it

                DataStore selectedItem = SelectBody.getSelectionModel().getSelectedItem();
                idsOfSelectedItem = selectedItem.getIds();
            }
        });

    }
    /*
    public void gettingSystem(int sysID){
        idOfSystemToEdit = sysID;
        updateView();
    }
     */
    public void gettingSystem(){
        idOfSystemToEdit = CreatorDataStorage.getSystemID();
        updateView();
    }

    //this is the same as in CreatorSystemSelectController.
    private void updateView(){
        SelectBody.getSelectionModel().clearSelection();
        SelectBody.getItems().clear();
        //except this gets the bodies of that system
        System.out.println("getting bodies ln 57 creatorEditor");
        DataStore[] bodies = getEntities();
        int noBodies = 0;
        for(DataStore bodyDets : bodies){
            System.out.println("line 59 CreatorEditorController: " +bodyDets);
            //bodyDetID in form: bodyID,
            SelectBody.getItems().add(bodyDets);
            noBodies++;
        }
        if(noBodies == 0){
            //then there are no bodies in the system that we are looking at.
            //this is sort of a problem because the listview now is just white
            //there isn't much of a fix that is possible due to engine limitations
            //so I suppose I can't do much about it
        }
    }

    public DataStore[] getEntities(){
        return MariaDBConnector.getBodyDataFromSystem(idOfSystemToEdit);
    }

    @FXML
    public void deleteSelected(ActionEvent e){
        if(checkCurrentlySelectedNeg() == false) {
            //delete the linkage between the Body and the system as well as the position and velocity
            MariaDBConnector.deleteBodyFromSystem(idsOfSelectedItem[0], idOfSystemToEdit, idsOfSelectedItem[1], idsOfSelectedItem[2]);
            //then delete any bodies that have no connections
        }
    }
    @FXML
    public void copySelected(ActionEvent e){
        //if selecting something
        if(checkCurrentlySelectedNeg() == false){
            //then make new position and velocity, and link the body to the system with that velocity
            MariaDBConnector.copySystemBodyLink(idsOfSelectedItem[0],idOfSystemToEdit,idsOfSelectedItem[1],idsOfSelectedItem[2]);
        }
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
