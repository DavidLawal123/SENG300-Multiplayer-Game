package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.GameType;

import java.util.List;
import java.util.Objects;

public class GOEMFactory {
    public GOEMFactory() {}

    public GameOutcomeEvaluatorManager buildGOEM(GameType gameType) {
        Objects.requireNonNull(gameType);

        return switch (gameType) {
            case TIC_TAC_TOE -> new GameOutcomeEvaluatorManager(List.of(new TTTGameOutcomeEvaluator()));
            case CONNECT_FOUR -> new GameOutcomeEvaluatorManager(List.of(new FourCGameOutcomeEvaluator()));
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        };
    }
}