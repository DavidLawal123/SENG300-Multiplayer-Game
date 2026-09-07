package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

import java.util.Objects;

public class TurnValidator extends MoveValidator {
    protected TurnValidator() {
        super();
    }

    @Override
    public ValidationResult validateMove(MoveContext ctx) {
        Objects.requireNonNull(ctx);

        GameState gs = ctx.getGameState();
        Player player = ctx.getPlayer();
        Player currentPlayerTurnState = gs.getPlayerTurnState();

        if (!player.getUUID().equals(currentPlayerTurnState.getUUID())) {
            return ValidationResult.INVALID_TURN;
        }
        return ValidationResult.VALID;
    }
}