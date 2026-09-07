package ca.ucalgary.seng300.gamelogic.gameengine;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome.GOEMFactory;
import ca.ucalgary.seng300.gamelogic.gameengine.validator.MVMFactory;

import java.util.Objects;

public class GameEngineFactory {
    private final MVMFactory mvmFactory = new MVMFactory();
    private final GOEMFactory goemFactory = new GOEMFactory();

    public GameEngineFactory() {}

    public GameEngine buildGameEngine(GameType gameType) {
        Objects.requireNonNull(gameType);

        var mvm = mvmFactory.buildMVM(gameType);
        var goem = goemFactory.buildGOEM(gameType);

        return switch (gameType) {
            case TIC_TAC_TOE -> new TTTGameEngine(mvm, goem);
            case CONNECT_FOUR -> new FourCGameEngine(mvm, goem);
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        };
    }
}