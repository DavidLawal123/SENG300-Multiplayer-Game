package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;

import java.util.Objects;

public class TTTRuleValidator extends RuleValidator {
    protected TTTRuleValidator() {
        super();
    }

    @Override
    public ValidationResult validateMove(MoveContext ctx) {
        Objects.requireNonNull(ctx);

        // Must place into an empty cell.
        if (ctx.getGameState().getOccupancy(ctx.getRow(), ctx.getCol()).isPresent()) {
            return ValidationResult.ILLEGAL_MOVE;
        }
        return ValidationResult.VALID;
    }
}