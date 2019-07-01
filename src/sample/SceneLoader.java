package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneLoader {

    private static final SceneLoader instance = new SceneLoader();
    private Stage currentStage;

    private void SceneLoader() {};

    public void loadScene(String named, Stage stage) throws IOException {
        currentStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource(named));
        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        scene.getStylesheets().add("sample/sample.css");
        stage.setScene(scene);
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public static SceneLoader getInstance() {
        return instance;
    }
}
