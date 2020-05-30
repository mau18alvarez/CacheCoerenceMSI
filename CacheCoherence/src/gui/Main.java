package gui;

import app.chip;
import app.core;

import java.io.InputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
           
        primaryStage.setTitle("CacheCoherence");
        primaryStage.setResizable(false);

        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });

        //ADD FXML files -> SceneBuilder
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("../fxml/mainScene.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        MainSceneController controller = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    

        chip cpu0 = new chip(0);
        //chip cpu1 = new chip(1);
        cpu0.start();
        //cpu1.start(); 
        
        Thread thread = new Thread(() -> {
            while(true){
              try {
                  Thread.sleep(500);
              } catch (InterruptedException exc) {
                  throw new Error("Unexpected interruption", exc);
              }
              Platform.runLater(() -> controller.populateTable(controller.L1P00,cpu0.getCore0().getL1()));
              //Platform.runLater(() -> controller.printMatrix(controller.L1P01,cpu0.getCore1().getL1()));
              //Platform.runLater(() -> controller.printMatrix(controller.L1P10,cpu1.getCore0().getL1()));
              //Platform.runLater(() -> controller.printMatrix(controller.L1P11,cpu1.getCore1().getL1()));
              
              
            }
          });
          thread.setDaemon(true);
          thread.start();
    }
    

    public static void main(String[] args) {
        // Here you can work with args - command line parameters
        Application.launch(args);
    }
}