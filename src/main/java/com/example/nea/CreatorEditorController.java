package com.example.nea;

import Database.MariaDBConnector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class CreatorEditorController implements Initializable {
    @FXML
    private ListView<String> selectBody;
    private int idOfSelectedItem;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //this would fetch all the Systems and then format them as so:
        //ID    name
//         exactly the same as in SimulatorSystemSelectController.
        updateView();
        selectBody.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //this is run whenever the user selects a different system.

                //so now I need to change the currently selected system to that which has been selected,
                //so I need the selected system ID
                //the text is in the form "ID name" so I need to split by spaces and grab
                //all of the stuff which is before the first space
                //then convert it to an integer.

                String selectedItemString = selectBody.getSelectionModel().getSelectedItem();
                String[] stringParts = selectedItemString.split(" ", 2);
                idOfSelectedItem = Integer.valueOf(stringParts[0]);
            }
        });
    }

    //this is the same as in CreatorSystemSelectController.
    private void updateView(){
        selectBody.getSelectionModel().clearSelection();
        selectBody.getItems().clear();
        String[] systems = MariaDBConnector.getSystems();
        for(String system : systems){
            System.out.println("line 114 CreatorSelectSystem: " +system);
            selectBody.getItems().add(system);
        }
    }

    @FXML
    public void deleteSelected(ActionEvent e){

    }
}
