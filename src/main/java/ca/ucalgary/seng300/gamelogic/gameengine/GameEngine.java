package ca.ucalgary.seng300.gamelogic.gameengine;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome.GameOutcomeEvaluatorManager;
import ca.ucalgary.seng300.gamelogic.gameengine.validator.MoveValidationManager;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

import java.util.Objects;

public abstract class GameEngine {
    private final MoveValidationManager mvm;
    private final GameOutcomeEvaluatorManager goem;

    protected GameEngine(MoveValidationManager mvm, GameOutcomeEvaluatorManager goem) {
        this.mvm = Objects.requireNonNull(mvm);
        this.goem = Objects.requireNonNull(goem);
    }

    public ValidationResult validateMove(MoveContext ctx) {
        Objects.requireNonNull(ctx);
        return mvm.validateMove(ctx);
    }

    public abstract void applyMove(MoveContext ctx);

    public GameOutcome evaluateGameOutcome(MoveContext ctx) {
        Objects.requireNonNull(ctx);
        return goem.evaluateOutcome(ctx);
    }

    public void applyGameOutcomeTransition(MoveContext ctx, GameOutcome gameOutcome){
        GameState gameState = ctx.getGameState();
        Player p1 = gameState.getP1();
        Player p2 = gameState.getP2();

        // player who made the move request
        Player requester = ctx.getPlayer();

        // player who is idle; not making a move
        Player opposition = requester.getUUID() == p1.getUUID() ? p2 : p1;

        // state transitions
        if (gameOutcome == GameOutcome.WIN){
            gameState.setGameOutcome(GameOutcome.WIN);
            gameState.setWinner(requester);
            gameState.setLoser(opposition);
        } else if (gameOutcome == GameOutcome.DRAW){
            gameState.setGameOutcome(GameOutcome.DRAW);
        } else {
            gameState.setGameOutcome(GameOutcome.ONGOING);
        }
    }

    public void applyPlayerTurnStateTransition(MoveContext ctx, Player nextPlayer){
        ctx.getGameState().setPlayerTurnState(nextPlayer);
    }
}