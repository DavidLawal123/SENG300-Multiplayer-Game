package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.GameSession;
import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class GameStateFactoryTests {

    private GameStateFactory stateFactory;
    private Player player1;
    private Player player2;

    @Before
    public void settingUp() {
        stateFactory = new GameStateFactory();
        player1 = makingPlayer("alice", "alice@example.com");
        player2 = makingPlayer("bob", "bob@example.com");
    }

    private Player makingPlayer(String username, String email) {
        User u = new User(username, email, "pass123");
        UUID uuid = UUID.randomUUID();

        return new Player() {
            @Override
            public UUID getUUID() {
                return uuid;
            }

            @Override
            public String getUsername() {
                return u.getUsername();
            }
        };
    }

    @Test
    public void constructorWorking() {
        assertNotNull(new GameStateFactory());
    }

    @Test(expected = NullPointerException.class)
    public void playerListNull() {
        stateFactory.buildGameState(null, GameType.TIC_TAC_TOE);
    }

    @Test(expected = NullPointerException.class)
    public void gameTypeNull() {
        stateFactory.buildGameState(List.of(player1, player2), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerListEmpty() {
        stateFactory.buildGameState(new ArrayList<>(), GameType.TIC_TAC_TOE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onlyOnePlayer() {
        stateFactory.buildGameState(List.of(player1), GameType.TIC_TAC_TOE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void threeplayers() {
        Player player3 = makingPlayer("charlie", "charlie@example.com");
        stateFactory.buildGameState(List.of(player1, player2, player3), GameType.TIC_TAC_TOE);
    }

    @Test
    public void buildingTicTacToeState() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        assertNotNull(gameState);
        assertEquals(3, gameState.getNumRows());
        assertEquals(3, gameState.getNumCols());
    }

    @Test
    public void buildingConnect4State() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.CONNECT_FOUR);

        assertNotNull(gameState);
        assertEquals(6, gameState.getNumRows());
        assertEquals(7, gameState.getNumCols());
    }

    @Test
    public void settingPlayersCorrectly() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        assertEquals(player1, gameState.getP1());
        assertEquals(player2, gameState.getP2());
    }

    @Test
    public void firstPlayerStarting(){
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        assertEquals(player1, gameState.getPlayerTurnState());
    }

    @Test
    public void defaultIsOngoing() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        assertEquals(GameOutcome.ONGOING, gameState.getGameOutcome());
    }

    @Test
    public void winnerStartingNull() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        assertNull(gameState.getWinner());
    }

    @Test
    public void loserStartingNull() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        assertNull(gameState.getLoser());
    }

    @Test
    public void TTTBoardStartsEmpty() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.TIC_TAC_TOE);

        for (int x = 0; x < gameState.getNumRows(); x++) {
            for (int y = 0; y < gameState.getNumCols(); y++) {
                assertFalse(gameState.getOccupancy(x, y).isPresent());
            }
        }
    }

    @Test
    public void C4BoardStartsEmpty() {
        GameState gameState = stateFactory.buildGameState(List.of(player1, player2), GameType.CONNECT_FOUR);

        for (int x = 0; x < gameState.getNumRows(); x++) {
            for (int y = 0; y < gameState.getNumCols(); y++) {
                assertFalse(gameState.getOccupancy(x, y).isPresent());
            }
        }
    }

    @Test
    public void TTTinBound() {
        List<Player> players = List.of(player1, player2);
        GameState gameState = stateFactory.buildGameState(players, GameType.TIC_TAC_TOE);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                assertTrue(gameState.inBounds(x, y));
            }
        }
    }

    @Test
    public void C4inBound() {
        List<Player> players = List.of(player1, player2);
        GameState gameState = stateFactory.buildGameState(players, GameType.CONNECT_FOUR);

        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 7; y++) {
                assertTrue(gameState.inBounds(x, y));
            }
        }
    }

}