package com.example.nea;

import Simulate.Simulator;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InterloperTypeSelectController {

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    private Simulator simulator;

    private Stage stage;
    private Scene scene;
    private Parent root;



    public void onRandomInterloperClick(ActionEvent event) throws IOException {
        simulator.setRandomInterloper();
        com.example.nea.FXMLLoader.changeInExistingWindow(event,"3DBodySimulator.fxml");
        /*
        root = FXMLLoader.load(getClass().getResource("3DBodySimulator.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */
    }
    public void onSpecifiedInterloperCLick(ActionEvent event) throws IOException {

        com.example.nea.FXMLLoader.changeInExistingWindow(event,"SpecifyInterloper.fxml");
        /*
        root = FXMLLoader.load(getClass().getResource("SpecifyInterloper.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */
    }
    public void onNoInterloperCLick(ActionEvent event) throws IOException {
        Simulator.noInterloper();

        com.example.nea.FXMLLoader.changeInExistingWindow(event,"3DBodySimulator.fxml");
        /*
        root = FXMLLoader.load(getClass().getResource("3DBodySimulator.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

         */
    }
}
