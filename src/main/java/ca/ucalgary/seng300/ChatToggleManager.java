package ca.ucalgary.seng300;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatToggleManager {
    /**
     * initialize the chat stage and an instance of the controller to be used across scenes
     * */
    private static final Stage stage = new Stage();
    private static ChatController cc; // store open chat

    /**
     * from MainApp, set the chat's owner as the MainApp
     * allows the window to close if the MainApp closes
     * */
    public ChatToggleManager(Stage root) {
        stage.initOwner(root);
    }

    /**
     * if the chat is closed, open a new stage
     * otherwise if the chat is open, close the stage
     * */
    public static void toggleChat() {
        stage.setResizable(false); // resizable as false
        if (!stage.isShowing()) {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("conversation.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(),200,230);
                cc = fxmlLoader.getController(); // store open chat
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("Chat");
            stage.setScene(scene);
            stage.show();
        }
        // if the MainApp is closed as well
        else if (stage.isShowing() || !stage.getOwner().isShowing()) {
            stage.close();
        }
    }

    /**
     * close the chat if the user logs out
     * called by logout method in MainPage, Profile, Settings, and Leaderboard
     * */
    public static void closeChat() {
        stage.close();
    }

    /**
     * get the stored chatController
     * mostly for sending messages once a game has been won
     * */
    public static ChatController getChatController() {
        return cc;
    }

    /**
     * check if a chat is already open
     * mostly for sending messages once a game as been won
     * */
    public static boolean isOpen() {
        return stage.isShowing();
    }
}
