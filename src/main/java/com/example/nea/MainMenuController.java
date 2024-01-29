package com.example.nea;

import Database.MariaDBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    public void onSimulatorButtonClick(ActionEvent event) throws IOException {
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"SimulatorSystemSelect.fxml");
    }
    public void onEditorButtonClick(ActionEvent event) throws IOException {
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"CreatorSystemSelect.fxml");
    }

    public void onExitButtonClick(ActionEvent event) throws  IOException{
        MariaDBConnector.closeConnection();
        javafx.application.Platform.exit();
    }
}
