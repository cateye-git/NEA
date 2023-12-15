package com.example.nea;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FXMLLoader {
    private final static String CSS = "/menus.css";



    public static void changeInExistingWindow(ActionEvent event, String fileName) throws IOException {
        Parent root = javafx.fxml.FXMLLoader.load(FXMLLoader.class.getResource(fileName));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FXMLLoader.class.getResource(CSS).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    //this class needs to be instantiated because you can't run some of the methods in it otherwise.
    public Object changeInExistingWindowReturnController(ActionEvent event, String fileName) throws IOException{
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fileName));
        //Parent root = javafx.fxml.FXMLLoader.load(FXMLLoader.class.getResource(fileName));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FXMLLoader.class.getResource(CSS).toExternalForm());
        stage.setScene(scene);
        stage.show();

        return loader.getController();

        //Class test = scene.getClass();
        //System.out.println(test);
    }
}
