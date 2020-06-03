package gui;
import app.mainMemory;
import gui.log;

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
    
        //Initializate the Memory with the chips
        mainMemory sisMem = new mainMemory();
        sisMem.start();

        Thread thread = new Thread(() -> {
            while(true){
                //Render Cache L1 
                Platform.runLater(() -> controller.populateTable(controller.L1P00, sisMem.getChip0().getCore0().getL1()));
                Platform.runLater(() -> controller.populateTable(controller.L1P01, sisMem.getChip0().getCore1().getL1()));
                Platform.runLater(() -> controller.populateTable(controller.L1P10, sisMem.getChip1().getCore0().getL1()));
                Platform.runLater(() -> controller.populateTable(controller.L1P11, sisMem.getChip1().getCore1().getL1()));

                //Render Cache L2
                Platform.runLater(() -> controller.populateTable(controller.L2P0, sisMem.getChip0().getL2()));
                Platform.runLater(() -> controller.populateTable(controller.L2P1, sisMem.getChip1().getL2()));

                //Render Main Memory
                Platform.runLater(() -> controller.populateTable(controller.MEM_TABLE, sisMem.getMemory()));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Fail in Render Thread:" + e.getMessage());
                }
            }
          });
          thread.setDaemon(true);
          thread.start();
    }

   /**
     * ----------------------------------------------RUN MAIN APPLICATION WITH THE INTERFACE-------------------------------------------------------------------
     */

    public static void main(String[] args) {
        Application.launch(args);
    }

}