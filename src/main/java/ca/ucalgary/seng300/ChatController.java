package ca.ucalgary.seng300;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatController {
    @FXML
    private TextField chatField; // message field

    @FXML
    private ScrollPane scrollPane; // for automatic scrolling

    @FXML
    private VBox displayMessages; // to show messages

    @FXML
    private ImageView send; // send button

    OpponentChats opponentChats = new OpponentChats();
    User currentUser = Database.getUserFromUUID(SessionData.getCurrentLoggedInUUID());

    /**
     * bind the vertical property of the scrollPane and the VBox together
     * allows the scrollPane to automatically scroll up as new entries are added in the VBox
     * */
    @FXML
    void initialize() { // bind VBox and scrollPane together so they scroll when more messages made
        scrollPane.vvalueProperty().bind(displayMessages.heightProperty());
    }

    /**
     * for each new message sent, generate a new label and fix to an HBox
     * display the HBox in the VBox and clear the textfield to imitate "sent"
     * */
    @FXML
    void onSendClick() {
        String message =  chatField.getText();

        // generate a text for each message
        Text text = new Text(message);
        text.setStyle("-fx-text-fill: #6d6875");

        // link to a TextFlow container & set maxWidth to the size of the scrollPane
        TextFlow bubble = new TextFlow(text);
        bubble.setMaxWidth(scrollPane.getWidth());
        bubble.setStyle("-fx-background-color: #fae8e0; -fx-padding: 5; -fx-background-radius: 10;");

        // link to an HBox to implement alignment
        HBox container = new HBox(bubble);
        container.setMaxWidth(Double.MAX_VALUE);
        container.setAlignment(Pos.CENTER_RIGHT);

        // display the HBox("container")
        displayMessages.getChildren().add(container);

        // clear textfield like the message was sent
        chatField.clear();

        // a "reply" once a message is sent
        displayOpponentMessages(opponentChats.getOngoingReply());
    }

    // hover functionality for send message button
    @FXML
    void sendHoverEnter() {
        send.setScaleX(1.1);
        send.setScaleY(1.1);
    }
    @FXML
    void sendHoverExit(){
        send.setScaleX(1.0);
        send.setScaleY(1.0);
    }

    /**
     * display the opponent's message in the chat stage
     * */
    public void displayOpponentMessages(String oppMessage) {
        // generate a text for each message
        Text text = new Text(oppMessage);
        text.setStyle("-fx-text-fill: #6d6875;");

        // link to a TextFlow container & set maxWidth to the size of the scrollPane
        TextFlow bubble = new TextFlow(text);
        bubble.setMaxWidth(scrollPane.getWidth());
        bubble.setStyle("-fx-background-color: #ffc8c8; -fx-padding: 5; -fx-background-radius: 10;");

        // link to an HBox to implement alignment
        HBox container = new HBox(bubble);
        container.setMaxWidth(Double.MAX_VALUE);
        container.setAlignment(Pos.CENTER_LEFT);

        // display the HBox("container")
        displayMessages.getChildren().add(container);
    }

    /***
     * send a final message once a game ends (and if the chat is already open)
     */
    public void gameFinished(String winner) {
        if (winner.equals(currentUser.getUsername())) {
            displayOpponentMessages(opponentChats.getFinishedReply(false)); // user won
        }
        else {
            displayOpponentMessages(opponentChats.getFinishedReply(true)); // user lost
        }
    }
}
