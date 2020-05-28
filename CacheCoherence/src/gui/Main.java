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
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
           
        primaryStage.setTitle("CacheCoherence");
        primaryStage.setResizable(false);

        //ADD FXML files -> SceneBuilder
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("../fxml/mainScene.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        MainSceneController controller = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    
        //Label helloWorldLabel = new Label("Hello world!");
        //helloWorldLabel.setAlignment(Pos.CENTER);
        //Scene primaryScene = new Scene(helloWorldLabel);
        //primaryStage.setScene(primaryScene);

        chip cpu1 = new chip(1);
        //chip cpu2 = new chip(2);
        cpu1.start();
        //cpu2.start();     
    }
    

    public static void main(String[] args) {
        // Here you can work with args - command line parameters
        Application.launch(args);
    }
}