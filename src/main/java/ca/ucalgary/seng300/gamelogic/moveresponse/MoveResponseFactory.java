package ca.ucalgary.seng300.gamelogic.moveresponse;

import ca.ucalgary.seng300.gamelogic.RequestStatus;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateView;

public class MoveResponseFactory {
    public MoveResponseFactory() {}

    public MoveResponse buildAcceptedResponse(GameStateView gsv){
        return new MoveResponse(RequestStatus.ACCEPTED, ValidationResult.VALID, gsv);
    }

    public MoveResponse buildRejectedResponse(ValidationResult validationResult, GameStateView gsv){
        return new MoveResponse(RequestStatus.REJECTED, validationResult, gsv);
    }
}
