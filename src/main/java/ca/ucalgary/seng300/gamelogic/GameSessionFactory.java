package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngineFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;

import java.util.List;
import java.util.Objects;

public class GameSessionFactory {
    private final GameStateFactory gsf = new GameStateFactory();
    private final GameEngineFactory gef = new GameEngineFactory();

    protected GameSessionFactory() {}


    public GameSession buildGameSession(GameType gameType, List<Player> players) {
        Objects.requireNonNull(gameType);
        Objects.requireNonNull(players);

        GameState gs = gsf.buildGameState(players, gameType);
        GameEngine engine = gef.buildGameEngine(gameType);

        return new GameSession(gameType, players, engine, gs);
    }
}