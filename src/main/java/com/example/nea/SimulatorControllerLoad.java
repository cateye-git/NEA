package com.example.nea;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



public class SimulatorControllerLoad {

    private Stage stage;

    public SimulatorControllerLoad(Stage stage){
        System.out.println(stage);
        this.stage = stage;
    }

    public void load(String name, String title)
    {
        //System.out.println("1");
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource(name));
        Scene scene = null;
        //System.out.println("2");
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (Exception e) {
            System.out.println("something went wrong in SimulatorControllerLoad: "+e);
        }

        //System.out.println("3");
        //String css = getClass().getResource("menus.css").toExternalForm();
        //scene.getStylesheets().add(css);
        scene.getStylesheets().add(getClass().getResource("/menus.css").toExternalForm());

        System.out.println("loading a stage SimulatorControllerLoad line 37");
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
