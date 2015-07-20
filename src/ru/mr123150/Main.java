package ru.mr123150;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        Controller controller=loader.getController();

        double width=primaryScreenBounds.getWidth();
        double height=primaryScreenBounds.getHeight();

        Scene scene=new Scene(root, width, height);
        stage.setTitle("Canvas");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            controller.disconnect();
        });


        stage.show();

        controller.resizeCanvas(stage.getWidth(),stage.getHeight());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
