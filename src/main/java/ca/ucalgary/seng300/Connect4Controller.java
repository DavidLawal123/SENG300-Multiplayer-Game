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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class Connect4Controller {
    @FXML
    private ImageView chatButton; // chat button

    @FXML
    private Button exitGame; // exit game

    @FXML
    private Label p1Username; // display player 1 username

    @FXML
    private Label p2Username; // display player 2 username

    @FXML
    private AnchorPane player1; // highlight player 1

    @FXML
    private AnchorPane player2; // highlight player 2

    @FXML
    private Circle player1Pic; // display player 1 pfp

    @FXML
    private Circle player2Pic; // display player 2 pfp

    @FXML
    private Label winMessage; // win message

    @FXML
    private AnchorPane winPopUp; // win popup

    @FXML
    private GridPane c4Board; // board

    @FXML
    private StackPane column1;

    @FXML
    private StackPane column2;

    @FXML
    private StackPane column3;

    @FXML
    private StackPane column4;

    @FXML
    private StackPane column5;

    @FXML
    private StackPane column6;

    @FXML
    private StackPane column7;

    /**
     * start a new game, define the players, create a new move request
     * */
    private final GameCatalogService gcs= new GameCatalogService();
    private final ClientMoveRequestFactory cmrf = new ClientMoveRequestFactory();
    Player p1;
    Player p2;
    Player currentPlayer;
    private Circle[][] board = new Circle[6][7];
    private GameSession gameSession;
    User user1;
    User user2;

    /**
     * stylization: inflate the player display of the currently playing player
     *      indicates who's turn it is currently
     * stylize board
     * */
    @FXML
    void initialize() {
        //set turn
        player1.setScaleX(1.1);
        player1.setScaleY(1.1);
        player2.setScaleX(1.0);
        player2.setScaleY(1.0);

        winPopUp.setVisible(false);
        for(Node node : c4Board.getChildren()) {

           Integer rCell=GridPane.getRowIndex(node);
           int row;
           if(rCell==null) {
               row=0;

           }else{
               row=rCell;
           }

           Integer cCell=GridPane.getColumnIndex(node);
           int col;
           if(cCell==null) {
               col=0;
           }else{
               col=cCell;
           }
           if (!(node instanceof StackPane)) {
               continue;
           }

           StackPane cell=(StackPane)node;
           Circle circle=(Circle)cell.getChildren().get(0);

           board[row][col]=circle;
       }
    }

    /**
     * setup the game by displaying the player's pfp and username and the opponent's pfp and username
     * start the new game of type Connect4
     * */
    public void userSetup(User u1, User u2) {
        System.out.println("userSetup called");
        this.user1 = u1;
        this.user2 = u2;

        int pfp1 = u1.getProfilePicture();
        player1Pic.setFill(new ImagePattern(PFPManager.getProfilePicture(pfp1)));

        int pfp2 = u2.getProfilePicture();
        player2Pic.setFill(new ImagePattern(PFPManager.getProfilePicture(pfp2)));

        p1Username.setText(u1.getUsername());
        p2Username.setText(u2.getUsername());

        gameSession= gcs.startMatch(GameType.CONNECT_FOUR, List.of(u1, u2));

        p1= gameSession.getP1();
        p2= gameSession.getP2();

        currentPlayer=p1;
    }

    /**
     * mouseclick events for every square of the board
     * sends the coordinates to the logic handler clickBoard which will then update the UI
     * */
    @FXML
    void columnClick1(MouseEvent event) {
        columnClickDetected(0);
    }
    @FXML
    void columnClick2(MouseEvent event) {
        columnClickDetected(1);
    }
    @FXML
    void columnClick3(MouseEvent event) {
        columnClickDetected(2);
    }
    @FXML
    void columnClick4(MouseEvent event) {
        columnClickDetected(3);
    }
    @FXML
    void columnClick5(MouseEvent event) {
        columnClickDetected(4);
    }
    @FXML
    void columnClick6(MouseEvent event) {
        columnClickDetected(5);
    }
    @FXML
    void columnClick7(MouseEvent event) {
        columnClickDetected(6);
    }

    /**
     * gravity function for the game pieces
     * */
    private void columnClickDetected(int col){
        if(gameSession == null){
            System.out.println("null");
            return;
        }

        GameStateView st=gameSession.getGameStateView();

        for(int row=5; row>=0; row--){
            if(st.getOccupancy(row,col).isEmpty()){
                move(row,col);

                return;
            }
        }
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
    public void move(int row, int col) {

        Player movingPlayer = gameSession.getGameStateView().getPlayerTurnState();

        ClientMoveRequest request = cmrf.buildRequest((row),col,movingPlayer);

        MoveResponse mr= gameSession.submitMoveRequest(request);

        if(mr.getRequestStatus()== RequestStatus.ACCEPTED){

           updateBoardUI(row,col,movingPlayer);
           currentPlayer=gameSession.getGameStateView().getPlayerTurnState();

           GameStateView st=gameSession.getGameStateView();

           if(st.getGameOutcome()==GameOutcome.WIN){
               c4Board.setDisable(true);
               column1.setDisable(true);
               column2.setDisable(true);
               column3.setDisable(true);
               column4.setDisable(true);
               column5.setDisable(true);
               column6.setDisable(true);
               column7.setDisable(true);

               String winner=st.getWinner().getUsername();

               winMessage.setText(winner+" is the winner!");
               winPopUp.setVisible(true);
               winAnimation();

               // receive message from opponent
                    // if the chat is already open, call the stored ChatController
               // if not, don't send a message
               if (ChatToggleManager.isOpen()) {
                   ChatController cc = ChatToggleManager.getChatController();
                   cc.gameFinished(winner);
               }

           }else if(st.getGameOutcome()==GameOutcome.DRAW){
               c4Board.setDisable(true);
               column1.setDisable(true);
               column2.setDisable(true);
               column3.setDisable(true);
               column4.setDisable(true);
               column5.setDisable(true);
               column6.setDisable(true);
               column7.setDisable(true);

               winMessage.setText("IT WAS AAAA TIEEEE");
               winPopUp.setVisible(true);
           }
        }
    }

    /**
     * update the UI of the board if a move is valid
     *      player 1 = X
     *      player 2 = O
     * inflate the next player's display
     * */
    private void updateBoardUI(int row, int col, Player player) {
        Circle circle = board[row][col];
        if (circle == null) {
            return;
        }
        if(player.getUsername().equals(p1.getUsername())){
            circle.setFill(Color.web("ffc8c8"));
            // visually switch players
            player1.setScaleX(1.0);
            player1.setScaleY(1.0);
            player2.setScaleX(1.1);
            player2.setScaleY(1.1);
        }else{
            circle.setFill(Color.web("B5838D"));
            // visually switch players
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
    // hover functionality for exit button
    @FXML
    void exitHoverEnter() {
        exitGame.setScaleX(1.1);
        exitGame.setScaleY(1.1);
    }
    @FXML
    void exitHoverExit(){
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
