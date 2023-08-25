package com.example.nea;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("mainMenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        //String css = getClass().getResource("menus.css").toExternalForm();
        //scene.getStylesheets().add(css);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());
        //repeat for all scenes

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
