package ca.ucalgary.seng300.gamelogic.gameengine;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.gamelogic.GameSession;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.context.MoveContextFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameEngineSystemTest {
    private GameEngineFactory gameEngineFactory;
    private GameStateFactory gameStateFactory;
    private MoveContextFactory moveContextFactory;

    /**
     * Set up objects before each test
     */
    @Before
    public void before(){
        //new objects before each test
        gameEngineFactory = new GameEngineFactory();
        gameStateFactory = new GameStateFactory();
        moveContextFactory = new MoveContextFactory();
    }

    /**
     * Helper method to create player 1
     */
    private Player makePlayer1(){
        return new User("player1", "player1@test.com", "pass1");
    }

    /**
     * Helper method to create player 2
     */
    private Player makePlayer2(){
        return new User("player2", "player2@test.com", "pass2");
    }

    /**
     * Helper method to create TicTacToe state.
     */
    private GameState TTTState(Player p1, Player p2) {
        //3x3 game state
        return gameStateFactory.buildGameState(List.of(p1, p2), GameType.TIC_TAC_TOE);
    }

    /**
     * Helper method to create ConnectFour state.
     */
    private GameState C4State(Player p1, Player p2) {
        //6x7 game state
        return gameStateFactory.buildGameState(List.of(p1, p2), GameType.CONNECT_FOUR);
    }

    /**
     * Helper method to create move context.
     */
    private MoveContext makeContext(int row, int col, Player player, GameState gs) {
        return moveContextFactory.buildCTX(row, col, player, gs);
    }

    //GameEngineFactory Tests

    /**
     * Test that TicTacToe engine is created correctly
     */
    @Test
    public void testBuildGameEngineTTT(){
        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);

        //engine should exist and be correct type
        assertNotNull(engine);
        assertTrue(engine instanceof TTTGameEngine);
    }

    /**
     * Test that ConnectFour engine is created correctly
     */
    @Test
    public void testBuildGameEngineC4(){
        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);

        //engine should exist and be correct type
        assertNotNull(engine);
        assertTrue(engine instanceof FourCGameEngine);
    }

    /**
     * Test that null game type throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBuildGameEngineNull(){
        gameEngineFactory.buildGameEngine(null);
    }

    /**
     * Test that building the same engine twice for TicTacToe returns different objects
     */
    @Test
    public void testBuildGameEngineReturnsNewInstanceEachTime() {
        //build engine twice
        GameEngine engine1 = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        GameEngine engine2 = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);

        //both should exist and be different objects
        assertNotNull(engine1);
        assertNotNull(engine2);
        assertNotSame(engine1, engine2);
    }

    /**
     * Test that building the same engine twice for ConnectFour returns different objects.
     */
    @Test
    public void testBuildConnectFourEngineReturnsNewInstanceEachTime() {
        //build engine twice
        GameEngine engine1 = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        GameEngine engine2 = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);

        //both should exist and be different objects
        assertNotNull(engine1);
        assertNotNull(engine2);
        assertNotSame(engine1, engine2);
    }

    //GameEngine Tests

    /**
     * Test that TicTacToe engine validates a correct move as VALID
     */
    @Test
    public void testTTTGameEngineValidateMoveValid(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //move should be valid
        assertEquals(ValidationResult.VALID, engine.validateMove(context));
    }

    /**
     * Test that TicTacToe engine catches an invalid turn
     */
    @Test
    public void testTTTGameEngineValidateMoveInValid(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //wrong turn
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player2, gameState);

        //move should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, engine.validateMove(context));
    }

    /**
     * Test that TicTacToe engine catches invalid bound
     */
    @Test
    public void testTTTGameEngineValidateMoveInvalidBound(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //out of bounds move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(5, 5, player1, gameState);

        //move should be invalid bound
        assertEquals(ValidationResult.INVALID_BOUND, engine.validateMove(context));
    }

    /**
     * Test that TicTacToe engine catches illegal occupied move
     */
    @Test
    public void testTTTGameEngineValidateMoveIllegalMove(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //occupy cell first
        gameState.setOccupancy(0, 0, player2);

        //build game
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //move should be illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, engine.validateMove(context));
    }

    /**
     * Test that ConnectFour engine validates bottom row move as VALID
     */
    @Test
    public void testC4GameEngineValidateMoveValid(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 0, player1, gameState);

        //move should be valid
        assertEquals(ValidationResult.VALID, engine.validateMove(context));
    }

    /**
     * Test that ConnectFour engine catches invalid turn
     */
    @Test
    public void testC4GameEngineValidatorMoveInvalid(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //wrong turn
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 0, player2, gameState);

        //move should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, engine.validateMove(context));
    }

    /**
     * Test that ConnectFour engine catches invalid bound
     */
    @Test
    public void testC4GameEngineValidateMoveInvalidBound(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //out of bounds move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 5, player1, gameState);

        //move should be invalid bound
        assertEquals(ValidationResult.INVALID_BOUND, engine.validateMove(context));
    }

    /**
     * Test that ConnectFour engine catches illegal floating move
     */
    @Test
    public void testC4GameEngineValidateMoveIllegalFloatingMove(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(3, 0, player1, gameState);

        //move should be invalid bound
        assertEquals(ValidationResult.ILLEGAL_MOVE, engine.validateMove(context));
    }

    /**
     * Test that engine validateMove throws exception for null context
     */
    @Test(expected = NullPointerException.class)
    public void testGameEngineValidateMoveNull(){
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        engine.validateMove(null);
    }

    /**
     * Test that TicTacToe applyMove places the player in the correct cell
     */
    @Test
    public void testTTTGameEngineApplyMove(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(1, 1, player1, gameState);

        //apply move
        engine.applyMove(context);

        //cell should now contain player1
        assertTrue(gameState.getOccupancy(1,1).isPresent());
        assertEquals(player1, gameState.getOccupancy(1, 1).get());
    }

    /**
     * Test that ConnectFour applyMove places the player in the correct cell
     */
    @Test
    public void testC4GameEngineApplyMove(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 3, player1, gameState);

        //apply move
        engine.applyMove(context);

        //cell should now contain player1
        assertTrue(gameState.getOccupancy(5,3).isPresent());
        assertEquals(player1, gameState.getOccupancy(5, 3).get());
    }

    /**
     * Test that applying move to another TTT cell updates that cell correctly
     */
    @Test
    public void testTTTGameEngineApplyMoveDifferentCell(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(2, 0, player1, gameState);

        //apply move
        engine.applyMove(context);

        //cell should now contain player1
        assertTrue(gameState.getOccupancy(2,0).isPresent());
        assertEquals(player1, gameState.getOccupancy(2, 0).get());
    }

    /**
     * Test that applying move to another C4 cell updates that cell correctly
     */
    @Test
    public void testC4GameEngineApplyMoveDifferentCell(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //build engine
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 6, player1, gameState);

        //apply move
        engine.applyMove(context);

        //cell should now contain player1
        assertTrue(gameState.getOccupancy(5,6).isPresent());
        assertEquals(player1, gameState.getOccupancy(5, 6).get());
    }

    /**
     * Test that applyMove throws exception for null context
     */
    @Test(expected = NullPointerException.class)
    public void testTTTGameEngineApplyMoveNull(){
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        engine.applyMove(null);
    }

    /**
     * Test that applyMove throws exception for null context
     */
    @Test(expected = NullPointerException.class)
    public void testC4GameEngineApplyMoveNull(){
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        engine.applyMove(null);
    }

    /**
     * Test that first TicTacToe move keeps the game ongoing
     */
    @Test
    public void testEvaluateGameOutcomeOngoingTTT(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //create engine and first move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //first move should not finish game
        assertEquals(GameOutcome.ONGOING, outcome);
    }

    /**
     * Test that TicTacToe winning move returns WIN
     */
    @Test
    public void testEvaluateGameOutcomeWinTTT(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //two marks on a row
        gameState.setOccupancy(0, 0, player1);
        gameState.setOccupancy(0, 1, player1);

        //prepare winning move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 2, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //should be a win
        assertEquals(GameOutcome.WIN, outcome);
    }

    /**
     * Test that TicTacToe vertical winning move returns WIN
     */
    @Test
    public void testEvaluateGameOutcomeVerticalWinTTT(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //prepare vertical win
        gameState.setOccupancy(0, 1, player1);
        gameState.setOccupancy(1, 1, player1);

        //winning move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(2, 1, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //should be a win
        assertEquals(GameOutcome.WIN, outcome);
    }

    /**
     * Test that TicTacToe Diagonal winning move returns WIN
     */
    @Test
    public void testEvaluateGameOutcomeDiagonalWinTTT(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //prepare diagonal move
        gameState.setOccupancy(0, 0, player1);
        gameState.setOccupancy(1, 1, player1);

        //winning move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(2, 2, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //should be a win
        assertEquals(GameOutcome.WIN, outcome);
    }

    /**
     * Test that TicTacToe final non-winning move returns DRAW
     */
    @Test
    public void testEvaluateGameOutcomeDrawTTT(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //set board one move away from win
        gameState.setOccupancy(0, 0, player1);
        gameState.setOccupancy(0, 1, player2);
        gameState.setOccupancy(0, 2, player1);
        gameState.setOccupancy(1, 0, player1);
        gameState.setOccupancy(1, 1, player2);
        gameState.setOccupancy(1, 2, player2);
        gameState.setOccupancy(2, 0, player2);
        gameState.setOccupancy(2, 1, player1);

        //last move fills the board without win
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(2, 2, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //should be a draw
        assertEquals(GameOutcome.DRAW, outcome);
    }

    /**
     * Test that first ConnectFour move keeps the game ONGOING
     */
    @Test
    public void testEvaluateGameOutComeOngoingC4(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //create engine and first move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 0, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //first move should not finish game
        assertEquals(GameOutcome.ONGOING, outcome);
    }

    /**
     * Test that horizontal ConnectFour winning move returns WIN
     */
    @Test
    public void testEvaluateGameOutcomeHorizontalWinC4(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //prepare three in a row
        gameState.setOccupancy(5, 0, player1);
        gameState.setOccupancy(5, 1, player1);
        gameState.setOccupancy(5, 2, player1);

        //winning move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 3, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //should be a win
        assertEquals(GameOutcome.WIN, outcome);
    }

    /**
     * Test that vertical ConnectFour winning move returns WIN
     */
    @Test
    public void testEvaluateGameOutcomeVerticalWinC4(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //prepare vertical stack
        gameState.setOccupancy(5, 0, player1);
        gameState.setOccupancy(4, 0, player1);
        gameState.setOccupancy(3, 0, player1);

        //winning move
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(2, 0, player1, gameState);
        engine.applyMove(context);

        //evaluate outcome
        GameOutcome outcome = engine.evaluateGameOutcome(context);

        //should be a win
        assertEquals(GameOutcome.WIN, outcome);
    }

    /**
     * Test that evaluateGameOutcome throws exception for null context
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateGameOutcomeNullTTT(){
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        engine.evaluateGameOutcome(null);
    }

    /**
     * Test that evaluateGameOutcome throws exception for null context
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateGameOutcomeNullC4(){
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        engine.evaluateGameOutcome(null);
    }

    /**
     * Test that WIN transition sets outcome, winner and loser
     */
    @Test
    public void testApplyGameOutcomeTransitionWin(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //apply win transition
        engine.applyGameOutcomeTransition(context, GameOutcome.WIN);

        //verify state changes
        assertEquals(GameOutcome.WIN, gameState.getGameOutcome());
        assertEquals(player1, gameState.getWinner());
        assertEquals(player2, gameState.getLoser());
    }

    /**
     * Test that DRAW transition sets draw outcome
     */
    @Test
    public void testApplyGameOutcomeTransitionDraw(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //apply draw transition
        engine.applyGameOutcomeTransition(context, GameOutcome.DRAW);

        //verify state changes
        assertEquals(GameOutcome.DRAW, gameState.getGameOutcome());
        assertNull(gameState.getWinner());
        assertNull(gameState.getLoser());
    }

    /**
     * Test that ONGOING transition sets ongoing outcome
     */
    @Test
    public void testApplyGameOutcomeTransitionOngoing(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //set state first to something else
        gameState.setGameOutcome(GameOutcome.WIN);
        gameState.setWinner(player1);
        gameState.setLoser(player2);

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //apply ongoing transition
        engine.applyGameOutcomeTransition(context, GameOutcome.ONGOING);

        //verify state changes
        assertEquals(GameOutcome.ONGOING, gameState.getGameOutcome());
        assertEquals(player1, gameState.getWinner());
        assertEquals(player2, gameState.getLoser());
    }

    /**
     * Test that ConnectFour WIN transition sets outcome, winner and loser
     */
    @Test
    public void testApplyGameOutcomeTransitionWinC4(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.CONNECT_FOUR);
        MoveContext context = makeContext(5, 0, player1, gameState);

        //apply win transition
        engine.applyGameOutcomeTransition(context, GameOutcome.WIN);

        //verify state changes
        assertEquals(GameOutcome.WIN, gameState.getGameOutcome());
        assertEquals(player1, gameState.getWinner());
        assertEquals(player2, gameState.getLoser());
    }

    /**
     * Test that player turn changes from player1 to player2 in TicTacToe
     */
    @Test
    public void testApplyPlayerTurnStateTransitionTTT(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //verify initial turn
        assertEquals(player1, gameState.getPlayerTurnState());

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //apply turn change
        engine.applyPlayerTurnStateTransition(context, player2);

        //verify new turn
        assertEquals(player2, gameState.getPlayerTurnState());
    }

    /**
     * Test that player turn changes from player1 to player2 in ConnectFour
     */
    @Test
    public void testApplyPlayerTurnStateTransitionC4(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //verify initial turn
        assertEquals(player1, gameState.getPlayerTurnState());

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(5, 0, player1, gameState);

        //apply turn change
        engine.applyPlayerTurnStateTransition(context, player2);

        //verify new turn
        assertEquals(player2, gameState.getPlayerTurnState());
    }

    /**
     * Test that player turn can change back from player2 to player1
     */
    @Test
    public void testApplyPlayerTurnStateTransitionBackToPlayer1(){
        //create players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //set turn to player2
        gameState.setPlayerTurnState(player2);

        //create engine and context
        GameEngine engine = gameEngineFactory.buildGameEngine(GameType.TIC_TAC_TOE);
        MoveContext context = makeContext(1, 1, player2, gameState);

        //apply turn change back to player1
        engine.applyPlayerTurnStateTransition(context, player1);

        //verify new turn
        assertEquals(player1, gameState.getPlayerTurnState());
    }
}
