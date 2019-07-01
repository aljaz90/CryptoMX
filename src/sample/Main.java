package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
        primaryStage.setTitle("Crypto FX");
        Scene scene = new Scene(root, 800,500);
        scene.getStylesheets().add("sample/sample.css");

        primaryStage.setScene(scene);
        //root.setStyle("-fx-background-color: transparent");
        //primaryStage.initStyle(StageStyle.DECORATED.UNDECORATED);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
