package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.context.ClientMoveRequest;
import ca.ucalgary.seng300.gamelogic.context.ClientMoveRequestFactory;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngineFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class GameSessionTests {

    private GameSession gameSession;
    private Player player1;
    private Player player2;
    private GameEngine gameEngine;
    private GameState gameState;
    private ClientMoveRequestFactory clientMoveRequestFactory;

    @Before
    public void settingUp() {
        player1 = makingPlayer("alice");
        player2 = makingPlayer("bob");

        clientMoveRequestFactory = new ClientMoveRequestFactory();

        GameStateFactory factory = new GameStateFactory();
        gameState = factory.buildGameState(Arrays.asList(player1, player2), GameType.TIC_TAC_TOE);

        GameEngineFactory gameEngineFactory = new GameEngineFactory();
        gameEngine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);

        gameSession = new GameSession(GameType.TIC_TAC_TOE, Arrays.asList(player1, player2), gameEngine, gameState);
    }

    private Player makingPlayer(String name) {
        User user = new User(name, name + "@mail.com", "pass123");
        UUID uuid = UUID.randomUUID();

        return new Player() {
            public UUID getUUID() { return uuid; }
            public String getUsername() { return user.getUsername(); }
        };
    }

    /**
     * This Tests that Constructor working successfully
     */
    @Test
    public void constructorWorking() {
        assertNotNull(gameSession);
    }

    /**
     * Throws when null game type.
     */
    @Test(expected = NullPointerException.class)
    public void constructorNullGameType() {
        new GameSession(null, Arrays.asList(player1, player2), gameEngine, gameState);
    }

    /**
     * Throws when invalid players.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorInvalidPlayers() {
        new GameSession(GameType.TIC_TAC_TOE, List.of(player1), gameEngine, gameState);
    }

    /**
     * getUUID works.
     */
    @Test
    public void getUUIDWorks() {
        assertNotNull(gameSession.getUUID());
    }

    /**
     * get players works.
     */
    @Test
    public void getPlayersWorks() {
        assertEquals(player1, gameSession.getP1());
        assertEquals(player2, gameSession.getP2());
    }

    /**
     * Null request throws.
     */
    @Test(expected = NullPointerException.class)
    public void submitMoveNull() {
        gameSession.submitMoveRequest(null);
    }

    /**
     * Unknown player rejected.
     */
    @Test
    public void rejectUnknownPlayer() {
        Player fake = makingPlayer("fake");

        ClientMoveRequest req = clientMoveRequestFactory.buildRequest(0, 0, fake);

        assertEquals(RequestStatus.REJECTED,
                gameSession.submitMoveRequest(req).getRequestStatus());
    }

    /**
     * Valid move accepted.
     */
    @Test
    public void acceptValidMove() {
        ClientMoveRequest req = clientMoveRequestFactory.buildRequest(0, 0, player1);

        assertEquals(RequestStatus.ACCEPTED,
                gameSession.submitMoveRequest(req).getRequestStatus());
    }
}
