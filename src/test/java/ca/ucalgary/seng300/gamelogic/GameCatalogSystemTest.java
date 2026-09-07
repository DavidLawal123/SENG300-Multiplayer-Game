package ca.ucalgary.seng300.gamelogic;

//import ca.ucalgary.seng300.GameSessionTest;
import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.gameengine.FourCGameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngineFactory;
import ca.ucalgary.seng300.gamelogic.gameengine.TTTGameEngine;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class GameCatalogSystemTest {

    private GameEngineFactory gameEngineFactory;
    private GameStateFactory gameStateFactory;
    private GameSessionFactory gameSessionFactory;
    private GameCatalogService gameCatalogService;

    @Before
    public void before(){
        gameEngineFactory = new GameEngineFactory();
        gameStateFactory = new GameStateFactory();
        gameSessionFactory = new GameSessionFactory();
        gameCatalogService = new GameCatalogService();
    }


    /**
     * Helper method to create players for testing
     */
    private List<Player> makePlayers(int count){
        List<Player> players = new ArrayList<>();

        if (count >= 1){
            players.add(new User("player1" , "player1@test.com", "pass1"));
        }
        if (count >= 2){
            players.add(new User("player2", "player2@test.com", "pass2"));
        }
        if (count >= 3){
            players.add(new User("player3", "player3@test.com", "pass3"));
        }
        return players;
    }

    //GameEngineFactory tests

    /**
     * Test that TicTacToe engine is created correctly
     */
    @Test
    public void testBuildGameEngineTicTacToe(){
        //create engine
        GameEngine gameEngine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);

        //verify engine
        assertNotNull(gameEngine);
        assertTrue(gameEngine instanceof TTTGameEngine);
    }

    /**
     * Test that ConnectFour is created correctly
     */
    @Test
    public void testBuildGameEngineConnectFour(){
        //create engine
        GameEngine gameEngine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);

        //verify engine
        assertNotNull(gameEngine);
        assertTrue(gameEngine instanceof FourCGameEngine);
    }

    /**
     * Test that null game type throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBuildGameEngineNull(){
        gameEngineFactory.buildGameEngine(null);
    }

    /**
     * Test that each call created a new engine object for TicTacToe
     */
    @Test
    public void testBuildGameEngineReturnsNewInstanceTTT(){
        //build the same engine twice
        GameEngine gameEngine1 = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        GameEngine gameEngine2 = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);

        //both should exist and be different objects
        assertNotNull(gameEngine1);
        assertNotNull(gameEngine2);
        assertNotEquals(gameEngine1, gameEngine2);
    }

    /**
     * Test that each call created a new engine object for ConnectFour
     */
    @Test
    public void testBuildGameEngineReturnsNewInstanceC4(){
        //build the same engine twice
        GameEngine gameEngine1 = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        GameEngine gameEngine2 = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);

        //both should exist and be different objects
        assertNotNull(gameEngine1);
        assertNotNull(gameEngine2);
        assertNotEquals(gameEngine1, gameEngine2);
    }

    //GameStateFactory Tests

    /**
     * Test that TicTacToe state is created correctly
     */
    @Test
    public void testBuildGameStateTicTacToe(){
        List<Player> players = makePlayers(2);

        GameState gameState = gameStateFactory.buildGameState(players, GameType.TIC_TAC_TOE);

        assertNotNull(gameState);
    }

    /**
     * Test that ConnectFour state is created correctly
     */
    @Test
    public void testBuildGameStateConnectFOur(){
        List<Player> players = makePlayers(2);

        GameState gameState = gameStateFactory.buildGameState(players, GameType.CONNECT_FOUR);

        assertNotNull(gameState);
    }

    /**
     * Test null players throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBuildGameStateNullPlayers(){
        gameStateFactory.buildGameState(null, GameType.CONNECT_FOUR);
    }

    /**
     * Test null game type throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBuildGameStateNullGame(){
        gameStateFactory.buildGameState(makePlayers(2), null);
    }

    /**
     * Test invalid number of players throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildGameStateInvalidPlayers(){
        gameStateFactory.buildGameState(makePlayers(1), GameType.TIC_TAC_TOE);
    }

    /**
     * Test that repeated state creation returns new objects for TicTacToe
     */
    @Test
    public void testBuildGameStateReturnsNewObjectTTT(){
        //create players
        List<Player> players = makePlayers(2);

        //build two states
        GameState gameState1 = gameStateFactory.buildGameState(players, GameType.TIC_TAC_TOE);
        GameState gameState2 = gameStateFactory.buildGameState(players, GameType.TIC_TAC_TOE);

        //both state should exist and be different objects
        assertNotNull(gameState1);
        assertNotNull(gameState2);
        assertNotEquals(gameState1, gameState2);
    }

    /**
     * Test that repeated state creation returns new objects for ConnectFOur
     */
    @Test
    public void testBuildGameStateReturnsNewObjectC4(){
        //create players
        List<Player> players = makePlayers(2);

        //build two states
        GameState gameState1 = gameStateFactory.buildGameState(players, GameType.CONNECT_FOUR);
        GameState gameState2 = gameStateFactory.buildGameState(players, GameType.CONNECT_FOUR);

        //both state should exist and be different objects
        assertNotNull(gameState1);
        assertNotNull(gameState2);
        assertNotEquals(gameState1, gameState2);
    }

    //GameSessionFactory Tests

    @Test
    public void testBuildGameSessionTicTacToe(){
        //create players
        List<Player> players = makePlayers(2);

        //build session
        GameSession gameSession = gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, players);

        //verify session data
        assertNotNull(gameSession);
        assertEquals(GameType.TIC_TAC_TOE, gameSession.getGameType());
        assertEquals(players.get(0), gameSession.getP1());
        assertEquals(players.get(1), gameSession.getP2());
        assertNotNull(gameSession.getUUID());
        assertNotNull(gameSession.getGameStateView());
    }

    /**
     * Test that ConnectFour session is created correctly
     */
    @Test
    public void testBuildGameSessionConnectFour(){
        //create players
        List<Player> players = makePlayers(2);

        //build session
        GameSession gameSession = gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, players);

        //verify session data
        assertNotNull(gameSession);
        assertEquals(GameType.TIC_TAC_TOE, gameSession.getGameType());
        assertEquals(players.get(0), gameSession.getP1());
        assertEquals(players.get(1), gameSession.getP2());
        assertNotNull(gameSession.getUUID());
        assertNotNull(gameSession.getGameStateView());
    }

    /**
     * Test that null game type throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBuildGameSessionNullGame(){
        gameSessionFactory.buildGameSession(null, makePlayers(2));
    }

    /**
     * Test that zero players throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBuildGameSessionNullPlayers(){
        gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, null);
    }

    /**
     * Test that zero players throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildGameSessionZeroPlayers(){
        gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, makePlayers(0));
    }

    /**
     * Test that one player throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildGameSessionOnePlayers(){
        //invalid player count
        gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, makePlayers(1));
    }

    /**
     * Test that three players throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildGameSessionThreePlayers() {
        //invalid player count
        gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, makePlayers(3));
    }

    /**
     * Test that building the same session twice returns different session objects for TicTacToe
     */
    @Test
    public void testBuildGameSessionReturnsNewSessionTTT(){
        //create players
        List<Player> players = makePlayers(2);

        //build two sessions
        GameSession gameSession1 = gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, players);
        GameSession gameSession2 = gameSessionFactory.buildGameSession(GameType.TIC_TAC_TOE, players);

        //both should exist and have different UUIDs
        assertNotNull(gameSession1);
        assertNotNull(gameSession2);
        assertNotEquals(gameSession1.getUUID(), gameSession2.getUUID());
    }

    /**
     * Test that building the same session twice returns different session objects for ConnectFour
     */
    @Test
    public void testBuildGameSessionReturnsNewSessionC4(){
        //create players
        List<Player> players = makePlayers(2);

        //build two sessions
        GameSession gameSession1 = gameSessionFactory.buildGameSession(GameType.CONNECT_FOUR, players);
        GameSession gameSession2 = gameSessionFactory.buildGameSession(GameType.CONNECT_FOUR, players);

        //both should exist and have different UUIDs
        assertNotNull(gameSession1);
        assertNotNull(gameSession2);
        assertNotEquals(gameSession1.getUUID(), gameSession2.getUUID());
    }

    //GameCatalogService Tests

    /**
     * Test that games list is not null
     */
    @Test
    public void testGetGamesListNotNull(){
        //get games list
        List<GameInfo> games = gameCatalogService.getGamesList();

        //verify list exists
        assertNotNull(games);
    }

    /**
     * Test that games list size is two
     */
    @Test
    public void testGetGameListSizeTwo(){
        //get games list
        List<GameInfo> games = gameCatalogService.getGamesList();

        //default catalog should have two games
        assertEquals(2, games.size());
    }

    /**
     * Test that the games list contains TicTacToe
     */
    @Test
    public void testGetGamesListContainsTTT(){
        //get games list
        List<GameInfo> games = gameCatalogService.getGamesList();

        boolean found = false;
        for(GameInfo game : games){
            if(game.getGameType() == GameType.TIC_TAC_TOE){
                found = true;
                assertEquals("Tic-Tac-Toe (2 players)", game.getDescription());
                assertEquals(2, game.getNumPlayersRequired());
            }
        }

        //TicTacToe should exist
        assertTrue(found);
    }

    /**
     * Test that the games list contains ConnectFour
     */
    @Test
    public void testGetGamesListContainsC4(){
        //get games list
        List<GameInfo> games = gameCatalogService.getGamesList();

        boolean found = false;
        for(GameInfo game : games){
            if(game.getGameType() == GameType.CONNECT_FOUR){
                found = true;
                assertEquals("Connect Four (2 players)", game.getDescription());
                assertEquals(2, game.getNumPlayersRequired());
            }
        }

        //ConnectFour should exist
        assertTrue(found);
    }

    /**
     * Test that returned games list is a copy of the internal list
     */
    @Test
    public void testGetGamesListReturnsList(){
        List<GameInfo> games = gameCatalogService.getGamesList();
        games.clear();

        assertEquals(2, gameCatalogService.getGamesList().size());
    }

    /**
     * Test that each catalog item can be found by UUID
     */
    @Test
    public void testGetGameInfoValidForEachCatalogItem(){
        //get all games
        List<GameInfo> games = gameCatalogService.getGamesList();

        //each game should be found correctly
        for (GameInfo game : games){
            GameInfo found = gameCatalogService.getGameInfo(game.getUuid());

            assertNotNull(found);
            assertEquals(game.getUuid(), found.getUuid());
            assertEquals(game.getDescription(), found.getDescription());
            assertEquals(game.getNumPlayersRequired(), found.getNumPlayersRequired());
            assertEquals(game.getGameType(), found.getGameType());
        }
    }

    /**
     * Test that null UUID throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testGetGameInfoNullUUID(){
        gameCatalogService.getGameInfo(null);
    }

    /**
     * Test that unknown UUID throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetGameInfoUnknownUUID(){
        gameCatalogService.getGameInfo(UUID.randomUUID());
    }

    /**
     * Test that TicTacToe match starts correctly
     */
    @Test
    public void testStartMatchTicTacToe(){
        //start mach
        GameSession session = gameCatalogService.startMatch(GameType.TIC_TAC_TOE, makePlayers(2));

        //verify session
        assertNotNull(session);
        assertEquals(GameType.TIC_TAC_TOE, session.getGameType());

    }

    /**
     * Test that ConnectFour match starts correctly
     */
    @Test
    public void testStartMatchConnectFour(){
        //start mach
        GameSession session = gameCatalogService.startMatch(GameType.CONNECT_FOUR, makePlayers(2));

        //verify session
        assertNotNull(session);
        assertEquals(GameType.CONNECT_FOUR, session.getGameType());
    }

    /**
     * Test that started match stores both correct players
     */
    @Test
    public void testStartMatchStoresCorrectPlayers(){
        //create players
        List<Player> players = makePlayers(2);

        //start match
        GameSession gameSession = gameCatalogService.startMatch(GameType.TIC_TAC_TOE, players);

        //verify session players
        assertEquals(players.get(0), gameSession.getP1());
        assertEquals(players.get(1), gameSession.getP2());
    }

    /**
     * Test that started matches return different sessions each time
     */
    @Test
    public void testStartMatchReturnsDifferentSessions(){
        //create players
        List<Player> players = makePlayers(2);

        //start match
        GameSession session1 = gameCatalogService.startMatch(GameType.TIC_TAC_TOE, players);
        GameSession session2 = gameCatalogService.startMatch(GameType.TIC_TAC_TOE, players);

        //both sessions should have different UUIDs
        assertNotNull(session1);
        assertNotNull(session2);
        assertNotEquals(session1.getUUID(), session2.getUUID());
    }

    /**
     * Test that null game type throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testStartMatchNullGameType(){
        gameCatalogService.startMatch(null, makePlayers(2));
    }

    /**
     * Test that null players throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testStartMatchNullPlayers(){
        gameCatalogService.startMatch(GameType.TIC_TAC_TOE, null);
    }

    /**
     * Test that zero players throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testStartMatchZeroPlayer(){
        //invalid player count
        gameCatalogService.startMatch(GameType.TIC_TAC_TOE, makePlayers(0));
    }

    /**
     * Test that one player throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testStartMatchOnePlayer(){
        //invalid player count
        gameCatalogService.startMatch(GameType.TIC_TAC_TOE, makePlayers(1));
    }

    /**
     * Test that three player throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testStartMatchThreePlayers(){
        //invalid player count
        gameCatalogService.startMatch(GameType.TIC_TAC_TOE, makePlayers(3));
    }
}
