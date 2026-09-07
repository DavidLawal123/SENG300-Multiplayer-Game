package ca.ucalgary.seng300;

import ca.ucalgary.seng300.statistics.PlayerStats;
import ca.ucalgary.seng300.statistics.StatisticsManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class LeaderBoardController {

    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private ImageView menuOpen; // open side panel

    @FXML
    private ImageView menuClose; // close side panel

    @FXML
    private Text toHome; // to home page

    @FXML
    private Text toLogout; // to logout page

    @FXML
    private Text toProfile; // to profile page

    @FXML
    private Text toSettings; // to settings page

    @FXML
    private TableView<PlayerStats> leaderboardTable; // leaderboard

    @FXML
    private TableColumn<PlayerStats, String> col_user; // column for user

    @FXML
    private TableColumn<PlayerStats, String> col_winrate; // column for winrate

    @FXML
    private TableColumn<PlayerStats, Integer> col_wins; // column for wins

    @FXML
    private TableColumn<PlayerStats, Integer> col_losses; // column for losses

    @FXML
    private TableColumn<PlayerStats, Integer> col_draws; // column for draws

    @FXML
    private TableColumn<PlayerStats, Integer> col_rank; // column for rank

    StatisticsManager statsManager = new StatisticsManager();
    private User currentUser = Database.getUserFromUUID(SessionData.getCurrentLoggedInUUID());

    /**
     * initialize dropdown with game choices
     * set up leaderboard table
     * set side panel invisible
     * */
    @FXML
    public void initialize(){
        setupGameChoices();
        setupLeaderboardTable();
        sidePane.setVisible(false);
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

    @FXML
    private ChoiceBox<GameType> game_choice;

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

        ProfileController controller = fxmlLoader.getController();
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
     * display all of the player info in the leaderboard
     * */
    private void setupLeaderboardTable(){
        ArrayList<PlayerStats> leaderboard = statsManager.getRawGameStats(game_choice.getValue());
        leaderboardTable.getItems().clear();

        col_user.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(Objects.requireNonNull(Database.getUserFromUUID(data.getValue().getUserID())).getUsername()));
        col_user.setStyle("-fx-alignment: CENTER;");

        col_wins.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getWins()));
        col_wins.setStyle("-fx-alignment: CENTER;");

        col_losses.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getLosses()));
        col_losses.setStyle("-fx-alignment: CENTER;");

        col_draws.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getDraws()));
        col_draws.setStyle("-fx-alignment: CENTER;");

        col_winrate.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(String.format("%.2f%%", data.getValue().getWinRate())));
        col_winrate.setStyle("-fx-alignment: CENTER;");

        col_rank.setCellValueFactory(data -> {
           int index = leaderboard.indexOf(data.getValue());
           return new ReadOnlyObjectWrapper<>(index + 1);
        });
        col_rank.setStyle("-fx-alignment: CENTER;");

        leaderboardTable.setItems(FXCollections.observableArrayList(leaderboard));
    }

    /**
     * add game choices to the dropdown
     * currently only TICTACTOE and CONNECT4
     * */
    private void setupGameChoices () {
        game_choice.getItems().addAll(GameType.values());
        game_choice.setValue(GameType.CONNECT_FOUR);
        // Sets choice box to use display name for the GameType instead of the enum value
        game_choice.setConverter(new StringConverter<GameType>() {
            // Converts enum to String
            @Override
            public String toString(GameType gameType) {
                return gameType.getDisplayName();
            }

            // Required for interface, not used for choice box
            @Override
            public GameType fromString(String s) {
                return null;
            }
        });

        game_choice.setOnAction(event -> setupLeaderboardTable());
    }
}