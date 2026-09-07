package ca.ucalgary.seng300;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class MainPageController {
    @FXML
    private ImageView menuOpen; // to open side panel

    @FXML
    private ImageView menuClose; // to close side panel

    @FXML
    private ImageView rankButton; // to access leaderboard

    @FXML
    private Button createTTT; // create a lobby for TTT

    @FXML
    private Button createC4; // create a lobby for C4

    @FXML
    private Button joinTTT; // join a lobby for TTT

    @FXML
    private Button joinC4; // join a lobby for C4

    @FXML
    private Label errorLabelMain; // error label for lobby/queue failure

    @FXML
    private ListView<String> lobbyListConnect4; // list of open C4 lobbies

    @FXML
    private ListView<String> lobbyListTictactoe; // list of open TTT lobbies

    @FXML
    private AnchorPane joinAnchorConnect4; // prompt join lobby for C4

    @FXML
    private AnchorPane queueAnchorConnect4; // prompt join queue for C4

    @FXML
    private AnchorPane joinAnchorTictactoe; // prompt join lobby for TTT

    @FXML
    private AnchorPane queueAnchor; // prompt join queue for TTT

    @FXML
    private AnchorPane sidePane; // side panel

    @FXML
    private Text toHome; // go home on side menu

    @FXML
    private Text toLogout; // go logout on side menu

    @FXML
    private Text toSettings; // go to settings on side menu

    @FXML
    private Text toProfile; // go to profile on side menu

    @FXML
    private Button buttonTriggerTTT; // enter a game of TTT

    @FXML
    private Button buttonTriggerC4; // enter a game of C4


    @FXML
    private TextField searchBar; // search bar

    @FXML
    private ImageView searchButton; // trigger for search

    @FXML
    private Label errorMessage; // error message for search

    @FXML
    private AnchorPane rootAnchor;

    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    private ProgressIndicator loadingSpinner1;

    /**
     * create new matchmatcher
     * initialize lobby lists for TTT and C4
     * */
    private Matchmaker matchmaker = new Matchmaker();
    private final User currentUser = Database.getUserFromUUID(SessionData.getCurrentLoggedInUUID());
    private List<User> listedUsers = new  ArrayList<>();
    private List<User> listedUsersC4 = new   ArrayList<>();

    /**
     * set side panel as invisible
     * stylization for when progress spinners are triggered
     * */
    @FXML
    void initialize() {
        if (sidePane != null) sidePane.setVisible(false);

        lobbyListConnect4.setMouseTransparent(true);
        lobbyListConnect4.setFocusTraversable(false);

        loadingSpinner.setVisible(false);
        loadingSpinner1.setVisible(false);

        matchmaker.fakePopulateLobby(currentUser);
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
     * booleans to detect which option has been chosen
     * */
    boolean createLobbyOpen = false;
    boolean createLobbyOpenConnect4 = false;
    boolean joinLobbyOpenConnect4 = false;
    boolean joinLobbyOpenTictactoe = false;

    /**
     * match player with the selected opponent in the lobby for TTT
     * */
    @FXML
    void triggerLobbyTTT(ActionEvent event) {
        //check if panel is visible
        if(!joinAnchorTictactoe.isVisible()){
            return;
        }

        //diable the button to prevent it from being pressed multiple times
        buttonTriggerTTT.setDisable(true);
        rootAnchor.setDisable(true);
        loadingSpinner1.setVisible(true);

        //store handler id so we can unregister it after a match is found to avoid the multiple game session bug
        final UUID[] handlerID = new UUID[1];
        handlerID[0]=matchmaker.getEventBus().registerHandler(()-> {
            System.out.println("triggered");
            Platform.runLater(() -> {
                try {
                    Lobby myLobby = null;
                    int count = 0;

                    for (Lobby lobby : matchmaker.getLobbies()) {
                        if (lobby.getUsers().contains(currentUser)) {
                            myLobby = lobby;
                            count++;
                        }
                    }

                    if (count == 0) {
                      //  System.out.println("No lobby found for current user.");
                        errorLabelMain.setText("No lobby found for current user.");
                        buttonTriggerTTT.setDisable(false);
                        loadingSpinner1.setVisible(false);
                        rootAnchor.setDisable(false);
                        return;
                    }

                    if (count > 1) {
                        //System.out.println("ERROR: current user is in multiple lobbies.");
                        errorLabelMain.setText("Multiple lobbies found for current user.");
                        buttonTriggerTTT.setDisable(false);
                        loadingSpinner1.setVisible(false);
                        rootAnchor.setDisable(false);
                        return;
                    }

                    matchmaker.getEventBus().removeHandler(handlerID[0]);

                    //get players in lobby
                    List<User> users = myLobby.getUsers();
                    User opponent;

                    //checks which user is the current user
                    if (users.get(0).equals(currentUser)) {
                        opponent = users.get(1);
                    } else {
                        opponent = users.get(0);
                    }
                    //loads scene
                    FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("tictactoe.fxml"));
                    Scene scene;
                    scene = new Scene(fxmlLoader.load(), 600, 400);
                    TicTacToeController controller = fxmlLoader.getController();
                    //IMPORTANTTTT passes currentuser and the opps
                    controller.userSetUp(currentUser, opponent);
                    Stage stage  = (Stage) ((Node)event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    buttonTriggerTTT.setDisable(false);
                    loadingSpinner1.setVisible(false);
                    rootAnchor.setDisable(false);
                }
            });
        }, Event.LOBBY_CREATED_FROM_QUEUE);

        //checks if the user is online, then queues them for the game
        if(currentUser.getStatus()==(UserStatus.ONLINE)){
            System.out.println("pls wait:)");
            errorLabelMain.setText("Queueing. Please wait...");
            matchmaker.queueForMatch(currentUser, GameType.TIC_TAC_TOE);
        }
    }

    /**
     * match player with the selected opponent in the lobby for C4
     * */
    @FXML
    void triggerLobbyC4(ActionEvent event) {
        //check if panel is visible
        if(!joinAnchorConnect4.isVisible()){
            return;
        }

        //diable the button to prevent it from being pressed multiple times
        buttonTriggerC4.setDisable(true);
        rootAnchor.setDisable(true);
        loadingSpinner.setVisible(true);

         //store handler id so we can unregister it after a match is found to avoid the multiple game session bug
        final UUID[] handlerID = new UUID[1];
        handlerID[0]=matchmaker.getEventBus().registerHandler(()-> {
            System.out.println("triggered");
            Platform.runLater(() -> {
                try {
                    Lobby myLobby = null;
                    int count = 0;

                    for (Lobby lobby : matchmaker.getLobbies()) {
                        if (lobby.getUsers().contains(currentUser)) {
                            myLobby = lobby;
                            count++;
                        }
                    }

                    if (count == 0) {
                        System.out.println("No lobby found for current user.");
                        errorLabelMain.setText("No lobby found for current user.");
                        buttonTriggerC4.setDisable(false);
                        loadingSpinner.setVisible(false);
                        rootAnchor.setDisable(false);
                        return;
                    }

                    if (count > 1) {
                        System.out.println("ERROR: current user is in multiple lobbies.");
                        errorLabelMain.setText("Multiple lobbies found for current user.");
                        buttonTriggerC4.setDisable(false);
                        loadingSpinner.setVisible(false);
                        rootAnchor.setDisable(false);
                        return;
                    }

                    matchmaker.getEventBus().removeHandler(handlerID[0]);

                          //get players in lobby
                    List<User> users = myLobby.getUsers();
                    User opponent;

                    //checks which user is the current user
                    if (users.get(0).equals(currentUser)) {
                        opponent = users.get(1);
                    } else {
                        opponent = users.get(0);
                    }
                    //loads scene
                    FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("connect4.fxml"));
                    Scene scene;
                    scene = new Scene(fxmlLoader.load(), 600, 400);
                    Connect4Controller controller = fxmlLoader.getController();
                    //IMPORTANTTTT passes currentuser and the opps
                    controller.userSetup(currentUser, opponent);
                    Stage stage  = (Stage) ((Node)event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    buttonTriggerC4.setDisable(false);
                    loadingSpinner.setVisible(false);
                    rootAnchor.setDisable(false);
                }
            });
        }, Event.LOBBY_CREATED_FROM_QUEUE);

        //checks if the user is online, then queues them for the game
        if(currentUser.getStatus()==(UserStatus.ONLINE)){
            System.out.println("pls wait:)");
            errorLabelMain.setText("Queueing. Please wait...");
            matchmaker.queueForMatch(currentUser, GameType.CONNECT_FOUR);
        }
    }

    /**
     * anchorPane for joining C4 queue
     * */
    @FXML
    void createLobbyOptionButtonConnect4() {
        queueAnchorConnect4.setVisible(true);
    }
    // hover functionality for create TTT game button
    @FXML
    void createTTTHoverEnter() {
        createTTT.setScaleX(1.1);
        createTTT.setScaleY(1.1);
    }
    @FXML
    void createTTTHoverExit(){
        createTTT.setScaleX(1.0);
        createTTT.setScaleY(1.0);
    }

    /**
     * anchorPane for joining TTT queue
     * */
    @FXML
    void createLobbyOptionButtonTictactoe() {
        queueAnchor.setVisible(true);
    }
    // hover functionality for create C4 game button
    @FXML
    void createC4HoverEnter() {
        createC4.setScaleX(1.1);
        createC4.setScaleY(1.1);
    }
    @FXML
    void createC4HoverExit(){
        createC4.setScaleX(1.0);
        createC4.setScaleY(1.0);
    }

    /**
     * add player to queue for C4
     * if no available players, match user with a bot
     * */
    @FXML
    void joinLobbyConnect4(ActionEvent event) {
        //shows panel
        if (joinLobbyOpenConnect4==false) {
            joinAnchorConnect4.setVisible(true);
            joinLobbyOpenConnect4=true;
            //calls fake populate to populate the lobbies
            matchmaker.fakePopulateLobby(currentUser);
            //grabs all the queues for game types (which can be used to show users in the queue)
            HashMap<GameType,List<User>> queue=matchmaker.getCurrentQueues();

            lobbyListConnect4.getItems().clear();//clear the list
            listedUsersC4.clear();

            //get the list of all users waiting for c4 from the que. (return an empty lsit if no useres in in yet)
            List<User> waitingRoom=queue.getOrDefault(GameType.CONNECT_FOUR,new ArrayList<>());

            for(User users:waitingRoom){
                //skip the current user so they dont appear in the lobby list
                if(!users.getUUID().equals(currentUser.getUUID())){
                    //add a string to the listview showing the other users name and level for that specific game
                    lobbyListConnect4.getItems().add(users.getUsername()+ "'s waiting! LVL: "+ users.getLevel(GameType.CONNECT_FOUR));
                   //add the user to another lsit for later reference
                    listedUsersC4.add(users);

                }
            }

        }else if (joinLobbyOpenConnect4==true) {
            joinAnchorConnect4.setVisible(false);
            joinLobbyOpenConnect4=false;
            listedUsersC4.clear();

            lobbyListConnect4.getItems().clear(); //clear the list

        }
    }
    // hover functionality for joining C4 button
    @FXML
    void joinC4HoverEnter() {
        joinC4.setScaleX(1.1);
        joinC4.setScaleY(1.1);
    }
    @FXML
    void joinC4HoverExit(){
        joinC4.setScaleX(1.0);
        joinC4.setScaleY(1.0);
    }


    /**
     * add player to queue for TTT
     * if no available players, match user with a bot
     * */
    public void joinLobbyTictactoe(ActionEvent actionEvent) {
        //shows panel
        if (joinLobbyOpenTictactoe==false) {
            joinAnchorTictactoe.setVisible(true);
            joinLobbyOpenTictactoe=true;
            //calls fake populate to populate the lobbies
            matchmaker.fakePopulateLobby(currentUser);
            //grabs all the queues for game types (which can be used to show users in the queue)
            HashMap<GameType,List<User>> queue=matchmaker.getCurrentQueues();

            lobbyListTictactoe.getItems().clear();//clear the list
            listedUsers.clear();

            //get the list of all users waiting for c4 from the que. (return an empty lsit if no useres in in yet)
            List<User> waitingRoom=queue.getOrDefault(GameType.TIC_TAC_TOE,new ArrayList<>());

            for(User users:waitingRoom){
                //skip the current user so they dont appear in the lobby list
                if(!users.getUUID().equals(currentUser.getUUID())){
                    //add a string to the listview showing the other users name and level for that specific game
                    lobbyListTictactoe.getItems().add(users.getUsername()+ "'s waiting! LVL: "+ users.getLevel(GameType.TIC_TAC_TOE));
                    //add the user to another lsit for later reference
                    listedUsers.add(users);

                }
            }

        }else if (joinLobbyOpenTictactoe==true) {
            joinAnchorTictactoe.setVisible(false);
            joinLobbyOpenTictactoe=false;
            listedUsers.clear();

            lobbyListTictactoe.getItems().clear(); //clear the list

        }

    }
    // hover functionality for joining TTT button
    @FXML
    void joinTTTHoverEnter() {
        joinTTT.setScaleX(1.1);
        joinTTT.setScaleY(1.1);
    }
    @FXML
    void joinTTTHoverExit(){
        joinTTT.setScaleX(1.0);
        joinTTT.setScaleY(1.0);
    }

    /**
     * close anchorPanes
     * */
    public void closeQueueAnchorConnect4() {
        queueAnchorConnect4.setVisible(false);
        createLobbyOpenConnect4=true;
    }
    public void closeJoinAnchorConnect4() {
        joinAnchorConnect4.setVisible(false);
        joinLobbyOpenConnect4=true;
    }
    public void closeJoinAnchorTictactoe() {
        joinAnchorTictactoe.setVisible(false);
        joinLobbyOpenTictactoe=true;
    }


    /**
     * when player is put into a queue for TTT, switch to the TTT queue screen
     * */
    @FXML
    public void jumptoQueueScreenTTT(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("QueueScreenTTT.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            QueueScreen_TTTController controller = fxmlLoader.getController(); // was LoadingscreenTTTController
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            controller.setupMatchmaking(currentUser, matchmaker, stage); // was setupMatchmaking on loading screen
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * when player is put into a queue fpr C4, switch to the C4 queue screen
     * */
    @FXML
    public void jumptoQueueScreenC4(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("QueueScreenC4.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            QueueScreen_C4Controller controller = fxmlLoader.getController(); // was LoadingscreenTTTController
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            controller.setupMatchmaking(currentUser, matchmaker, stage); // was setupMatchmaking on loading screen
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * search function to search for other players
     * if the player doesn't exist in the database, an error message shows up
     * */
    @FXML
    void searchForPlayers() throws Exception {
        String otherPlayerName = searchBar.getText().trim();
        User otherUser = Database.getUserFromUsername(otherPlayerName);

        if (otherUser != null) {
            // switch to that user's profile
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("profile.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), 600, 400);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ProfileController controller = fxmlLoader.getController();
            controller.setDisplayedUser(otherUser);
            Stage stage = (Stage) searchButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else {
            errorMessage.setText("Player does not exist.");
            errorMessage.setStyle("-fx-text-fill: #000000");
        }
    }

    // hover functionality for search button
    @FXML
    void searchHoverEnter() {
        searchButton.setScaleX(1.1);
        searchButton.setScaleY(1.1);
    }
    @FXML
    void searchHoverExit(){
        searchButton.setScaleX(1.0);
        searchButton.setScaleY(1.0);
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

    /**
     * close anchorPane for TTT queue
     * */
    @FXML
    void closeQueueAnchor() {
        queueAnchor.setVisible(false);
        createLobbyOpen=true;
    }

    /**
     * switch to the leaderboard.fxml
     * */
    public void goToRank() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("leaderboard.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) rankButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    // hover functionality for rank button
    @FXML
    void rankHoverEnter() {
        rankButton.setScaleX(1.1);
        rankButton.setScaleY(1.1);
    }
    @FXML
    void rankHoverExit(){
        rankButton.setScaleX(1.0);
        rankButton.setScaleY(1.0);
    }

    /**
     * on click to user profile, switch scenes to profile.fxml
     * */
    @FXML
    void goToProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("profile.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        // Retrieve profile controller instance
        ProfileController controller = fxmlLoader.getController();

        // Initialize the profile view with currentUser
        controller.setDisplayedUser(currentUser);

        Stage stage  = (Stage) toProfile.getScene().getWindow();
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
     * on click to home, close side panel (already on home page)
     * */
    @FXML
    void goToHome() {
        sidePane.setVisible(false);
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
}
