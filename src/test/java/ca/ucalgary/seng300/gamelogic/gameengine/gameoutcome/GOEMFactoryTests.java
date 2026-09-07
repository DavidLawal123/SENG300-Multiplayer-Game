package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.GameType;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class GOEMFactoryTests {

    /**
     * The Test that factory object is successfully created
     */
    @Test
    public void constructorWorking() {
        GOEMFactory goemFactory = new GOEMFactory();
        assertNotNull(goemFactory);
    }

    /**
     * This tests that buildGOEM throws exception when gametype is null
     */
    @Test(expected = NullPointerException.class)
    public void buildGOEMThrowsExceptionForNull() {
        GOEMFactory goemFactory = new GOEMFactory();
        goemFactory.buildGOEM(null);
    }

    /**
     * This Tests that TTT creates the correct evaluator manager
     */
    @Test
    public void buildGOEMTTTCreatesCorrectEvaluator() throws Exception {
        GOEMFactory goemFactory = new GOEMFactory();
        GameOutcomeEvaluatorManager evaluatorManager = goemFactory.buildGOEM(GameType.TIC_TAC_TOE);

        assertNotNull(evaluatorManager);

        List<?> list = getPipeline(evaluatorManager);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof TTTGameOutcomeEvaluator);
    }

    /**
     * This Tests that C4 creates the correct evaluator manager
     */
    @Test
    public void buildGOEMC4CreatesCorrectEvaluator() throws Exception {
        GOEMFactory goemFactory = new GOEMFactory();
        GameOutcomeEvaluatorManager evaluatorManager = goemFactory.buildGOEM(GameType.CONNECT_FOUR);

        assertNotNull(evaluatorManager);

        List<?> list = getPipeline(evaluatorManager);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof FourCGameOutcomeEvaluator);
    }

    /**
     * This Tests that unsupported game type throws exception
     */
    @Test
    public void unsupportedGameTypesException() {
        GOEMFactory goemFactory = new GOEMFactory();

        for (GameType gameType : GameType.values()) {
            if (gameType != GameType.TIC_TAC_TOE && gameType != GameType.CONNECT_FOUR) {
                assertThrows(IllegalArgumentException.class, () -> goemFactory.buildGOEM(gameType));
            }
        }
    }

    /**
     *
     * This Test get the internal pipeline list from the evaluator manager using reflection
     *
     * @param manager GameOutcomeEvaluatorManager
     * @return list of evaluator in manager
     * @throws Exception if field cannot be accessed
     */
    private List<?> getPipeline(GameOutcomeEvaluatorManager manager) throws Exception {
        Field pipeline = GameOutcomeEvaluatorManager.class.getDeclaredField("pipeline");
        pipeline.setAccessible(true);
        return (List<?>) pipeline.get(manager);
    }
}