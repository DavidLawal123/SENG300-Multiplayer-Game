package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TTTGameOutcomeEvaluatorTests {

    /**
     * This Tests if the constructor is working successfully
     */
    @Test
    public void constructorWorking() {
        TTTGameOutcomeEvaluator evaluator = new TTTGameOutcomeEvaluator();
        assertNotNull(evaluator);
    }

    /**
     * This Tests that win length is 3
     */
    @Test
    public void winIsThree() {
        TTTGameOutcomeEvaluator evaluator = new TTTGameOutcomeEvaluator();
        assertEquals(3, evaluator.getWinLength());
    }
}