package com.example.nea;

import Database.MariaDBConnector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddExistingBodyController implements Initializable {
    @FXML
    private ListView<DataStore> SelectBody;

    private int idOfSelectedItem;
    private int idOfSystemToEdit = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this would fetch all the Systems and then format them as so:
        //ID    name
//         exactly the same as in SimulatorSystemSelectController.
        //updateView();
        SelectBody.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observableValue, DataStore s, DataStore t1) {
                //this is run whenever the user selects a different system.
                //get the item, get its ID, put it in the correct location.
                idOfSelectedItem = SelectBody.getSelectionModel().getSelectedItem().getIds()[0];
            }
        });
    }

    @FXML
    public void addExisting(ActionEvent e){
        if(idOfSelectedItem != -1) {
            //if have selected a body:
            //so add the selected item to this database
            //the problem is that this means adding a new Linker entity
            //which requires an associated position and velocity.
            //These fields will be different to the previously found ones
            //so we need to send them to yet another screen where they can edit these positions and
            //velocities
        }
    }

    @FXML
    public void addNew(ActionEvent e){
        //so we need to send them
    }

    public void gettingSystem(int sysID){
        idOfSystemToEdit = sysID;
        updateView();
    }

    private void updateView(){
        SelectBody.getSelectionModel().clearSelection();
        SelectBody.getItems().clear();
        //except this gets the bodies of that system
        System.out.println("getting bodies ln 67 AddExistingBodyController");
        DataStore[] bodies = MariaDBConnector.getAllBodies();
        for(DataStore body : bodies){
            System.out.println("line 59 CreatorEditorController: " +body);
            SelectBody.getItems().add(body);
        }
        if(bodies.length == 0){
            //then there are no bodies.
            //this is sort of a problem because the listview now is just white
            //there isn't much of a fix that is possible due to engine limitations
            //so I suppose I can't do much about it

            //this also means that the user has managed to delete every existing body - I suppose that isn't entirely a bad thing but it's quite surprising.
        }
    }
}
