package ca.ucalgary.seng300.gamelogic.gameengine.validator;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.ValidationResult;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.context.MoveContextFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ValidationSystemTest {

    private GameStateFactory gameStateFactory;

    /**
     * Set up objects before each test
     */
    @Before
    public void before(){
        //create new factory for each test
        gameStateFactory = new GameStateFactory();
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
     * Helper method to create TicTacToe game state
     */
    private GameState TTTState(Player p1, Player p2){
        //create 3x3 TicTacToe state
        return gameStateFactory.buildGameState(List.of(p1, p2), GameType.TIC_TAC_TOE);
    }

    /**
     * Helper method to create ConnectFour game state
     */
    private GameState C4State(Player p1, Player p2){
        //create 6x7 ConnectFour state
        return gameStateFactory.buildGameState(List.of(p1, p2), GameType.CONNECT_FOUR);
    }

    /**
     * Helper method to create move context
     */
    private MoveContext makeContext(int row, int col, Player player, GameState gameState){
        //create move context
        MoveContextFactory factory = new MoveContextFactory();
        return factory.buildCTX(row, col, player, gameState);
    }

    //ValidationResult Tests

    /**
     * Test that ValidationResult has four values
     */
    @Test
    public void testValidationResultCount(){
        //get all enum values
        ValidationResult[] results = ValidationResult.values();

        //enum should have 4 values
        assertEquals(4, results.length);
    }

    /**
     * Test that INVALID_BOUND exists
     */
    @Test
    public void testInvalidBoundExists(){
        assertEquals(ValidationResult.INVALID_BOUND, ValidationResult.valueOf("INVALID_BOUND"));
    }

    /**
     * Test that INVALID_TURN exists
     */
    @Test
    public void testInvalidTurnExists(){
        assertEquals(ValidationResult.INVALID_TURN, ValidationResult.valueOf("INVALID_TURN"));
    }

    /**
     * Test that ILLEGAL_MOVE exists
     */
    @Test
    public void testIllegalMoveExists(){
        assertEquals(ValidationResult.ILLEGAL_MOVE, ValidationResult.valueOf("ILLEGAL_MOVE"));
    }

    /**
     * Test that VALID exists
     */
    @Test
    public void testValidExists(){
        assertEquals(ValidationResult.VALID, ValidationResult.valueOf("VALID"));
    }

    //TurnValidator Tests

    /**
     * Test that correct player turn returns VALID for TicTacToe
     */
    @Test
    public void testTurnValidatorValidTurnTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state with player1 turn
        GameState gameState = TTTState(player1, player2);

        //create move context for correct player
        MoveContext context = makeContext(0, 0, player1, gameState);

        //validate move
        TurnValidator validator = new TurnValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that wrong player turn returns INVALID_TURN for TicTacToe
     */
    @Test
    public void testTurnValidatorInvalidTurnTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state with player1 turn
        GameState gameState = TTTState(player1, player2);

        //create move context fot the wrong player
        MoveContext context = makeContext(0, 0, player2, gameState);

        //validate move
        TurnValidator validator = new TurnValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, result);
    }

    /**
     * Test that null context throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testTurnValidatorNull(){
        TurnValidator validator = new TurnValidator();
        validator.validateMove(null);
    }

    /**
     * Test that different players with the same text data still fails if the UUID is different
     */
    @Test
    public void testTVDifferentPlayerSameData(){
        //create two players with the same data
        Player player1 = new User("same", "same@test.com", "pass");
        Player player1Copy = new User("same", "same@test.com", "pass");

        //create game state with player1's turn
        GameState gameState = TTTState(player1, player1Copy);

        //wrong UUID player tries to move
        MoveContext context = makeContext(0, 0, player1Copy, gameState);

        //validate move
        TurnValidator validator = new TurnValidator();
        ValidationResult result = validator.validateMove(context);

        //should still be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, result);
    }

    /**
     * Test that correct player turn returns VALID for ConnectFour
     */
    @Test
    public void testTurnValidatorValidTurnC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state with player1 turn
        GameState gameState = C4State(player1, player2);

        //create move context for correct player
        MoveContext context = makeContext(0, 0, player1, gameState);

        //validate move
        TurnValidator validator = new TurnValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that wrong player turn returns INVALID_TURN for ConnectFour
     */
    @Test
    public void testTurnValidatorInvalidTurnC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state with player1 turn
        GameState gameState = C4State(player1, player2);

        //create move context fot the wrong player
        MoveContext context = makeContext(0, 0, player2, gameState);

        //validate move
        TurnValidator validator = new TurnValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, result);
    }

    //BoundsValidator Tests

    /**
     * Test that valid TicTacToe position returns VALID
     */
    @Test
    public void testBoundsValidatorTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(1, 1, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //position should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that negative row returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorNegativeRowTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(-1, 0, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that negative column returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorNegativeColumnTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(0, -1, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that row too large returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorRowTooLargeTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(3, 1, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that column too large returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorColumnTooLargeTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(1, 3, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that top-left corner is VALID
     */
    @Test
    public void testBoundsValidatorTopLeftValidTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(0, 0, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //position should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that bottom right corner is valid
     */
    @Test
    public void testBoundsValidatorBottomRightValidTTT(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = TTTState(player1, player2);

        //create valid move
        MoveContext context = makeContext(2, 2, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //position should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that valid Connect Four bottom-right position returns VALID
     */
    @Test
    public void testBoundsValidatorValidC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = C4State(player1, player2);

        //create valid move
        MoveContext context = makeContext(5, 6, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //position should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that Connect Four row too large returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorRowTooLargeC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = C4State(player1, player2);

        //create valid move
        MoveContext context = makeContext(6, 0, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that Connect Four column too large returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorColumnTooLargeC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = C4State(player1, player2);

        //create valid move
        MoveContext context = makeContext(0, 7, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that Connect Four negative row returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorNegativeRowC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = C4State(player1, player2);

        //create valid move
        MoveContext context = makeContext(-1, 4, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that Connect Four negative column returns INVALID_BOUND
     */
    @Test
    public void testBoundsValidatorNegativeColumnC4(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create game state
        GameState gameState = C4State(player1, player2);

        //create valid move
        MoveContext context = makeContext(4, -1, player1, gameState);

        //validate move
        BoundsValidator validator = new BoundsValidator();
        ValidationResult result = validator.validateMove(context);

        //move should be out of bounds
        assertEquals(ValidationResult.INVALID_BOUND, result);
    }

    /**
     * Test that null context throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testBoundsValidatorNullContext(){
        //create validator
        BoundsValidator validator = new BoundsValidator();
        validator.validateMove(null);
    }

    // TTTRuleValidator Tests

    /**
     * Test that placing in an empty TicTacToe cell returns VALID
     */
    @Test
    public void testTTTRuleValidatorValidMove(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty TicTacToe state
        GameState gameState = TTTState(player1, player2);

        //create move in empty cell
        MoveContext context = makeContext(0, 0, player1, gameState);

        //validate move
        TTTRuleValidator validator = new TTTRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //empty cell should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that placing in an occupied TicTacToe cell returns ILLEGAL_MOVE
     */
    @Test
    public void testTTTRulesValidatorOccupiedCell(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty TicTacToe state
        GameState gameState = TTTState(player1, player2);

        //occupy the cell first
        gameState.setOccupancy(0, 0, player1);

        //try placing again in the same cell
        MoveContext context = makeContext(0, 0, player1, gameState);

        //validate move
        TTTRuleValidator validator = new TTTRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //occupied cell should be illegal
        assertEquals(ValidationResult.ILLEGAL_MOVE, result);
    }

    /**
     * Test that a different occupied TicTacToe cell also returns ILLEGAL_MOVE
     */
    @Test
    public void testTTTRulesValidatorDifferentOccupiedCell(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty TicTacToe state
        GameState gameState = TTTState(player1, player2);

        //occupy another cell
        gameState.setOccupancy(2, 2, player1);

        //try placing again in the same occupied cell
        MoveContext context = makeContext(2, 2, player1, gameState);

        //validate move
        TTTRuleValidator validator = new TTTRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //occupied cell should be illegal
        assertEquals(ValidationResult.ILLEGAL_MOVE, result);
    }

    /**
     * Test that another empty TicTacToe returns VALID
     */
    @Test
    public void testTTTRuleValidatorAnotherEmptyCell(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty TicTacToe state
        GameState gameState = TTTState(player1, player2);

        //create move i another empty cell
        MoveContext context = makeContext(1, 2, player1, gameState);

        //validate move
        TTTRuleValidator validator = new TTTRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //empty cell should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that null context throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testTTTRuleValidatorNull(){
        TTTRuleValidator validator = new TTTRuleValidator();
        validator.validateMove(null);
    }

    //FourCRuleValidator Tests

    /**
     * Test that placing in the bottom row returns VALID
     */
    @Test
    public void testFourCValidatorBottomRowValid(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //bottom row move
        MoveContext context = makeContext(5, 0, player1, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //bottom row should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that placing in the another bottom row also returns VALID
     */
    @Test
    public void testFourCValidatorBottomRowValid1(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //bottom row in another column
        MoveContext context = makeContext(5, 6, player1, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //bottom row should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that placing in an occupied ConnectFour cell returns ILLEGAL_MOVE
     */
    @Test
    public void testFourCValidatorOccupiedCell(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //occupy cell first
        gameState.setOccupancy(5, 0, player1);

        //try placing again in the same cell
        MoveContext context = makeContext(5, 0, player2, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //occupied cell should be illegal
        assertEquals(ValidationResult.ILLEGAL_MOVE, result);
    }

    /**
     * Test that placing a floating move returns ILLEGAL_MOVE
     */
    @Test
    public void testFourCRuleValidatorFloatingPiece(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //row 3 has empty cell below so this is floating
        MoveContext context = makeContext(3, 0, player1, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //floating move should be illegal
        assertEquals(ValidationResult.ILLEGAL_MOVE, result);
    }

    /**
     * Test that placing two rows above with only one support below is still an illegal move
     */
    @Test
    public void testFourCRuleValidatorFloatingPiece1(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //place support piece below
        gameState.setOccupancy(5, 0, player1);

        //place above the supported cell
        MoveContext context = makeContext(3, 0, player2, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //still floating, so illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, result);
    }

    /**
     * Test that placing above a supported piece returns VALID
     */
    @Test
    public void testFourCRuleValidatorSupportedMove(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //create empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //place support piece below
        gameState.setOccupancy(5, 0, player1);

        //place above the supported cell
        MoveContext context = makeContext(4, 0, player2, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //supported move should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that placing above two stacked pieces returns VALID
     */
    @Test
    public void testFourCRuleValidatorSupportedMove1(){
        //create players
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();

        //empty ConnectFour state
        GameState gameState = C4State(player1, player2);

        //stack two pieces
        gameState.setOccupancy(5, 0, player1);
        gameState.setOccupancy(4, 0, player2);

        //place above the supported cell
        MoveContext context = makeContext(3, 0, player2, gameState);

        //validate move
        FourCRuleValidator validator = new FourCRuleValidator();
        ValidationResult result = validator.validateMove(context);

        //supported move should be valid
        assertEquals(ValidationResult.VALID, result);
    }

    /**
     * Test that null context throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testFourCRuleValidatorNull(){
        FourCRuleValidator validator = new FourCRuleValidator();
        validator.validateMove(null);
    }

    //MoveValidationManager Tests

    /**
     * Test that null pipeline throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testMoveValidationManagerNUllPipeline(){
        new MoveValidationManager(null);
    }

    /**
     * Test that empty pipeline returns VALID
     */
    @Test
    public void testMoveValidationManagerEmptyPipeline(){
        //manager with empty pipeline
        MoveValidationManager manager = new MoveValidationManager(new java.util.ArrayList<>());

        //normal move context
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //no validators mean valid
        assertEquals(ValidationResult.VALID, manager.validateMove(context));
    }

    /**
     * Test that null context throws exception
     */
    @Test(expected = NullPointerException.class)
    public void testMoveValidationManagerNullContext(){
        MoveValidationManager manager = new MoveValidationManager(new java.util.ArrayList<>());
        manager.validateMove(null);
    }

    /**
     * Test that manager returns VALID when all validators pass for TicTacToe
     */
    @Test
    public void testMoveValidationManagerAllValidTTT(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new TTTRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //valid move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //all validators should pass
        assertEquals(ValidationResult.VALID, manager.validateMove(context));
    }

    /**
     * Test that first invalid result is returned
     */
    @Test
    public void testFirstInvalidResultTTT(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new TTTRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //wrong player turn should fail first
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(0, 0, player2, gameState);

        //first invalid result should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that bounds failure is returned before the rule validation
     */
    @Test
    public void testBoundsFailBeforeRuleTTT(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new TTTRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //out of bounds move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(5, 5, player1, gameState);

        //bounds should fail before the rule check
        assertEquals(ValidationResult.INVALID_BOUND, manager.validateMove(context));
    }

    /**
     * Test that illegal move is returned after earlier validators pass
     */
    @Test
    public void testIllegalMoveAfterEarlierPassesTTT(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new TTTRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //occupy cell first
        gameState.setOccupancy(0, 0, player2);

        //valid turn and valid bounds, but illegal occupied move
        MoveContext context = makeContext(0, 0, player1, gameState);

        //result should be illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, manager.validateMove(context));
    }

    /**
     * Test that manager with one validator returns that validator result
     */
    @Test
    public void testSingleValidatorTTT(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //wrong turn move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(0, 0, player2, gameState);

        //should return invalid turn
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that manager preserves validator order
     */
    @Test
    public void testOrderMattersTTT(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //this is both wrong turn and out of bounds
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(10, 10, player2, gameState);

        //first validator should decide result
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that manager returns VALID when all validators pass for ConnectFour
     */
    @Test
    public void testMoveValidationManagerAllValidMoveC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new FourCRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //valid bottom row move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(5, 0, player1, gameState);

        //all validators should pass
        assertEquals(ValidationResult.VALID, manager.validateMove(context));
    }

    /**
     * Test that manager returns illegal move for floating ConnectFour move
     */
    @Test
    public void testIllegalMoveC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new FourCRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //floating move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(3, 0, player1, gameState);

        //should be illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, manager.validateMove(context));
    }
    /**
     * Test that first invalid result is returned
     */
    @Test
    public void testFirstInvalidResultC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new FourCRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //wrong player turn should fail first
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(0, 0, player2, gameState);

        //first invalid result should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that bounds failure is returned before the rule validation
     */
    @Test
    public void testBoundsFailBeforeRuleC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new FourCRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //out of bounds move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(8, 7, player1, gameState);

        //bounds should fail before the rule check
        assertEquals(ValidationResult.INVALID_BOUND, manager.validateMove(context));
    }

    /**
     * Test that illegal move is returned after earlier validators pass
     */
    @Test
    public void testIllegalMoveAfterEarlierPassesC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());
        validators.add(new FourCRuleValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //players and state
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //occupy cell first
        gameState.setOccupancy(0, 0, player2);

        //valid turn and valid bounds, but illegal occupied move
        MoveContext context = makeContext(0, 0, player1, gameState);

        //result should be illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, manager.validateMove(context));
    }

    /**
     * Test that manager with one validator returns that validator result
     */
    @Test
    public void testSingleValidatorC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //wrong turn move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(3, 4, player2, gameState);

        //should return invalid turn
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that manager preserves validator order
     */
    @Test
    public void testOrderMattersC4(){
        //create validator pipeline
        java.util.List<MoveValidator> validators = new java.util.ArrayList<>();
        validators.add(new TurnValidator());
        validators.add(new BoundsValidator());

        MoveValidationManager manager = new MoveValidationManager(validators);

        //this is both wrong turn and out of bounds
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(8, 8, player2, gameState);

        //first validator should decide result
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    //MVMFactory Tests

    /**
     * Test that TicTacToe validation manager is created
     */
    @Test
    public void testMVMFactoryBuildTTTManager(){
        //create factory
        MVMFactory factory = new MVMFactory();

        //build manager
        MoveValidationManager manager = factory.buildMVM(GameType.TIC_TAC_TOE);

        //manager should exist
        assertNotNull(manager);
    }

    /**
     * Test that ConnectFour validation manager is created
     */
    @Test
    public void testMVMFactoryBuildC4Manager(){
        //create factory
        MVMFactory factory = new MVMFactory();


        //build manager
        MoveValidationManager manager = factory.buildMVM(GameType.CONNECT_FOUR);

        //manager should exist
        assertNotNull(manager);
    }

    /**
     * Test that TicTacToe manager validates a correct move
     */
    @Test
    public void testTTTManagerValidMove(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.TIC_TAC_TOE);

        //valid move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(0, 0, player1, gameState);

        //should be valid
        assertEquals(ValidationResult.VALID, manager.validateMove(context));
    }

    /**
     * Test that TicTacToe manager catches the invalid turn
     */
    @Test
    public void testTTTManagerInvalidTurn(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.TIC_TAC_TOE);

        //wrong turn move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(0, 0, player2, gameState);

        //should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that TicTacToe manager catches invalid bounds
     */
    @Test
    public void testTTTManagerInvalidBound(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.TIC_TAC_TOE);

        //out of bounds move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);
        MoveContext context = makeContext(10, 10, player1, gameState);

        //should be invalid bound
        assertEquals(ValidationResult.INVALID_BOUND, manager.validateMove(context));
    }

    /**
     * Test that TicTacToe manager catches illegal occupied move
     */
    @Test
    public void testTTTManagerIllegalMove(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.TIC_TAC_TOE);

        //state with occupied cell
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = TTTState(player1, player2);

        //occupy cell
        gameState.setOccupancy(0, 0, player2);

        //try occupied cell
        MoveContext context = makeContext(0, 0, player1, gameState);

        //should be illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, manager.validateMove(context));
    }

    /**
     * Test that ConnectFour manager validates bottom row move
     */
    @Test
    public void testC4ManagerValidMove(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.CONNECT_FOUR);

        //valid bottom row move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(5, 0, player1, gameState);

        //should be valid
        assertEquals(ValidationResult.VALID, manager.validateMove(context));
    }

    /**
     * Test that ConnectFour manager catches invalid turn
     */
    @Test
    public void testC4ManagerInvalidTurn(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.CONNECT_FOUR);

        //wrong turn move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(5, 0, player2, gameState);

        //should be invalid turn
        assertEquals(ValidationResult.INVALID_TURN, manager.validateMove(context));
    }

    /**
     * Test that ConnectFour manager catches invalid bounds
     */
    @Test
    public void testC4ManagerInvalidBound(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.CONNECT_FOUR);

        //out of bounds move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(10, 10, player1, gameState);

        //should be invalid bound
        assertEquals(ValidationResult.INVALID_BOUND, manager.validateMove(context));
    }

    /**
     * Test that ConnectFour manager catches illegal floating move
     */
    @Test
    public void testC4ManagerIllegalMove(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.CONNECT_FOUR);

        //floating move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);
        MoveContext context = makeContext(3, 0, player1, gameState);

        //should be illegal move
        assertEquals(ValidationResult.ILLEGAL_MOVE, manager.validateMove(context));
    }

    /**
     * Test that ConnectFour manager allows supported move
     */
    @Test
    public void testC4ManagerSupportedMove(){
        //create factory and manager
        MVMFactory factory = new MVMFactory();
        MoveValidationManager manager = factory.buildMVM(GameType.CONNECT_FOUR);

        //supported move
        Player player1 = makePlayer1();
        Player player2 = makePlayer2();
        GameState gameState = C4State(player1, player2);

        //occupy cell
        gameState.setOccupancy(5, 0, player1);

        //place above
        MoveContext context = makeContext(4, 0, player1, gameState);

        //should be valid
        assertEquals(ValidationResult.VALID, manager.validateMove(context));
    }
}