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
import java.time.Duration;
import java.time.LocalDateTime;

public class HelloApplication extends Application {

    private static final double everythingMultiplier = 1e-5f;

    private static final int screenWidth = 1400;        //  setting the width of the window
    private static final int screenHeight = 1000;       //  setting the height of the window

    private static final double mouseSens = 0.1f;
    private static double camSpeed = 1e6*everythingMultiplier;
    private static double dtMultiplier = 1;

    private static boolean following = false;
    private Body followBody;
    private double lastTime;

    private PerspectiveCamera cam;
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
        camLocalZpos = -1e8*everythingMultiplier;

        //set all translations relative to this body
        translateCam();
    }

    public void runThing(Stage primaryStage) throws IOException{

        bodies = Simulator.getBodies();
        Group group = new Group();

        ArrayList<Sphere> spheres = new ArrayList<>();
        for(Body body : bodies){
            Sphere sphere = new Sphere(0);
            sphere.setRadius(body.getRadius()*everythingMultiplier);
         //   sphere.translateXProperty().set(screenWidth/2);             //  moving the sphere to the centre of the screen
         //   sphere.translateYProperty().set(screenHeight/2);
            sphere.translateXProperty().set(body.getPosition().getComponent(0)*everythingMultiplier);
            sphere.translateYProperty().set(body.getPosition().getComponent(1)*everythingMultiplier*-1);
            sphere.translateZProperty().set(body.getPosition().getComponent(2)*everythingMultiplier);
            System.out.println("added sphere at "+body.getPosition().getComponent(0)+ " " + body.getPosition().getComponent(1)*-1 + " "+ body.getPosition().getComponent(2));
            group.getChildren().add(sphere);

            spheres.add(sphere);
        }

        //TEMP
        followBody = bodies.get(0);
        //Sphere sphere = new Sphere(200);                        //      generating a sphere

        Scene scene = new Scene(group,screenWidth,screenHeight, true);    //  making the scene object so that we can actually make a new window


        cam = new PerspectiveCamera(true);                       //  instantiating a camera with the correct properties
        cam.setNearClip(0.01f);
        cam.setFarClip(1e100);
        cam.setFieldOfView(100);

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

                double nanoTime = Duration.between(LocalDateTime.MIN, LocalDateTime.now()).getNano(); //gets the nanoseconds part of the time between now and the epoch
                double deltaTime = nanoTime - lastTime;
                if(deltaTime < 0){
                    deltaTime += 1e9; //so if it goes from say 0.9 secs to 0.1, the dt will be
                    //          0.1-0.9 = -0.8
                    //          so we add 1 to it to get 0.2 which is the true time elapsed
                    //          that is unless more than a second has passed, in which case the simulation is running
                    //          too slowly anyway, so is not a valuable thing to consider
                }

                lastTime = nanoTime;

                deltaTime *= dtMultiplier / 1e9;
                System.out.println(deltaTime);

                Simulator.updateBodies(deltaTime);
                // now we need to update the spheres
                int counter =0;
                for(Sphere sphere : spheres){
                    Vector3D bodyPos = bodies.get(counter).getPosition();
                   // System.out.println(bodyPos);
                    sphere.setTranslateX(bodyPos.getComponent(0)*everythingMultiplier);
                    sphere.setTranslateY(bodyPos.getComponent(1)*-everythingMultiplier);
                    sphere.setTranslateZ(bodyPos.getComponent(2)*everythingMultiplier);
                    counter++;
                }

                translateCam();


               // System.out.println("velocity = " + bodies.get(0).getVelocity());
               // System.out.println("position = " + bodies.get(0).getPosition());
            }
        };
        timer.start();



        primaryStage.setTitle("Simulator 3D Window");           //  naming our scene and building it
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
            followPos = Vector3D.multiply(followBody.getPosition(),everythingMultiplier);


        }
        cam.setTranslateX(followPos.getComponent(0) + camLocalXpos);
        cam.setTranslateY(-followPos.getComponent(1) + camLocalYpos);
        cam.setTranslateZ(followPos.getComponent(2) + camLocalZpos);
    }
}
