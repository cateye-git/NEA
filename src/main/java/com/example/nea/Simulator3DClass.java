package com.example.nea;

import Simulate.Body;
import Simulate.Simulator;
import Simulate.Vector3D;
import javafx.animation.AnimationTimer;
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
import java.net.URL;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class Simulator3DClass {
    private AnimationTimer timer;
    private final double everythingMultiplier = 1e-5f;
    private double radiusMultiplier = 1;

    private final int screenWidth = 1400;        //  setting the width of the window
    private final int screenHeight = 1000;       //  setting the height of the window

    private final double fileWriteInterval = 1209600;
    private double timeElapsed = 0;
    private double timeElapsedSinceLastFileWrite = 0;



    private final double mouseSens = 0.1f;
    private double camSpeed = 1e6*everythingMultiplier;
    private double averageSize;
    private double dtMultiplier = 0;

    private boolean following = false;
    private Body followBody;
    private LocalDateTime lastTime = LocalDateTime.now();

    private PerspectiveCamera cam;
    private double camLocalXpos = 0;
    private double camLocalYpos = 0;
    private double camLocalZpos = 0;

    private double mouseX;
    private double mouseY;

    public double getTimeElapsed(){
        return  timeElapsed;
    }


    private ArrayList<Body> bodies;
    private Stage myStage;


    public void stopAll(){

        //close this window for the user
        //stop application from still running
      //  System.out.println("line 67 Simulator3DClass stopping all");
        timer.stop();
        Simulator.endSimulation(timeElapsed);
        myStage.close();
    }
    public void getNewFollowPos(int id){
        //System.out.println("new follow pos: "+id);

        //find body with this ID

        for(Body body : bodies){

            //   System.out.println("body: "+body + " id: " +body.getSimulationID());
            if(body.getSimulationID() == id){
                //then we have found the correct ID
                followBody = body;
            }
        }

        following = true;
        //reset all translations
        camLocalXpos = 0;
        camLocalYpos = 0;
        camLocalZpos = -followBody.getRadius()*3*everythingMultiplier;


        //set all translations relative to this body
      //  translateCam();
    }

    public void main3D(Stage primaryStage) throws IOException{
        myStage = primaryStage;

        bodies = Simulator.getBodies();
        System.out.println(bodies);
        Group group = new Group();

        //import spheres as bodies
        ArrayList<Sphere> spheres = new ArrayList<>();
        double sumOfSizes = 0;
        double noBodies = 0;
        for(Body body : bodies){
            Sphere sphere = new Sphere(0);
            sphere.setRadius(body.getRadius()*everythingMultiplier);
            sphere.translateXProperty().set(body.getPosition().getComponent(0)*everythingMultiplier);
            sphere.translateYProperty().set(body.getPosition().getComponent(1)*everythingMultiplier*-1);
            sphere.translateZProperty().set(body.getPosition().getComponent(2)*everythingMultiplier);
            //System.out.println("added sphere at "+body.getPosition().getComponent(0)+ " " + body.getPosition().getComponent(1)*-1 + " "+ body.getPosition().getComponent(2));
            group.getChildren().add(sphere);
            sumOfSizes+= body.getRadius()*everythingMultiplier;
            noBodies++;
            spheres.add(sphere);
        }
        averageSize = sumOfSizes/noBodies;

        followBody = bodies.get(0);

        Scene scene = new Scene(group,screenWidth,screenHeight, true);    //  making the scene object so that we can actually make a new window


        cam = new PerspectiveCamera(true);                       //  instantiating a camera with the correct properties
        cam.setNearClip(0.01f);
        cam.setFarClip(1e100);
        cam.setFieldOfView(90);
       // cam.setDe

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

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {        //      handling the inputs to allow the user to move the camera
            switch (keyEvent.getCode()) {
                case W:
                   camLocalZpos += camSpeed;
                    break;
                case S:
                    camLocalZpos -= camSpeed;
                    break;
                case A:
                   camLocalXpos -= camSpeed;
                    break;
                case D:
                   camLocalXpos += camSpeed;
                    break;
                case Q:
                   camLocalYpos -= camSpeed;
                    break;
                case E:
                    camLocalYpos += camSpeed;
                    break;
                case M:
                    dtMultiplier *= 1.1f;
                    break;
                case N:
                    dtMultiplier *= 0.9f;
            }
        } );


        timer = new AnimationTimer() {
            @Override
            public void handle(long timeStamp) {

                double nanoTime = Duration.between(lastTime, LocalDateTime.now()).getNano(); //gets the nanoseconds part of the time between now and last time

                lastTime = LocalDateTime.now();
                nanoTime *= dtMultiplier / 1e9;
                timeElapsed += nanoTime;

                if(timeElapsed >= Simulator.getQuitTime() && Simulator.getStageOfRunning() == "runningWithInterloper"){
                    stopAll();
                }
                timeElapsedSinceLastFileWrite += nanoTime;

                Simulator.updateBodies(nanoTime, timeElapsed);

                if(timeElapsedSinceLastFileWrite >= fileWriteInterval){
                    while(timeElapsedSinceLastFileWrite >= fileWriteInterval) {
                        timeElapsedSinceLastFileWrite -= fileWriteInterval;
                    }
                    Simulator.writeSnapshot(timeElapsed);
                }

                //if there's a discrepancy between no bodies and no spheres, remove all and then add new
                //this is inefficient but because of the way I have set it up, it is very hard to effectively
                //find the correct Sphere to remove
                if(bodies.size() != spheres.size()){
                    while(spheres.size() > 0){
                        group.getChildren().remove(spheres.get((0)));
                        spheres.remove(0);
                    }
                    int counter = 0;
                    for(Body body: bodies){
                        spheres.add(new Sphere(body.getRadius() * 10));
                        group.getChildren().add(spheres.get(counter));
                        counter++;
                    }
                }
                //System.out.println(followBody);
                //update remaining spheres
                int counter =0;
                try{
                for(Body body : bodies){
                    Vector3D bodyPos = body.getPosition();
                    Sphere sphere = spheres.get(counter);

                    sphere.setRadius(body.getRadius()*everythingMultiplier * radiusMultiplier);
                    sphere.setTranslateX(bodyPos.getComponent(0)*everythingMultiplier);
                    sphere.setTranslateY(bodyPos.getComponent(1)*-everythingMultiplier);
                    sphere.setTranslateZ(bodyPos.getComponent(2)*everythingMultiplier);
                    counter++;
                }
            }catch (Exception e){
                    System.out.println(bodies.size() + " " + spheres.size());
                    throw new RuntimeException("dicrepency when configuring spheres: " + e);
                }

                translateCam();
            }
        };
        timer.start();



        primaryStage.setTitle("Simulator 3D Window");           //  naming our scene and building it
        primaryStage.setScene(scene);
        primaryStage.show();
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

    public void changedtValue(double val){
        dtMultiplier = val;
    }
    public void changeCamSpeedValue(double val){
        camSpeed = val * averageSize / 10;
    }
    public void changeRadiusMultiplier(double val){
        radiusMultiplier = val;
    }
}