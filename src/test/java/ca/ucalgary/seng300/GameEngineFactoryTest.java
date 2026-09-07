package ca.ucalgary.seng300;
import ca.ucalgary.seng300.gamelogic.gameengine.FourCGameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.TTTGameEngine;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngineFactory;
/**
 * This test class verifies that the Game engine factory correctly create game specific engine instances based on the provided GameType.
 */
public class GameEngineFactoryTest {
    /**
     * Tests that a TicTacToe engine is created.
     */
    @Test
    public void testBuildTTTEngine(){
        GameEngineFactory factory = new GameEngineFactory();
        GameEngine engine = factory.buildGameEngine(GameType.TIC_TAC_TOE);

        assertTrue(engine instanceof TTTGameEngine);
    }

    /**
     * Tests that a Connect Four engine is created.
     */
    @Test
    public void testBuildFourCEngine(){
        GameEngineFactory factory = new GameEngineFactory();
        GameEngine engine = factory.buildGameEngine(GameType.CONNECT_FOUR);

        assertTrue(engine instanceof FourCGameEngine);
    }

    /**
     * Tests that a null input is not allowed.
     */
    @Test(expected = NullPointerException.class)
    public void testBuildNullGameType(){
        GameEngineFactory factory = new GameEngineFactory();
        factory.buildGameEngine(null);
    }

}