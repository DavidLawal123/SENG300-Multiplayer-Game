package ca.ucalgary.seng300.gamelogic.gameengine;

import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome.GameOutcomeEvaluatorManager;
import ca.ucalgary.seng300.gamelogic.gameengine.validator.MoveValidationManager;

public class FourCGameEngine extends GameEngine {
    protected FourCGameEngine(MoveValidationManager mvm, GameOutcomeEvaluatorManager goem) {
        super(mvm, goem);
    }

    @Override
    public void applyMove(MoveContext ctx) {
        ctx.getGameState().setOccupancy(ctx.getRow(), ctx.getCol(), ctx.getPlayer());
    }
}