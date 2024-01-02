package com.example.nea;

import Database.MariaDBConnector;
import Simulate.Body;
import Simulate.Planet;
import Simulate.Star;
import Simulate.Vector3D;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditBodyController implements Initializable {
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

    private final String editorName = "CreatorEditor.fxml";

    private final String[] types = {"body","planet","star"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //add all of the types if body to types
        type.getItems().addAll(types);
        //get the existing position and velocity and body details, and set the values of the fields to that
        try {
            System.out.println("line 53 EditBodyController getting body details: ");
            Body bodyDets = MariaDBConnector.getBody(CreatorDataStorage.getBodyID());
            System.out.println("line 53 EditBodyController getting pos details: ");
            Vector3D pos = MariaDBConnector.getPos(CreatorDataStorage.getPosID());
            System.out.println("line 53 EditBodyController getting vel details: ");
            Vector3D vel = MariaDBConnector.getVel(CreatorDataStorage.getVelID());

            posX.setText(String.valueOf(pos.getComponent(0)));
            posY.setText(String.valueOf(pos.getComponent(1)));
            posZ.setText(String.valueOf(pos.getComponent(2)));

            velX.setText(String.valueOf(vel.getComponent(0)));
            velY.setText(String.valueOf(vel.getComponent(1)));
            velZ.setText(String.valueOf(vel.getComponent(2)));

            mass.setText(String.valueOf(bodyDets.getMass()));
            radius.setText(String.valueOf(bodyDets.getRadius()));
            if(bodyDets instanceof Star){
                illumination.setText(String.valueOf(((Star) bodyDets).getIllumination()));
                type.getSelectionModel().select(types[2]);//I dont want to have to do this, but using a String may get it confused
                //as it may return that the string doesnt exist in the list
            }
            else if (bodyDets instanceof Planet){
                type.getSelectionModel().select(types[1]);
            }
            else{
                type.getSelectionModel().select(types[0]);
            }
            name.setText(bodyDets.getName());
        }catch (Exception e){
            throw new RuntimeException("error at ln 80 EditBodyController: "+e);
        }
    }

    @FXML
    public void justThisBodyClickBody(ActionEvent e){
        if(checkIfAnythingChanged()){
            try {
                //make a new body with these settings
                //delete the linker which linked the old body to the system
                //add a new linker which links the new body to the system with the same velocity and position
                //update the SelectedBody field in CreatorDataStorage

                MariaDBConnector.editInstanceOfBody(CreatorDataStorage.getPosID(),CreatorDataStorage.getVelID(), CreatorDataStorage.getBodyID(),
                        CreatorDataStorage.getSystemID(),Double.valueOf(mass.getText()), Double.valueOf(radius.getText()),
                        Double.valueOf(illumination.getText()), name.getText(), type.getValue());
            }catch (Exception ex){
                System.out.println("unchecked input ln 98 EditBodyController: "+ex);
            }
        }
    }
    @FXML
    public void allBodiesClickBody(ActionEvent e){
        if(checkIfAnythingChanged()){}
        //update the value in the Body table
        try {
            MariaDBConnector.updateBody(CreatorDataStorage.getBodyID(), Double.valueOf(mass.getText()), Double.valueOf(radius.getText()),
                    Double.valueOf(illumination.getText()), name.getText(), type.getValue());
        }
        catch (Exception ex){
            System.out.println("unchecked input ln 106 EditBodyController: "+ex);
        }
    }
    @FXML
    public void justThisBodyClickPosVel(ActionEvent e){
        if(checkIfAnythingChanged()){}
        //update the current position and velocity
        try {
            MariaDBConnector.updatePosition(CreatorDataStorage.getPosID(), Double.valueOf(posX.getText()),Double.valueOf(posY.getText()),
                    Double.valueOf(posZ.getText()));
            MariaDBConnector.updateVelocity(CreatorDataStorage.getVelID(), Double.valueOf(velX.getText()),Double.valueOf(velY.getText()),
                    Double.valueOf(velZ.getText()));
        } catch (Exception ex) {
            System.out.println("some unchecked input w user, line 109 EditBodyController: "+ex);
        }
    }
    @FXML
    public void allBodiesClickPosVel(ActionEvent e){
        if(checkIfAnythingChanged()){}
        //update every position and velocity which has a linker object attached to the body needed
        try {
            MariaDBConnector.updateAllPosAndVel(CreatorDataStorage.getBodyID(), Double.valueOf(posX.getText()),Double.valueOf(posY.getText()),
                    Double.valueOf(posZ.getText()),Double.valueOf(velX.getText()),Double.valueOf(velY.getText()), Double.valueOf(velZ.getText()));
        } catch (Exception ex) {
            System.out.println("some unchecked input w user, line 123 EditBodyController: "+ex);
        }
    }

    private boolean checkIfAnythingChanged(){
        boolean changed = false;
        try {
            System.out.println("line 53 EditBodyController getting body details: ");
            Body bodyDets = MariaDBConnector.getBody(CreatorDataStorage.getBodyID());
            System.out.println("line 53 EditBodyController getting pos details: ");
            Vector3D pos = MariaDBConnector.getPos(CreatorDataStorage.getPosID());
            System.out.println("line 53 EditBodyController getting vel details: ");
            Vector3D vel = MariaDBConnector.getVel(CreatorDataStorage.getVelID());

            if(Double.valueOf(posX.getText()) != pos.getComponent(0) ||Double.valueOf(posY.getText()) != pos.getComponent(1)
            || Double.valueOf(posZ.getText()) != pos.getComponent(2)){
                changed = true;
            }

            if(Double.valueOf(velX.getText()) != vel.getComponent(0) ||Double.valueOf(velY.getText()) != vel.getComponent(1)
                    || Double.valueOf(velZ.getText()) != vel.getComponent(2)){
                changed = true;
            }

            String oldBodyType = "body";
            double oldBodyIllumination = 0;
            if(bodyDets instanceof Star){
                oldBodyType = "star";
                oldBodyIllumination = ((Star) bodyDets).getIllumination();
            }
            else if (bodyDets instanceof Planet){
                oldBodyType = "planet";
            }
            if(!mass.getText().equals(bodyDets.getMass()) || !oldBodyType.equals(type.getValue()) ||
                    Double.valueOf(radius.getText()) !=bodyDets.getRadius() || oldBodyIllumination != Double.valueOf(illumination.getText())
            || !name.getText().equals(bodyDets.getName()))
            {
                changed = true;
            };

        }catch (Exception e){
            throw new RuntimeException("error at ln 144 EditBodyController: "+e);
        }
        return changed;
    }

    @FXML
    public void goBackToEditor(ActionEvent e){
        try {
            FXMLLoader.changeInExistingWindow(e, editorName);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
