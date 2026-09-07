package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MVMFactory {
    public MVMFactory() {}

    public MoveValidationManager buildMVM(GameType gameType) {
        Objects.requireNonNull(gameType);

        List<MoveValidator> pipeline = new ArrayList<>();
        pipeline.add(new TurnValidator());
        pipeline.add(new BoundsValidator());

        switch (gameType) {
            case TIC_TAC_TOE -> pipeline.add(new TTTRuleValidator());
            case CONNECT_FOUR -> pipeline.add(new FourCRuleValidator());
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }

        return new MoveValidationManager(pipeline);
    }
}