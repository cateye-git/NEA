package com.example.nea;

import Database.MariaDBConnector;
import Simulate.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddNewBodyController implements Initializable {
    @FXML
    private TextField posX;
    @FXML
    private TextField posY;
    @FXML
    private TextField posZ;

    @FXML
    private TextField velX;
    @FXML
    private TextField velY;
    @FXML
    private TextField velZ;

    @FXML
    private TextField mass;
    @FXML
    private TextField radius;
    @FXML
    private TextField name;
    @FXML
    private TextField illumination;
    @FXML
    private ChoiceBox<String> type;

    private final String[] types = {"body","planet","star"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //add all of the types if body to types
        type.getItems().addAll(types);
        type.getSelectionModel().selectFirst();
    }

    @FXML
    public void finishSpecifyingClick(ActionEvent event){

        try {
            String typeString = type.getValue();
            Vector3D position = new Vector3D(Double.valueOf(posX.getText()), Double.valueOf(posY.getText()), Double.valueOf(posZ.getText()));
            Vector3D velocity = new Vector3D(Double.valueOf(velX.getText()), Double.valueOf(velY.getText()), Double.valueOf(velZ.getText()));

            //now I need to add this new body to the bodies
           // System.out.println("line 60 AddNewBody adding the new body: ");
            MariaDBConnector.addNewBodyToSystem(name.getText(), Double.valueOf(mass.getText()), Double.valueOf(radius.getText()),
                    Double.valueOf(illumination.getText()), typeString, position, velocity, CreatorDataStorage.getSystemID());

            //and finally send the user back to the editor
            FXMLLoader.changeInExistingWindow(event, "CreatorEditor.fxml");
        }
        catch (Exception ex){
            System.out.println("error line 67 AddNewBody (probably an illegitimate input): "+ex);
        }
    }
}
