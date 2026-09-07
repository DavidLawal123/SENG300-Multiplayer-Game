package ca.ucalgary.seng300;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // call ChatToggleManager and SettingsToggleManager to intantialize MainApp as the owner of both stages
        // used to ensure that when MainApp closes, all extra windows close as well
        ChatToggleManager ctm = new ChatToggleManager(stage);
        SettingsToggleManager stm = new SettingsToggleManager(stage);

        try {
            Database.readFromFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/ca/ucalgary/seng300/welcome.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Game Platform");
        stage.setResizable(false);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
