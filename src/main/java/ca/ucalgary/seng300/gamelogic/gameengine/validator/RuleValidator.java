package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;

public abstract class RuleValidator extends MoveValidator{
    protected RuleValidator() {
        super();
    }

    public abstract ValidationResult validateMove(MoveContext ctx);
}
