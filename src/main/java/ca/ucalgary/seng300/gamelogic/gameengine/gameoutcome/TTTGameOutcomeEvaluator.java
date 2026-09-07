package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.gamestate.Cell;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

import java.util.List;
import java.util.Optional;

public class TTTGameOutcomeEvaluator extends GameOutcomeEvaluator {
    public TTTGameOutcomeEvaluator() {
        super(3);
    }

    @Override
    protected GameOutcome evaluateGameOutcome(MoveContext ctx) {
        Optional<List<Cell>> nodes = GameOutcomeEvaluator.Search.winSearch(ctx, this.getWinLength());
        GameState gameState = ctx.getGameState();

        // only return win state, but not who won => GameSession can handle this
        if (nodes.isPresent()){
            // state transition
            gameState.setGameOutcome(GameOutcome.WIN);
            return GameOutcome.WIN;
        }
        GameOutcome outcome = gameState.isFull() ? GameOutcome.DRAW : GameOutcome.ONGOING;

        // state transitions
        if (outcome.name().equals(GameOutcome.ONGOING.name())){
            gameState.setGameOutcome(GameOutcome.ONGOING);
        } else {
            gameState.setGameOutcome(GameOutcome.DRAW);
        }
        return outcome;
    }
}