package ca.ucalgary.seng300;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsToggleManager {
    /**
     * initialize the settings changer stage
     * set the MainApp as the owner so that when MainApp is closed, settingsChanger will also close
     * */
    private static final Stage settingsStage = new Stage();
    public SettingsToggleManager(Stage root) {
        settingsStage.initOwner(root);
    }

    /**
     * @param type - string of the thing user wants to change (username, password, email)
     * */
    public static void toggleSettings(String type) {
        if (!settingsStage.isShowing()) {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("settingsChangeScreens.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), 237,265);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            SettingsChangeController controller = fxmlLoader.getController();
            controller.setMode(type);
            settingsStage.setTitle("Change " + type);
            settingsStage.setScene(scene);
            settingsStage.setResizable(false); // set as non-resizable
            settingsStage.show();
        }
        // if the MainApp is closed, close all extra windows
        else if (!settingsStage.getOwner().isShowing()) {
            settingsStage.close();
        }
    }

    /**
     * check if a settings is open
     * mostly for closing the window if MainApp closes
     * */
    public static boolean isOpen() {
        return settingsStage.isShowing();
    }

    /**
     * close the settings if the user logs out
     * called by logout method in MainPage, Profile, Settings, and Leaderboard
     * */
    public static void closeSettings() {
        settingsStage.close();
    }
}
