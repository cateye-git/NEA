package com.example.nea;

import Simulate.Body;
import Simulate.Simulator;
import Simulate.Vector3D;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {

    private static final int screenWidth = 1400;        //  setting the width of the window
    private static final int screenHeight = 1000;       //  setting the height of the window

    private static final double mouseSens = 0.1f;

    private double timeElapsed = 0;

    private static long time;
    private static double camSpeed = 1e6;
    private static double dtMultiplier = 100;

    private static boolean following = false;
    private Body followBody;

    private Camera cam;
    private double camLocalXpos = 0;
    private double camLocalYpos = 0;
    private double camLocalZpos = 0;

    private static double mouseX;
    private static double mouseY;


    private static ArrayList<Body> bodies;

    public void getNewFollowPos(int id){

        //find body with this ID

        for(Body body : bodies){
            if(body.getSimulationID() == id){
                //then we have found the correct ID
                followBody = body;
            }
        }

        following = true;
        //reset all translations
        camLocalXpos = 0;
        camLocalYpos = 0;
        camLocalZpos = -1e8;

        //set all translations relative to this body
        translateCam();
    }

    public void runThing(Stage primaryStage) throws IOException{

        bodies = Simulator.getBodies();
        Group group = new Group();

        ArrayList<Sphere> spheres = new ArrayList<>();
        for(Body body : bodies){
            Sphere sphere = new Sphere(200);
            sphere.setRadius(body.getRadius());
         //   sphere.translateXProperty().set(screenWidth/2);             //  moving the sphere to the centre of the screen
         //   sphere.translateYProperty().set(screenHeight/2);
            sphere.translateXProperty().set(body.getPosition().getComponent(0));
            sphere.translateYProperty().set(body.getPosition().getComponent(1)*-1);
            sphere.translateZProperty().set(body.getPosition().getComponent(2));
            System.out.println("added sphere at "+body.getPosition().getComponent(0)+ " " + body.getPosition().getComponent(1)*-1 + " "+ body.getPosition().getComponent(2));
            group.getChildren().add(sphere);

            spheres.add(sphere);
        }

        //TEMP
        followBody = bodies.get(0);
        //Sphere sphere = new Sphere(200);                        //      generating a sphere

        Scene scene = new Scene(group,screenWidth,screenHeight, true);    //  making the scene object so that we can actually make a new window


        cam = new PerspectiveCamera();                       //  instantiating a camera with the correct properties
        cam.setNearClip(0.01f);
        cam.setFarClip(1e100);
      //  cam.translateZProperty().set(cam.getTranslateZ() -100);     //  moving the camera back
        scene.setFill(Paint.valueOf("black"));

        scene.setCamera(cam);                                       //  giving our scene the camera

        //camera rotation with mouse; needs listener for scene

        scene.setOnMousePressed(mouseEvent ->{
                    mouseX = mouseEvent.getSceneX();
                    mouseY = mouseEvent.getSceneY();
                }
        );
        scene.setOnMouseDragged(mouseEvent ->{
                    rotateCam(cam, (mouseEvent.getSceneX() - mouseX)*mouseSens, 0,-1,0);
                    rotateCam(cam, (mouseEvent.getSceneY() - mouseY)*mouseSens, 1,0,0);

                    mouseX = mouseEvent.getSceneX();
                    mouseY = mouseEvent.getSceneY();
                }
                );

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {        //      handling the inputs to allow our user to move the camera
            switch (keyEvent.getCode()) {
                case W:
                   camLocalZpos += camSpeed;
                //   translateCam();
                    break;
                case S:
                    camLocalZpos -= camSpeed;
                //    translateCam();
                    break;

                case A:
                   camLocalXpos -= camSpeed;
                //    translateCam();
                    break;
                case D:
                   camLocalXpos += camSpeed;
                 //   translateCam();
                    break;
                case Q:
                   camLocalYpos -= camSpeed;
                 //   translateCam();
                    break;
                case E:
                    camLocalYpos += camSpeed;
                    break;
                case M:
                    dtMultiplier *= 1.1f;
                    //   translateCam();
                    break;
                case N:
                    dtMultiplier *= 0.9f;
            }
        } );


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                double deltaTime = l - time;
               // System.out.println(deltaTime);
                //so that if too much time has elapsed (eg at start or when user suspends application) we do not do anything
              //  System.out.println("dt is: "+deltaTime);
                if((l - time)> 1e8) {
                    System.out.println("too much time has elaped");
                    time = l;
                    deltaTime = 0;
                }
                timeElapsed += deltaTime * dtMultiplier;
              //  System.out.println(timeElapsed);
                Simulator.updateBodies(deltaTime/1e7*dtMultiplier);
                time = l;
                // now we need to update the spheres
                int counter =0;
                for(Sphere sphere : spheres){
                    Vector3D bodyPos = bodies.get(counter).getPosition();
                    System.out.println(bodyPos);
                   // System.out.println(bodyPos);
                    sphere.setTranslateX(bodyPos.getComponent(0));
                    sphere.setTranslateY(bodyPos.getComponent(1));
                    sphere.setTranslateZ(bodyPos.getComponent(2));
                    counter++;
                }
                translateCam();


               // System.out.println("velocity = " + bodies.get(0).getVelocity());
               // System.out.println("position = " + bodies.get(0).getPosition());
            }
        };
        timer.start();



        primaryStage.setTitle("test sphere");           //  naming our scene and building it
        primaryStage.setScene(scene);
        primaryStage.show();
    }

  @Override
   public void start(Stage primaryStage) throws IOException {
       Stage stage = new Stage();
        runThing(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    private static void rotateCam(Camera cam, double amount, double x, double y, double z){
        Transform transform = new Rotate(amount,new Point3D(x,y,z));
        cam.getTransforms().add(transform);
    }

    private void translateCam(){// updates position of camera.
        Vector3D followPos = new Vector3D(0,0,0);
        if(following) {
            followPos = followBody.getPosition();
        }
        cam.setTranslateX(followPos.getComponent(0) + camLocalXpos);
        cam.setTranslateY(-followPos.getComponent(1) + camLocalYpos);
        cam.setTranslateZ(followPos.getComponent(2) + camLocalZpos);
    }
}
