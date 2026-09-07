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

public class SignupController {
    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private Text toHome; // button to go to welcome page

    @FXML
    private Text signupToLogin; // button to go to login page

    @FXML
    private Button signUp; // button to authenticate signup

    @FXML
    private ImageView menuOpen; // open side panel

    @FXML
    private ImageView menuClose; // close side panel

    @FXML
    private TextField username; // username field
    @FXML
    private PasswordField password; // password field
    @FXML
    private PasswordField confirmPassword; // confirm password field
    @FXML
    private TextField email; // email field
    @FXML
    private Label errorMessage; // error message

    /**
     * initialize side panel invisible
     * set visible if open
     * set invisible if closed
     * */
    @FXML
    void initialize() throws Exception {
        sidePane.setVisible(false);
        errorMessage.setVisible(false);
        Database.readFromFile();
    }
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
     * on click home, switch scenes to welcome.fxml
     * */
    @FXML
    void onClickHome() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("welcome.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) toHome.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * on click to login to account instead, switch scenes to login.fxml
     * */
    @FXML
    void switchSignupToLogin() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) signupToLogin.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * on click to signup,
     * if the username, email, and password are valid inputs then create an account
     * add information to database
     * switch scenes to mainpage.fxml
     * otherwise, show an error and prompt user to try again in the invalid field
     * */
    @FXML
    void onCreateAccountClick() throws Exception {
        String user = username.getText().trim();
        String pass = password.getText();
        String mail = email.getText().trim();
        boolean accAlreadyMade = Database.authenticate(user, pass);

        // check if inputs are already valid
        if (accAlreadyMade) { // if an account already exists
            errorMessage.setVisible(true);
            errorMessage.setText("Account already exists. Please login instead.");
        }
        else if (username.getText().isEmpty()) { // if username field is empty
            errorMessage.setVisible(true);
            errorMessage.setText("Please enter a username.");
        }
        else if (email.getText().isEmpty()) { // if email field is empty
            errorMessage.setVisible(true);
            errorMessage.setText("Please enter an email.");
        }
        else if (!(email.getText().endsWith("@example.com"))) { // if email doesn't end in "@example.com"
            errorMessage.setVisible(true);
            errorMessage.setText("Please enter a valid email ending in @example.com.");
        }
        else if (password.getText().isEmpty()) { // if password field is empty
            errorMessage.setVisible(true);
            errorMessage.setText("Please enter a password.");
        }
        else if (confirmPassword.getText().isEmpty()) { // if confirm password field is empty
            errorMessage.setVisible(true);
            errorMessage.setText("Please confirm your password.");
        }
        else if (!(password.getText().equals(confirmPassword.getText()))) { // iff password doesn't match confirm password
            errorMessage.setVisible(true);
            errorMessage.setText("Passwords don't match.");
        }
        else {
            errorMessage.setVisible(false);

            Database.readFromFile();
            // add user to database
            Database.addUser(new User(user, mail, pass));
            // write to file
            Database.writeToFile();

            // update SessionData with the currently logged in user's info
            User u = Database.getUserFromUsername(user);
            SessionData.storeCurrentPlayer(u.getUUID());

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainpage.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), 600,400);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            Stage stage  = (Stage) signUp.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    // hover functionality for login authenticate button
    @FXML
    void signupHoverEnter() {
        signUp.setScaleX(1.1);
        signUp.setScaleY(1.1);
    }
    @FXML
    void signupHoverExit(){
        signUp.setScaleX(1.0);
        signUp.setScaleY(1.0);
    }
}
