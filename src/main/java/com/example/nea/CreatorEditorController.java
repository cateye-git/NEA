package com.example.nea;

import Database.MariaDBConnector;
import Interfaces.CRUDInterface;
import Simulate.Body;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreatorEditorController implements Initializable, CRUDInterface {
    @FXML
    private ListView<String> SelectBody;
    private int idOfSelectedItem = -1;
    private int idOfSystemToEdit;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this would fetch all the Systems and then format them as so:
        //ID    name
//         exactly the same as in SimulatorSystemSelectController.
        //updateView();
        SelectBody.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //this is run whenever the user selects a different system.

                //so now I need to change the currently selected system to that which has been selected,
                //so I need the selected system ID
                //the text is in the form "ID name" so I need to split by spaces and grab
                //all of the stuff which is before the first space
                //then convert it to an integer.

                String selectedItemString = SelectBody.getSelectionModel().getSelectedItem();
                String[] stringParts = selectedItemString.split(" ", 2);
                idOfSelectedItem = Integer.valueOf(stringParts[0]);
            }
        });

    }
    public void gettingSystem(int sysID){
        idOfSystemToEdit = sysID;
        updateView();
    }

    //this is the same as in CreatorSystemSelectController.
    private void updateView(){
        SelectBody.getSelectionModel().clearSelection();
        SelectBody.getItems().clear();
        //except this gets the bodies of that system
        System.out.println("getting bodies ln 57 creatorEditor");
        String[] bodies = getEntities();
        int noBodies = 0;
        for(String str : bodies){
            System.out.println("line 59 CreatorEditorController: " +str);
            SelectBody.getItems().add(str);
            noBodies++;
        }
        if(noBodies == 0){
            //then there are no bodies in the system that we are looking at.
            //this is sort of a problem because the listview now is just white
            //there isn't much of a fix that is possible due to engine limitations
            //so I suppose I can't do much about it
        }
    }

    public String[] getEntities(){
        return MariaDBConnector.getBodyNamesAndIdsFromSystem(idOfSystemToEdit);
    }

    @FXML
    public void deleteSelected(ActionEvent e){
        System.out.println("test");
    }
    @FXML
    public void copySelected(ActionEvent e){

    }
    @FXML
    private void onMainMenuClick(ActionEvent e){
        try {
            FXMLLoader.changeInExistingWindow(e, "mainMenuView.fxml");
        } catch (IOException ex) {
            throw new RuntimeException("there was a problem with returning to the menu from EditorController ln 69: " + ex);
        }
    }
    @FXML
    public void editSelected(ActionEvent e){

    }
    @FXML
    public void addNew(ActionEvent e){
        //send the user to the AddExistingBodyController page
        //I will also need to give the page my ID so that it can return it to me when it loads me back up
    }
}
