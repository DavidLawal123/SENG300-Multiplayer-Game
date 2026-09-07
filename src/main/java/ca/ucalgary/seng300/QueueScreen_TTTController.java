package ca.ucalgary.seng300;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

//Controller for the game lobby
public class QueueScreen_TTTController {
    @FXML
    private Button Start_game_button;

    @FXML
    private Label user_one_name;

    @FXML
    private Label user_two_name;

    @FXML
    private ImageView user_one_pfp;

    @FXML
    private ImageView user_two_pfp;

    @FXML
    private Circle userCircle1;

    @FXML
    private Circle userCircle2;
    @FXML
    private Label versus_Label;
    @FXML
    private Button endgame;

    private User currentUser;
    private Matchmaker matchmaker;
    private UUID lobbyHandlerID;

    /**
     * fire matchmaker periodically to search for a match
     * if no player found after a certain amount of time, assign a bot
     * */
    public void setupMatchmaking(User user, Matchmaker matchmaker, Stage stage) {
        this.currentUser = user;
        this.matchmaker = matchmaker;

        user_one_name.setText(currentUser.getUsername());
        userCircle1.setFill(new ImagePattern(PFPManager.getProfilePicture(currentUser)));
        user_two_name.setText("Searching...");
        Start_game_button.setDisable(true);

        lobbyHandlerID = matchmaker.getEventBus().registerHandler(() -> {
            matchmaker.getEventBus().removeHandler(lobbyHandlerID);
            Platform.runLater(() -> {
                Lobby mylobby = null;
                for (Lobby lobby : matchmaker.getLobbies()) {
                    if (lobby.getUsers().contains(currentUser)) {
                        mylobby = lobby;
                        break;
                    }
                }
                if (mylobby == null) return;

                List<User> users = mylobby.getUsers();
                User opponent = users.get(0).equals(currentUser) ? users.get(1) : users.get(0);

                user_two_name.setText(opponent.getUsername());

                if (opponent.getUsername().equals("Bot")) {
                    Image robotImage = new Image(PFPManager.class.getResource("/pfps/Cartoon_Robot.jpg").toExternalForm());
                    userCircle2.setFill(new ImagePattern(robotImage));
                } else {
                    userCircle2.setFill(new ImagePattern(PFPManager.getProfilePicture(opponent)));
                }

                queueAnimation();

                Start_game_button.setDisable(false);
                Start_game_button.setUserData(opponent);
            });
        }, Event.LOBBY_IS_FULL);

        matchmaker.queueForMatch(currentUser, GameType.TIC_TAC_TOE);

        for (User u : Database.getUsersWithStatus(UserStatus.ONLINE)) {
            if (!u.getUsername().equals(currentUser.getUsername())) {
                try {
                    matchmaker.queueForMatch(u, GameType.TIC_TAC_TOE);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        try {
            User botUser = new User("Bot", "bot@test.com", "password");
            matchmaker.queueForMatch(botUser, GameType.TIC_TAC_TOE);
        } catch (Exception e) {
            System.out.println("Bot queue failed: " + e);
        }
    }

    /**
     * set player 1 picture and name
     * set player 2 picture and name
     * */
    @FXML
    public void initialize(){
        user_one_name.setText("Waiting...");
        user_two_name.setText("Searching...");
        Start_game_button.setDisable(true);

        userCircle1.setScaleX(4.0);
        userCircle2.setScaleX(4.0);
        userCircle1.setScaleY(4.0);
        userCircle2.setScaleY(4.0);

        versus_Label.setScaleX(4.0);
        versus_Label.setScaleY(4.0);

        versus_Label.setOpacity(0.0);
        userCircle2.setOpacity(0.0);
    }

    /**
     * animation for queue setup
     * */
    private void queueAnimation(){
        ScaleTransition player1= new ScaleTransition(Duration.millis(400), userCircle1);
        ScaleTransition player2= new ScaleTransition(Duration.millis(400), userCircle2);
        ScaleTransition versus= new ScaleTransition(Duration.millis(400), versus_Label);
        player1.setToX(1.0);
        player2.setToX(1.0);
        versus.setToX(1.0);

        player1.setToY(1.0);
        player2.setToY(1.0);
        versus.setToY(1.0);

        player1.setInterpolator(Interpolator.EASE_OUT);
        player2.setInterpolator(Interpolator.EASE_OUT);
        versus.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade1= new FadeTransition(Duration.millis(100), userCircle2);
        fade1.setFromValue(0.0);
        fade1.setToValue(1.0);

        FadeTransition fade2= new FadeTransition(Duration.millis(100), versus_Label);
        fade2.setFromValue(0.0);
        fade2.setToValue(1.0);

        SequentialTransition sequential = new SequentialTransition(
                player1,fade1,player2,fade2,versus
        );

        sequential.play();
    }

    /**
     * switch to game interface if player starts game
     * */
    @FXML
    public void handleStartGame(ActionEvent event) throws IOException {
        User opponent = (User) Start_game_button.getUserData();
        if (opponent == null) return;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("LoadingscreenTTT.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        LoadingscreenTTTController controller = fxmlLoader.getController();

        Stage stage = (Stage) Start_game_button.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        controller.setupForTransition(currentUser, opponent, matchmaker, stage);
    }

    /**
     * switch back to main page if player declines the game
     * */
    public void handleEndGame(ActionEvent event) {
        if (lobbyHandlerID != null) {
            try {
                matchmaker.getEventBus().removeHandler(lobbyHandlerID);
            } catch (IllegalArgumentException e) {
            }
            lobbyHandlerID = null;
        }
        currentUser.setStatus(UserStatus.ONLINE);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainpage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) Start_game_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void endGameHoverEnter(MouseEvent event) {
        endgame.setScaleX(1.1);
        endgame.setScaleY(1.1);
    }

    @FXML
    void endGameHoverExit(MouseEvent event) {
        endgame.setScaleX(1.0);
        endgame.setScaleY(1.0);
    }

    @FXML
    void startHoverEnter(MouseEvent event) {
        Start_game_button.setScaleX(1.1);
        Start_game_button.setScaleY(1.1);
    }

    @FXML
    void startHoverExit(MouseEvent event) {
        Start_game_button.setScaleX(1.0);
        Start_game_button.setScaleY(1.0);
    }
}
