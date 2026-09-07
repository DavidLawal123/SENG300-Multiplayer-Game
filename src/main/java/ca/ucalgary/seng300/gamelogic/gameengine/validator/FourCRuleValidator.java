package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.gamelogic.GameSessionFactory;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

import java.util.Objects;

public class FourCRuleValidator extends RuleValidator {
    protected FourCRuleValidator() {
        super();
    }

    /*
     * need to communicate with Client UI to implement this
     * they might be restricting the moves that client can make
     */
    @Override
    public ValidationResult validateMove(MoveContext ctx) {
        Objects.requireNonNull(ctx);

        GameState gs = ctx.getGameState();
        int bottomRow = gs.getNumRows()-1;

        int row = ctx.getRow();
        int col = ctx.getCol();

        // Must be empty.
        if (ctx.getGameState().getOccupancy(row, col).isPresent()) {
            return ValidationResult.ILLEGAL_MOVE;
        }

        // Gravity rule: checks if cell is at the very bottom
        if (bottomRow == row){
            return ValidationResult.VALID;
        }

        // Gravity rule: checks if the cell below does not have a piece placed
        if (ctx.getGameState().getOccupancy(row + 1, col).isEmpty()) {
            return ValidationResult.ILLEGAL_MOVE;
        }

        return ValidationResult.VALID;
    }
}