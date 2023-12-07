package com.example.nea;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;



    public void onSimulatorButtonClick(ActionEvent event) throws IOException {
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"SimulatorSystemSelect.fxml");
        /*
        System.out.println("sim click");
        root = FXMLLoader.load(getClass().getResource("SimulatorSystemSelect.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */
    }
    public void onEditorButtonClick(ActionEvent event) throws IOException {
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"CreatorSystemSelect.fxml");
        /*
        System.out.println("editor click");
        root = FXMLLoader.load(getClass().getResource("CreatorSystemSelect.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */
    }
}
