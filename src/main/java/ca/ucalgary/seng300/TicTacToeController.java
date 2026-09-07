package ca.ucalgary.seng300;

import ca.ucalgary.seng300.gamelogic.*;
import ca.ucalgary.seng300.gamelogic.GameSession;
import ca.ucalgary.seng300.gamelogic.context.ClientMoveRequest;
import ca.ucalgary.seng300.gamelogic.context.ClientMoveRequestFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateView;
import ca.ucalgary.seng300.gamelogic.moveresponse.MoveResponse;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class TicTacToeController {
    @FXML
    private GridPane TTTboard; // the game board

    @FXML
    private AnchorPane player1; // player 1 display

    @FXML
    private AnchorPane player2; // player 2 display

    @FXML
    private Circle p1Pic; // player 1 profile picture (display)

    @FXML
    private Circle p2Pic; // player 2 profile picture (display)

    @FXML
    private Label p1Username; // player 1 username (display)

    @FXML
    private Label p2Username; // player 2 username (display)

    @FXML
    private AnchorPane winPopUp; // ending message popup

    @FXML
    private Label winMessage; // message itself

    @FXML
    private Button exitGame; // button to exit game and return to main page

    @FXML
    private ImageView chatButton; // button to chat in-game

    /**
     * start a new game, define the players, create a new move request
     * */
    private final GameCatalogService newGame = new  GameCatalogService();
    private GameSession gameSession;
    Player p1;
    Player p2;
    Player current; // player currently playing
    User user1;
    User user2;
    private final ClientMoveRequestFactory cmrf = new ClientMoveRequestFactory();
    ClientMoveRequest request;

    /**
     * setup the game by displaying the player's pfp and username and the opponent's pfp and username
     * start the new game of type TicTacToe
     * */
    public void userSetUp(User u1, User u2){
        // for updating results
        this.user1 = u1;
        this.user2 = u2;

        // for game logic
        p1 = u1;
        p2 = u2;

        // for UI
        int pfp1 = u1.getProfilePicture();
        p1Pic.setFill(new ImagePattern(PFPManager.getProfilePicture(pfp1)));

        int pfp2 = u2.getProfilePicture();
        p2Pic.setFill(new ImagePattern(PFPManager.getProfilePicture(pfp2)));

        p1Username.setText(u1.getUsername());
        p2Username.setText(u2.getUsername());

        gameSession = newGame.startMatch(GameType.TIC_TAC_TOE, List.of(p1,p2));
        current = p1;
    }

    /**
     * stylization: inflate the player display of the currently playing player
     *      indicates who's turn it is currently
     * */
    @FXML
    void initialize() {
        player1.setScaleX(1.1);
        player1.setScaleY(1.1);
        player2.setScaleX(1.0);
        player2.setScaleY(1.0);

        winPopUp.setVisible(false);
    }

    /**
     * mouseclick events for every square of the gridpane
     * sends the coordinates to the logic handler clickBoard which will then update the UI
     * */
    @FXML
    void click00(MouseEvent event) {
        clickBoard(0,0);
    }
    @FXML
    void click01(MouseEvent event) {
        clickBoard(0,1);
    }
    @FXML
    void click02(MouseEvent event) {
        clickBoard(0,2);
    }
    @FXML
    void click10(MouseEvent event) {
        clickBoard(1,0);
    }
    @FXML
    void click11(MouseEvent event) {
        clickBoard(1,1);
    }
    @FXML
    void click12(MouseEvent event) {
        clickBoard(1,2);
    }
    @FXML
    void click20(MouseEvent event) {
        clickBoard(2,0);
    }
    @FXML
    void click21(MouseEvent event) {
        clickBoard(2,1);
    }
    @FXML
    void click22(MouseEvent event) {
        clickBoard(2,2);
    }

    /**
     * win animation for the ending message (WIN or DRAW)
     * */
    private void winAnimation(){
        winPopUp.setTranslateX(-600);
        winPopUp.setOpacity(0.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(700), winPopUp);
        tt.setToX(0);

        FadeTransition ft = new FadeTransition(Duration.millis(700), winPopUp);
        ft.setToValue(1);

        tt.play();
        ft.play();
    }

    /**
     * when a mouseclick is passed, submits a move request
     * if request accepted:
     *      update UI of the board
     *      search to see if the game is won, tied, or still ongoing
     * */
    public void clickBoard(int x, int y) {
        current = gameSession.getGameStateView().getPlayerTurnState(); // get current player
        request = cmrf.buildRequest(x,y,current);

        MoveResponse mr = gameSession.submitMoveRequest(request);

        if (mr.getRequestStatus() == RequestStatus.ACCEPTED) {
            updateBoard(x,y); // update the UI
            current = gameSession.getGameStateView().getPlayerTurnState(); // switch player

            GameStateView sv = gameSession.getGameStateView(); // get state after every move
            if (sv.getGameOutcome() == GameOutcome.WIN){
                TTTboard.setDisable(true); // freeze board
                // update message
                String winner = sv.getWinner().getUsername();
                winMessage.setText("CONGRATS, " + winner + " WON!");
                winPopUp.setVisible(true);
                winAnimation();

                // receive message from opponent
                // if the chat is already open, call the stored ChatController
                // if not, don't send a message
                if (ChatToggleManager.isOpen()) {
                    ChatController cc = ChatToggleManager.getChatController();
                    cc.gameFinished(winner);
                }
            }
            else if (sv.getGameOutcome() == GameOutcome.DRAW) { // if tie
                TTTboard.setDisable(true); // freeze board
                // update message
                winMessage.setText("TIE!");
                winPopUp.setVisible(true);
                winAnimation();
            }
        }
    }

    /**
     * update the UI of the board if a move is valid
     *      player 1 = X
     *      player 2 = O
     * inflate the next player's display
     * */
    private void updateBoard(int row, int col) {
        if (current.getUsername().equals(p1.getUsername())) {
            ImageView XPiece = new ImageView(MainApp.class.getResource("TTT_X.png").toExternalForm());
            XPiece.setFitWidth(100);
            XPiece.setFitHeight(100);
            TTTboard.add(XPiece,col,row);

            // potentially implement delay switching
            player1.setScaleX(1.0);
            player1.setScaleY(1.0);
            player2.setScaleX(1.1);
            player2.setScaleY(1.1);
        }
        else if (current.getUsername().equals(p2.getUsername())) {
            ImageView OPiece =  new ImageView(MainApp.class.getResource("TTT_O.png").toExternalForm());
            OPiece.setFitWidth(100);
            OPiece.setFitHeight(100);
            TTTboard.add(OPiece,col,row);

            // potentially implement delay switching
            player1.setScaleX(1.1);
            player1.setScaleY(1.1);
            player2.setScaleX(1.0);
            player2.setScaleY(1.0);
        }
    }

    /**
     * switch back to the main page once a game has ended
     * statistics are automatically updated vis GameSession classes
     * */
    @FXML
    void endGame() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainpage.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 600,400);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        Stage stage  = (Stage) exitGame.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // hover functionality for exit game button
    @FXML
    void exitHoverEnter() {
        exitGame.setScaleX(1.1);
        exitGame.setScaleY(1.1);
    }
    @FXML
    void exitHoverExit() {
        exitGame.setScaleX(1.0);
        exitGame.setScaleY(1.0);
    }

    /**
     * open and close chat from any interface with this button via ChatToggleManager
     */
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
}
