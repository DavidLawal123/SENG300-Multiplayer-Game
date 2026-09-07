package ca.ucalgary.seng300;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button toLogin; // to login page button

    @FXML
    private Button toSignup; // to signup page button

    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private ImageView menuOpen; // menu button to open side panel

    @FXML
    private ImageView menuClose; // menu button to close side panel

    /**
     * initialize side panel to invisible
     * */
    @FXML
    void initialize() {
        sidePane.setVisible(false);
    }

    /**
     * on click to login, switch scenes to login.fxml
     * */
    @FXML
    void switchToLogin() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) toLogin.getScene().getWindow(); //gets the stage that the UI element "toLogin" is from
        stage.setScene(scene);
        stage.show();
    }

    // hover functionality for login button
    @FXML
    void loginHoverEnter() {
        toLogin.setScaleX(1.1);
        toLogin.setScaleY(1.1);
    }
    @FXML
    void loginHoverExit(){
        toLogin.setScaleX(1.0);
        toLogin.setScaleY(1.0);
    }

    /**
     * on click to signup, switch scenes to signup.fxml
     * */
    @FXML
    void switchToSignup() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("signup.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) toSignup.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // hover functionality for signup button
    @FXML
    void signupHoverEnter() {
        toSignup.setScaleX(1.1);
        toSignup.setScaleY(1.1);
    }
    @FXML
    void signupHoverExit(){
        toSignup.setScaleX(1.0);
        toSignup.setScaleY(1.0);
    }

    /**
     * set side panel visible if open
     * set side panel invisible is closed
     * set side panel invisible on click home since we are already on the welcome page
     * */
    @FXML
    void openSidePanel() {
        sidePane.setVisible(true);
    }
    @FXML
    void closeSidePanel() {
        sidePane.setVisible(false);
    }
    @FXML
    void onClickHome() { //since you're still on the welcome page, this is the same as toggling visibility
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
}
