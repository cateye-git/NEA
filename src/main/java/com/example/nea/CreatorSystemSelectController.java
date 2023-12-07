package com.example.nea;

import Database.MariaDBConnector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class CreatorSystemSelectController implements Initializable {

    @FXML
    private ListView<String> SelectSystemForSim;

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label errorLabel;
    private int currentlySelectedItem = -1;

    public void onMainMenuClick(ActionEvent event) throws IOException {
        //return to the main menu
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"mainMenuView.fxml");
        /*
        Parent root = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */
    }

    public void addNew(ActionEvent event){
        //send to the editor with an ID of -1 meaning a new system
    }
    public void editSelected(ActionEvent event) throws IOException{
        if(currentlySelectedItem == -1){
            //user has not yet selected a system
            errorLabel.setText("please select a system");
        }
        else{
            //send to editor with ID of the one selected
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
        /*
        String[] systems = MariaDBConnector.getSystems();
        //result is in form: systemID(int) name(String)
        for(String system : systems){
            SelectSystemForSim.getItems().add(system);
        }
         */

        updateView();

        SelectSystemForSim.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //this means that whenever an item is selected it gets the index of that item in the list
                //which just happens to be the SystemID - 1. How convenient!
                currentlySelectedItem = SelectSystemForSim.getSelectionModel().getSelectedIndex()+1;
            }
        });
    }

    private void updateView(){

        String[] systems = MariaDBConnector.getSystems();
        for(String system : systems){
            System.out.println("line 114 CreatorSelectSystem: " +system);
            SelectSystemForSim.getItems().add(system);
        }
        SelectSystemForSim.getItems().removeAll();
    }
}
