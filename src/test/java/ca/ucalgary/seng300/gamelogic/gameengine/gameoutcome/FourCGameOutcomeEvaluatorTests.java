package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FourCGameOutcomeEvaluatorTests {

    /**
     * This Tests that constructor is working successfully
     */
    @Test
    public void constructorWorking() {
        FourCGameOutcomeEvaluator evaluator = new FourCGameOutcomeEvaluator();
        assertNotNull(evaluator);
    }

    /**
     * This Tests win length is 4
     */
    @Test
    public void winIsFour() {
        FourCGameOutcomeEvaluator evaluator = new FourCGameOutcomeEvaluator();
        assertEquals(4, evaluator.getWinLength());
    }
}