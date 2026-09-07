package ca.ucalgary.seng300;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class SettingsController {
    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private Text toHome; // button to go to main page (on side panel)

    @FXML
    private Text toLogout; // button to logout (on side panel)

    @FXML
    private Text toProfile; // button to go to user profile (on side panel)

    @FXML
    private Text toSettings; // button to go to settings (on side panel)

    @FXML
    private ImageView menuOpen; // open side panel

    @FXML
    private ImageView menuClose; // close side panel

    @FXML
    private Text cU; // change username button

    @FXML
    private Text cP; // change password button

    @FXML
    private Text cE; // change email button

    @FXML
    private Text updatePfpText; // prompt to change profile picture

    @FXML
    private Circle updatePicture; // view profile picture


    private final User currentUser = Database.getUserFromUUID(SessionData.getCurrentLoggedInUUID());

    /**
     * initially set side panel as invisible
     *      set visible when open
     *      set invisible when closed
     * get the current user and update their profile picture
     * */
    @FXML
    void initialize() {
        sidePane.setVisible(false);
        updatePfpText.setVisible(false);
        updatePicture.setFill(new ImagePattern(PFPManager.getProfilePicture(currentUser)));
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
     * on click to user profile, switch scenes to profile.fxml
     * */
    @FXML
    void goToProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("profile.fxml"));
        Scene scene;

        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Retrieve profile controller instance
        ProfileController controller = fxmlLoader.getController();

        // Initialize the profile view with currentUser
        controller.setDisplayedUser(currentUser);

        Stage stage = (Stage) toHome.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // hover functionality for going to profile
    @FXML
    void profileHoverEnter() {
        toProfile.setScaleX(1.1);
        toProfile.setScaleY(1.1);
    }
    @FXML
    void profileHoverExit() {
        toProfile.setScaleX(1.0);
        toProfile.setScaleY(1.0);
    }

    /**
     * on click to settings, close side panel since we are currently on settings page
     * */
    @FXML
    void goToSettings() {
        sidePane.setVisible(false);
    }

    // hover functionality for going to settings
    @FXML
    void settingsHoverEnter() {
        toSettings.setScaleX(1.1);
        toSettings.setScaleY(1.1);
    }
    @FXML
    void settingsHoverExit() {
        toSettings.setScaleX(1.0);
        toSettings.setScaleY(1.0);
    }

    /**
     * on click to home, switch scenes to mainpage.fxml
     * */
    @FXML
    void goToHome() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainpage.fxml"));
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

    // hover functionality for going to main page
    @FXML
    void homeHoverEnter() {
        toHome.setScaleX(1.1);
        toHome.setScaleY(1.1);
    }
    @FXML
    void homeHoverExit() {
        toHome.setScaleX(1.0);
        toHome.setScaleY(1.0);
    }

    /**
     * on click to logout, an ALERT pops up
     * confirmation logs user out and returns them to welcome.fxml
     * cancel closes the popup
     * */
    @FXML
    void goToLogout() {
        Alert logoutAlert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutAlert.setHeaderText("Logout");
        logoutAlert.setContentText("Are you sure you want to logout?");

        ButtonType logoutButton = new ButtonType("Logout", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        logoutAlert.getButtonTypes().setAll(logoutButton, cancelButton);

        Optional<ButtonType> result = logoutAlert.showAndWait();

        if (result.isPresent() && result.get() == logoutButton) {
            // close chat/settings if open when MainApp closes
            if (ChatToggleManager.isOpen()){
                ChatToggleManager.closeChat();
            }
            if (SettingsToggleManager.isOpen()){
                SettingsToggleManager.closeSettings();
            }

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("welcome.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), 600, 400);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = (Stage) toLogout.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (result.isPresent() && cancelButton == logoutButton) {
            logoutAlert.close();
        }
    }

    // hover functionality for going to logout
    @FXML
    void logoutHoverEnter() {
        toLogout.setScaleX(1.1);
        toLogout.setScaleY(1.1);
    }
    @FXML
    void logoutHoverExit() {
        toLogout.setScaleX(1.0);
        toLogout.setScaleY(1.0);
    }

    /**
     * in settings, on click to change username, open a new stage(if unopened) and switch scenes to "username"
     * */
    @FXML
    void changeUsername() {
        SettingsToggleManager.toggleSettings("Username");
    }

    // hover functionality for changing username
    @FXML
    void cUHoverEnter() {
        cU.setScaleX(1.1);
        cU.setScaleY(1.1);
    }
    @FXML
    void cUHoverExit() {
        cU.setScaleX(1.0);
        cU.setScaleY(1.0);
    }

    /**
     * in settings, on click to change password, open a new stage(if unopened) and switch scenes to "password"
     * */
    @FXML
    void changePassword() {
        SettingsToggleManager.toggleSettings("Password");
    }

    // hover functionality for changing password
    @FXML
    void cPHoverEnter() {
        cP.setScaleX(1.1);
        cP.setScaleY(1.1);
    }
    @FXML
    void cPHoverExit() {
        cP.setScaleX(1.0);
        cP.setScaleY(1.0);
    }

    /**
     * in settings, on click to change password, open a new stage(if unopened) and switch scenes to "email"
     * */
    @FXML
    void changeEmail() {
        SettingsToggleManager.toggleSettings("Email");
    }

    // hover functionality for changing email
    @FXML
    void cEHoverEnter() {
        cE.setScaleX(1.1);
        cE.setScaleY(1.1);
    }
    @FXML
    void cEHoverExit() {
        cE.setScaleX(1.0);
        cE.setScaleY(1.0);
    }

    /**
     * Changes the profile picture of the current user to the next one in the cycle
     * @param event
     */
    @FXML
    private void changePFP(MouseEvent event) throws Exception {
        int currentPfp = currentUser.getProfilePicture();
        currentPfp++;
        if (currentPfp > PFPManager.getPFPCount()) currentPfp = 1;
        currentUser.setPfp(currentPfp);
        updatePicture.setFill(new ImagePattern(PFPManager.getProfilePicture(currentPfp)));
        Database.writeToFile();
    }

    @FXML
    private void profileEnter(MouseEvent event) {
        updatePfpText.setVisible(true);
    }

    @FXML
    private void profileExit(MouseEvent event) {
        updatePfpText.setVisible(false);
    }
}
