package ca.ucalgary.seng300;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class SettingsChangeController {
    @FXML
    private AnchorPane usernameScreen; // change username scene

    @FXML
    private AnchorPane passwordScreen; // change password scene

    @FXML
    private AnchorPane emailScreen; // change email scene

    @FXML
    private TextField newUsername; // new username field

    @FXML
    private TextField newPassword; // new password field

    @FXML
    private TextField confirmNewPassword; // confirm new password field

    @FXML
    private TextField newEmail; // new email field

    @FXML
    private TextField code; // verification code field (for email)

    @FXML
    private Label passLabel; // error message for password

    @FXML
    private Label mailLabel; // error message for email

    @FXML
    private Label userLabel; // error message for username

    @FXML
    private Button okChangePass; // ok button for changing password

    @FXML
    private Button okChangeEmail; // ok button for changing email

    @FXML
    private Button okSendCode; // "send" verification code

    @FXML
    private Button okChangeName; // ok button for changing username

    /**
     * settings changer prevents the need for multiple stages, just the scene changes
     * the user can
     * change username: show username scene
     * change password: show password scene
     * change email: show email scene
     * */
    public void setMode(String mode) {
        switch (mode) {
            case "Username" -> {
                usernameScreen.setVisible(true);
                passwordScreen.setVisible(false);
                emailScreen.setVisible(false);
            }
            case "Password" -> {
                usernameScreen.setVisible(false);
                passwordScreen.setVisible(true);
                emailScreen.setVisible(false);
            }
            case "Email" -> {
                usernameScreen.setVisible(false);
                passwordScreen.setVisible(false);
                emailScreen.setVisible(true);
            }
        }
    }

    /**
     * initialize error messages as invisible
     * */
    @FXML
    void initialize() {
        passLabel.setVisible(false);
        mailLabel.setVisible(false);
        userLabel.setVisible(false);
    }

    /**
     * get the current user by UUID
     * */
    User user = Database.getUserFromUUID(SessionData.getCurrentLoggedInUUID());

    /**
     * change the username of the current user
     * display error message if:
     *      field is empty
     *      new username is the current username
     * */
    @FXML
    void changeUsername() throws Exception {
        if (newUsername.getText().isEmpty()) {
            userLabel.setVisible(true);
            userLabel.setText("Please enter a new username.");
            userLabel.setStyle("-fx-text-fill: red");
        }
        else if (newUsername.getText().equals(user.getUsername())) {
            userLabel.setVisible(true);
            userLabel.setText("Username is not new.");
            userLabel.setStyle("-fx-text-fill: red");
        }
        else {
            user.setUsername(newUsername.getText());
            Stage stage = (Stage) newUsername.getScene().getWindow();
            stage.close();
            Database.writeToFile();
        }
    }
    // hover functionality for ok button
    @FXML
    void userHoverEnter() {
        okChangeName.setScaleX(1.1);
        okChangeEmail.setScaleX(1.1);
    }
    @FXML
    void userHoverExit() {
        okChangeName.setScaleX(1.0);
        okChangeEmail.setScaleX(1.0);
    }

    /**
     * change password of the current user
     * display error message if:
     *      either field is empty
     *      confirmation of password doesn't match
     *      new password is the current password
     * */
    @FXML
    void changePassword() throws Exception {
        if (newPassword.getText().isEmpty()) {
            passLabel.setVisible(true);
            passLabel.setText("Please enter a new password.");
            passLabel.setStyle("-fx-text-fill: red");
        }
        else if (confirmNewPassword.getText().isEmpty()) {
            passLabel.setVisible(true);
            passLabel.setText("Please confirm the new password.");
            passLabel.setStyle("-fx-text-fill: red");
        }
        else if (!(newPassword.getText().equals(confirmNewPassword.getText()))) {
            passLabel.setVisible(true);
            passLabel.setText("Passwords do not match.");
            passLabel.setStyle("-fx-text-fill: red");
        }
        else if (user.testPassword(newPassword.getText())) {
            passLabel.setVisible(true);
            passLabel.setText("Password is not new.");
            passLabel.setStyle("-fx-text-fill: red");
        }
        else {
            user.setPassword(newPassword.getText());
            Stage stage  = (Stage) newPassword.getScene().getWindow();
            stage.close();
            Database.writeToFile();
        }
    }
    // hover functionality for ok button
    @FXML
    void passHoverEnter() {
        okChangePass.setScaleX(1.1);
        okChangePass.setScaleY(1.1);
    }
    @FXML
    void passHoverExit() {
        okChangePass.setScaleX(1.0);
        okChangePass.setScaleY(1.0);
    }


    /**
     * send verification code to the new email
     * display error message if:
     *      email field is empty
     *      new email is the current email
     * */
    @FXML
    void sendCode() {
        if (newEmail.getText().isEmpty()) {
            mailLabel.setVisible(true);
            mailLabel.setText("Please enter a new email.");
            mailLabel.setStyle("-fx-text-fill: red");
        }
        else if (newEmail.getText().equals(user.getEmail())) {
            mailLabel.setVisible(true);
            mailLabel.setText("Email is not new.");
            mailLabel.setStyle("-fx-text-fill: red");
        }
        else {
            mailLabel.setVisible(true);
            mailLabel.setText("Verification code sent!");
            mailLabel.setStyle("-fx-text-fill: green;");
        }
    }
    // hover functionality for send code button
    @FXML
    void codeHoverEnter() {
        okSendCode.setScaleX(1.1);
        okSendCode.setScaleY(1.1);
    }
    @FXML
    void codeHoverExit() {
        okSendCode.setScaleX(1.0);
        okSendCode.setScaleY(1.0);
    }

    /**
     * change email of the current user
     * display error message if:
     *      verification code field is empty
     *      code is not a 6-digit code
     * */
    @FXML
    void changeEmail() throws Exception {
        if (code.getText().isEmpty()){
            mailLabel.setVisible(true);
            mailLabel.setText("Please enter the 6-digit verification code.");
            mailLabel.setStyle("-fx-text-fill: red;");
        }
        else if (code.getText().length() != 6) {
            mailLabel.setVisible(true);
            mailLabel.setText("The code must be 6-digits.");
            mailLabel.setStyle("-fx-text-fill: red;");
        }
        else {
            user.setEmail(newEmail.getText());
            Stage stage  = (Stage) newEmail.getScene().getWindow();
            stage.close();
            Database.writeToFile();
        }
    }
    // hover functionality for ok button
    @FXML
    void mailHoverEnter() {
        okChangeEmail.setScaleX(1.1);
        okChangeEmail.setScaleY(1.1);
    }
    @FXML
    void mailHoverExit() {
        okChangeEmail.setScaleX(1.0);
        okChangeEmail.setScaleY(1.0);
    }
}

