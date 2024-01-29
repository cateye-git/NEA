package com.example.nea;

import Database.MariaDBConnector;
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
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());

        stage.setTitle("Celestial Body Simulator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("opening connection");
        MariaDBConnector.openConnection();
        launch();

    }
}
