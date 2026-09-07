package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;

public abstract class MoveValidator {
    protected MoveValidator() {}
    public abstract ValidationResult validateMove(MoveContext ctx);
}
