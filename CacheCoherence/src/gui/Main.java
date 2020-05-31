package gui;

import app.chip;
import app.mainMemory;
import app.memoryLine;

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
    
        //Initializate the CHIPS
        chip chip0 = new chip(0);
        chip chip1 = new chip(1);
        chip0.start();
        chip1.start(); 
        
        Thread thread = new Thread(() -> {
            while(true){
              try {
                  Thread.sleep(500);
              } catch (InterruptedException exc) {
                  throw new Error("Unexpected interruption", exc);
              }
              Platform.runLater(() -> controller.populateTable(controller.L1P00, chip0.getCore0().getL1()));
              Platform.runLater(() -> controller.populateTable(controller.L1P01, chip0.getCore1().getL1()));
              Platform.runLater(() -> controller.populateTable(controller.L1P10, chip1.getCore0().getL1()));
              Platform.runLater(() -> controller.populateTable(controller.L1P11, chip1.getCore1().getL1()));
            //Platform.runLater(() -> controller.populateTable(controller.LwP11, cpu1.getCore1().getL1()));


              //Platform.runLater(() -> controller.populateTable(controller.principalMem, mainMemory.getRenderMem()));
              
              
            }
          });
          thread.setDaemon(true);
          thread.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}