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
    private ListView<String> SelectBody;

    private int idOfSelectedItem;
    private int idOfSystemToEdit = -1;

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
        System.out.println("getting bodies ln 56 AddExistingBodyController");
        ArrayList<String> bodies = MariaDBConnector.getAllBodies();
        int noBodies = 0;
        for(String str : bodies){
            System.out.println("line 59 CreatorEditorController: " +str);
            SelectBody.getItems().add(str);
            noBodies++;
        }
        if(noBodies == 0){
            //then there are no bodies.
            //this is sort of a problem because the listview now is just white
            //there isn't much of a fix that is possible due to engine limitations
            //so I suppose I can't do much about it

            //this also means that the user has managed to delete every existing body - I suppose that isn't entirely a bad thing but it's quite surprising.
        }
    }
}
