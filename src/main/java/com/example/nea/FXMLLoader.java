package com.example.nea;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class FXMLLoader {
    private final static String CSS = "/menus.css";
/*
public class FXMLLoader {
    private final String CSS = "/menus.css";

    public static void changeInExistingWindow(ActionEvent event, String fileName) throws IOException {
        Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fileName));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
 */


    public static void changeInExistingWindow(ActionEvent event, String fileName) throws IOException {
        Parent root = javafx.fxml.FXMLLoader.load(FXMLLoader.class.getResource(fileName));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FXMLLoader.class.getResource(CSS).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}