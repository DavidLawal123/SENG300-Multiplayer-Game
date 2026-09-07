package ca.ucalgary.seng300;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

public class ProfileController {
    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private Text toHome; // to home page

    @FXML
    private Text toLogout; // to logout

    @FXML
    private Text toSettings; // to settings page

    @FXML
    private Text toProfile; // to profile page

    @FXML
    private Text userLevel; // user's level

    @FXML
    private ImageView chatButton; // chat button only on other player's profiles

    @FXML
    private ImageView menuOpen; // open side panel

    @FXML
    private ImageView menuClose; // close side panel

    @FXML
    private Text userName; // username

    @FXML
    private Text connect4WLD1; // win, loss, draws for C4

    @FXML
    private Text tictactoeWLD; // win, loss, draws for TTT

    @FXML
    private Button challengeTTTButton; // challenge opponent to TTT

    @FXML
    private Button challengeC4Button; // challenge opponent to C4

    @FXML
    public Circle profilePic; // profile picture

    private User displayedUser;
    private boolean isOwnProfile;
    private Matchmaker matchmaker = new Matchmaker();
    // currentUser cannot be FINAL because profile is reused for other users
    private User currentUser = Database.getUserFromUUID(SessionData.getCurrentLoggedInUUID());

    /**
     * set side panel as invisible
     * chat button as invisible; only appears on other player's profiles
     * */
    @FXML
    void initialize() {
        sidePane.setVisible(false);
        chatButton.setVisible(false);
    }

    /**
     * Populates profile view using currently assigned displayed user
     * */
    private void populateProfile() {
        if (displayedUser == null) {
            return;
        }

        profilePic.setFill(new ImagePattern(PFPManager.getProfilePicture(displayedUser)));
        userName.setText(displayedUser.getUsername());
        userLevel.setText("TTT Level: " + displayedUser.getLevel(GameType.TIC_TAC_TOE) + "*** C4 Level: " + displayedUser.getLevel(GameType.CONNECT_FOUR));
        connect4WLD1.setText("Wins: " + displayedUser.getPlayerStats(GameType.CONNECT_FOUR).getWins() + " Losses: " + displayedUser.getPlayerStats(GameType.CONNECT_FOUR).getLosses() + " Draws: " + displayedUser.getPlayerStats(GameType.CONNECT_FOUR).getDraws());
        tictactoeWLD.setText("Wins: " + displayedUser.getPlayerStats(GameType.TIC_TAC_TOE).getWins() + " Losses: " + displayedUser.getPlayerStats(GameType.TIC_TAC_TOE).getLosses() + " Draws: " + displayedUser.getPlayerStats(GameType.TIC_TAC_TOE).getDraws());
    }

    /**
     * Assigns the user to be rendered by the profile view
     * */
    public void setDisplayedUser(User user) {
        this.displayedUser = user;

        if (user == null) {
            return;
        }

        // Compares the selected user's UUID against the active session UUID
        this.isOwnProfile = user.getUUID().equals(SessionData.getCurrentLoggedInUUID());
        challengeTTTButton.setVisible(!isOwnProfile);
        challengeC4Button.setVisible(!isOwnProfile);
        chatButton.setVisible(!isOwnProfile);

        populateProfile();
    }

    // hover functionality for challenge TTT and challenge C4 buttons
    @FXML
    void TTTHoverEnter() {
        challengeTTTButton.setScaleX(1.1);
        challengeTTTButton.setScaleY(1.1);
    }
    @FXML
    void TTTHoverExit() {
        challengeTTTButton.setScaleX(1.0);
        challengeTTTButton.setScaleY(1.0);
    }
    @FXML
    void C4HoverEnter() {
        challengeC4Button.setScaleX(1.1);
        challengeC4Button.setScaleY(1.1);
    }
    @FXML
    void C4HoverExit() {
        challengeC4Button.setScaleX(1.0);
        challengeC4Button.setScaleY(1.1);
    }

    /**
     * Changes the profile picture of the current user to the next one in the cycle
     * @param event
     */
    @FXML
    private void changePFP(MouseEvent event) {
        if (!isOwnProfile || displayedUser == null) {
            return;
        }

        int currentPfp = displayedUser.getProfilePicture();
        currentPfp++;

        if (currentPfp > PFPManager.getPFPCount()) currentPfp = 1;

        displayedUser.setPfp(currentPfp);
        profilePic.setFill(new ImagePattern(PFPManager.getProfilePicture(currentPfp)));

        try {
            Database.writeToFile();
        } catch (Exception e) {
            System.out.println("Failed to write profile picture.");
            System.out.println(e.getMessage());
        }
    }

    /**
     * toggle chat if available to press
     * */
    @FXML
    void openCloseChat() {
        ChatToggleManager.toggleChat();
    }
    // hover functionality for chat button
    @FXML
    void chatHoverEnter() {
        chatButton.setScaleX(1.1);
        chatButton.setScaleY(1.1);
    }
    @FXML
    void chatHoverExit(){
        chatButton.setScaleX(1.0);
        chatButton.setScaleY(1.0);
    }

    /**
     * open and close side panel
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
     * instead of just closing side panel, reloads the scene and applies information depending on if it's the user, or another player
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
    // hover functionality for profile button
    @FXML
    void profileHoverEnter() {
        toProfile.setScaleX(1.1);
        toProfile.setScaleY(1.1);
    }
    @FXML
    void profileHoverExit(){
        toProfile.setScaleX(1.0);
        toProfile.setScaleY(1.0);
    }

    /**
     * on click to settings, switch scenes to settings.fxml
     * */
    @FXML
    void goToSettings() {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("settings.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) toSettings.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    // hover functionality for setting button
    @FXML
    void settingHoverEnter() {
        toSettings.setScaleX(1.1);
        toSettings.setScaleY(1.1);
    }
    @FXML
    void settingHoverExit(){
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
    // hover functionality for home button
    @FXML
    void homeHoverEnter() {
        toHome.setScaleX(1.1);
        toHome.setScaleY(1.1);
    }
    @FXML
    void homeHoverExit(){
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
    // hover functionality for logout button
    @FXML
    void logoutHoverEnter() {
        toLogout.setScaleX(1.1);
        toLogout.setScaleY(1.1);
    }
    @FXML
    void logoutHoverExit(){
        toLogout.setScaleX(1.0);
        toLogout.setScaleY(1.0);
    }

    /**
     * Challenge user to a TicTacToe match, instantly putting the two users into a lobby
     * */
    @FXML
    private void challengeTTT(MouseEvent event) {
        if (isOwnProfile || displayedUser == null) {
            return;
        }

        User challengedUser = Database.getUserFromUUID(displayedUser.getUUID());

        try {
            Lobby lobby = matchmaker.createDirectChallengeLobby(currentUser, challengedUser, GameType.TIC_TAC_TOE);

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("tictactoe.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            TicTacToeController controller = fxmlLoader.getController();
            controller.userSetUp(currentUser, challengedUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Challenge user to a Connect 4 match, instantly putting the two users into a lobby
     * */
    @FXML
    private void challengeC4(MouseEvent event) {
        if (isOwnProfile || displayedUser == null) {
            return;
        }

        User challengedUser = Database.getUserFromUUID(displayedUser.getUUID());

        try {
            Lobby lobby = matchmaker.createDirectChallengeLobby(currentUser, challengedUser, GameType.CONNECT_FOUR);

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("connect4.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            Connect4Controller controller = fxmlLoader.getController();
            controller.userSetup(currentUser, challengedUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}