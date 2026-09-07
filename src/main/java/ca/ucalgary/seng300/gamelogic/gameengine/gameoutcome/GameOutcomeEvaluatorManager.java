package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;

import java.util.List;
import java.util.Objects;

public class GameOutcomeEvaluatorManager {
    private final List<GameOutcomeEvaluator> pipeline;

    protected GameOutcomeEvaluatorManager(List<GameOutcomeEvaluator> pipeline) {
        this.pipeline = Objects.requireNonNull(pipeline);
    }

    public GameOutcome evaluateOutcome(MoveContext ctx) {
        Objects.requireNonNull(ctx);

        for (GameOutcomeEvaluator e : pipeline) {
            GameOutcome o = e.evaluateGameOutcome(ctx);
            if (o != GameOutcome.ONGOING) {
                return o;
            }
        }
        return GameOutcome.ONGOING;
    }
}