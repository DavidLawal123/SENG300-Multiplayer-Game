package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;

import java.util.List;
import java.util.Objects;

public class MoveValidationManager {
    private final List<MoveValidator> pipeline;

    protected MoveValidationManager(List<MoveValidator> pipeline) {
        this.pipeline = Objects.requireNonNull(pipeline);
    }

    /**
     * Runs validators in order and returns the first non-VALID result.
     * If all validators pass, returns VALID.
     */
    public ValidationResult validateMove(MoveContext ctx) {
        Objects.requireNonNull(ctx);

        for (MoveValidator v : pipeline) {
            ValidationResult r = v.validateMove(ctx);
            if (r != ValidationResult.VALID) {
                return r;
            }
        }
        return ValidationResult.VALID;
    }
}