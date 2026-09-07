package ca.ucalgary.seng300;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

//Controller for the game lobby
public class Game_lobby_controller {
    @FXML
    private Button Start_game_button;

    @FXML
    private Label user_one_name;

    @FXML
    private Label user_two_name;

    @FXML
    //used to set username
    public void initialize(){
        user_one_name.setText("test_user_1");
        //player name
        user_two_name.setText("test_user_2");
        //opponent name
        //add these later with database
    }

    @FXML
    public void handleStartGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca/ucalgary/seng300/LoadingScreenTTT.fxml"));
        Stage stage = (Stage) Start_game_button.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(loader.load(), 600, 400));
        stage.show();
    }
}
