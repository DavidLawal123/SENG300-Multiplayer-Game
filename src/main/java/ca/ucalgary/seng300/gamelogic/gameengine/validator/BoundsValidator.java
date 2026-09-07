package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

import java.util.Objects;

public class BoundsValidator extends MoveValidator {
    protected BoundsValidator() {
        super();
    }

    @Override
    public ValidationResult validateMove(MoveContext ctx) {
        Objects.requireNonNull(ctx);

        GameState gs = ctx.getGameState();
        int row = ctx.getRow();
        int col = ctx.getCol();

        if (!gs.inBounds(row, col)) {
            return ValidationResult.INVALID_BOUND;
        }
        return ValidationResult.VALID;
    }
}