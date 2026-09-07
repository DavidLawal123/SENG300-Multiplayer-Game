package ca.ucalgary.seng300;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoadingScreenC4Controller {

    @FXML
    private Label gameloading_label;
    private Timeline loadingAnimation;

    @FXML
    private Label funfact_label;
    private Timeline funFactAnimation;

    @FXML
    private ProgressBar loading_bar;
    private Timeline progressAnimation;

    private User currentUser;
    private Matchmaker matchmaker;

    @FXML
    public void initialize() {
        startLoadingAnimation();
        startFunFactAnimation();
        startProgressBarAnimation();
    }

    /**
     * fun fact animation cycling
     * */
    private void startFunFactAnimation() {
        String funtext_1 = "Sulfuric acid can melt bones and produces water vapour as a byproduct.";
        String funtext_2 = "You can bite through your finger like biting a carrot.";
        String funtext_3 = "You get entirely new skin every 26 days.";
        String[] funFacts = {funtext_1, funtext_2, funtext_3};
        int[] index = {0};

        funfact_label.setText(funFacts[0]);

        funFactAnimation = new Timeline(
                new KeyFrame(Duration.seconds(5), actionEvent -> {
                    index[0] = (index[0] + 1) % funFacts.length;
                    funfact_label.setText(funFacts[index[0]]);
                })
        );
        funFactAnimation.setCycleCount(Timeline.INDEFINITE);
        funFactAnimation.play();
    }

    /**
     * progress bar loading animation
     * */
    private void startLoadingAnimation() {
        String[] stats = {"Loading", "Loading.", "Loading..", "Loading..."};
        int[] index = {0};

        loadingAnimation = new Timeline(
                new KeyFrame(Duration.millis(500), event -> {
                    gameloading_label.setText(stats[index[0] % stats.length]);
                    index[0]++;
                })
        );
        loadingAnimation.setCycleCount(Timeline.INDEFINITE);
        loadingAnimation.play();
    }

    public void stopLoadingAnimation() {
        if (loadingAnimation != null) loadingAnimation.stop();
        if (funFactAnimation != null) funFactAnimation.stop();
        if (progressAnimation != null) progressAnimation.stop();
    }

    private Runnable onProgressComplete;

    private void startProgressBarAnimation() {
        double[] progress = {0.0};
        progressAnimation = new Timeline(
                new KeyFrame(Duration.millis(50), event -> {
                    progress[0] += 0.1;
                    loading_bar.setProgress(progress[0]);
                    if (progress[0] >= 1.0) {
                        progressAnimation.stop();
                        if (onProgressComplete != null) {
                            onProgressComplete.run();
                        }
                    }
                })
        );
        progressAnimation.setCycleCount(Timeline.INDEFINITE);
        progressAnimation.play();
    }

    /**
     * transition to the game interface
     * transfer user and opponent to the game controller
     * */
    public void setupForTransition(User user, User opponent, Matchmaker matchmaker, Stage stage) {
        this.currentUser = user;
        this.matchmaker = matchmaker;

        onProgressComplete = () -> {
            stopLoadingAnimation();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("connect4.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                Connect4Controller controller = fxmlLoader.getController();
                controller.userSetup(user, opponent);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
