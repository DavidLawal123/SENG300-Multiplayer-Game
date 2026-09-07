package ca.ucalgary.seng300.gamelogic.moveresponse;

import ca.ucalgary.seng300.gamelogic.RequestStatus;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateView;

public class MoveResponse {
    private final RequestStatus requestStatus;
    private final ValidationResult validationResult;
    private final GameStateView gsv;

    protected MoveResponse(final RequestStatus requestStatus, final ValidationResult validationResult, GameStateView gsv) {
        this.requestStatus = requestStatus;
        this.validationResult = validationResult;
        this.gsv = gsv;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public GameStateView getGameStateView() {
        return gsv;
    }
}