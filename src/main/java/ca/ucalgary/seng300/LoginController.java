package ca.ucalgary.seng300;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private Text toHome; // button to go to welcome page

    @FXML
    private Text loginToSignup; // button to go to signup page

    @FXML
    private Button login; // button to authenticate login

    @FXML
    private ImageView menuOpen; // open side panel

    @FXML
    private ImageView menuClose; // close side panel

    @FXML
    private TextField username; // username field

    @FXML
    private PasswordField password; // password field

    @FXML
    private Label incorrect; // error message

    /**
     * initially set the side panel to invisible
     * set error message to false
     * */
    @FXML
    void initialize() throws Exception {
        Database.readFromFile(); // read file to local database
        sidePane.setVisible(false);
        incorrect.setVisible(false);
    }

    /**
     * set visible when open
     * set invisible when closed
     * */
    @FXML
    void openSidePanel() {
        sidePane.setVisible(true);
    }
    @FXML
    void closeSidePanel() {
        sidePane.setVisible(false);
    }

    // hover functionality for menu button
    @FXML
    void menuHoverEnter() {
        menuOpen.setScaleX(1.1);
        menuOpen.setScaleY(1.1);
        menuClose.setScaleX(1.1);
        menuClose.setScaleY(1.1);
    }
    @FXML
    void menuHoverExit(){
        menuOpen.setScaleX(1.0);
        menuOpen.setScaleY(1.0);
        menuClose.setScaleX(1.0);
        menuClose.setScaleY(1.0);

    }

    /**
     * on click to go home, switch scenes to the welcome.fxml
     * */
    @FXML
    void onClickHome(){
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("welcome.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) toHome.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * on click to signup for an account instead, switch scenes to signup.fxml
     * */
    @FXML
    void switchLoginToSignup() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("signup.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) loginToSignup.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * on click to login,
     * if the username/email and password exist and match info in database then switch scenes to the mainpage.fxml
     * otherwise, show an error and prompt user to try again
     * */
    @FXML
    void onLoginClick() throws Exception {
        String user = username.getText().trim();
        String pass = password.getText();
        boolean authenticated = Database.authenticate(user,pass);

        if (authenticated){
            incorrect.setVisible(false);


            User u = Database.getUserFromUsername(user);
            SessionData.storeCurrentPlayer(u.getUUID());

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainpage.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), 600,400);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            Stage stage  = (Stage) login.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else {
            incorrect.setVisible(true);
        }
    }

    // hover functionality for login authenticate button
    @FXML
    void loginHoverEnter() {
        login.setScaleX(1.1);
        login.setScaleY(1.1);
    }
    @FXML
    void loginHoverExit(){
        login.setScaleX(1.0);
        login.setScaleY(1.0);
    }
}
