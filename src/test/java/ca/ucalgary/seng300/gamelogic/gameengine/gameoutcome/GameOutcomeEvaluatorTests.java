package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameOutcomeEvaluatorTests {

    /**
     * This is Dummy evaluator for testing GameOutcomeEvaluator
     */
    private static class Dummy extends GameOutcomeEvaluator {
        private final GameOutcome resultReturn;

        /**
         *
         * This creates a dummy evaluator with a win length and fixed result
         *
         * @param i win length
         * @param outcome result
         */
        public Dummy(int i, GameOutcome outcome) {
            super(i);
            this.resultReturn = outcome;
        }

        /**
         * @return fixed game outcome
         */
        @Override
        protected GameOutcome evaluateGameOutcome(MoveContext context) {
            return resultReturn;
        }

        /**
         * This calls evaluate method for testing
         */
        public GameOutcome callEvaluate(MoveContext context) {
            return evaluateGameOutcome(context);
        }
    }

    /**
     * This Tests that constructor is storing correct win length
     */
    @Test
    public void constructorStoresWinLength() {
        Dummy evaluator = new Dummy(4, GameOutcome.ONGOING);
        assertEquals(4, evaluator.getWinLength());
    }

    /**
     * This Tests that win length is working correctly for value 1
     */
    @Test
    public void getWinLengthWorksForOne() {
        Dummy evaluator = new Dummy(1, GameOutcome.ONGOING);
        assertEquals(1, evaluator.getWinLength());
    }

    /**
     * This Tests for Win Length can be 0 (no validation present)
     */
    @Test
    public void getWinLengthWorksForZero() {
        Dummy evaluator = new Dummy(0, GameOutcome.ONGOING);
        assertEquals(0, evaluator.getWinLength());
    }

    /**
     *  This Tests for Win Length can be negative (no validation present)
     */
    @Test
    public void getWinLengthWorksForNegative() {
        Dummy dummy = new Dummy(-3, GameOutcome.ONGOING);
        assertEquals(-3, dummy.getWinLength());
    }

    /**
     * This Tests that evaluator can return WIN
     */
    @Test
    public void gameOutcomeCanReturnWin() {
        Dummy dummy = new Dummy(3, GameOutcome.WIN);
        assertEquals(GameOutcome.WIN, dummy.callEvaluate(null));
    }

    /**
     * This Tests that evaluator can return DRAW
     */
    @Test
    public void gameOutcomeCanReturnDraw() {
        Dummy dummy = new Dummy(3, GameOutcome.DRAW);
        assertEquals(GameOutcome.DRAW, dummy.callEvaluate(null));
    }

    /**
     * This Tests that evaluator can return LOSS
     */
    @Test
    public void gameOutcomeCanReturnLoss() {
        Dummy dummy = new Dummy(3, GameOutcome.LOSS);
        assertEquals(GameOutcome.LOSS, dummy.callEvaluate(null));
    }

    /**
     * This Tests that evaluator can return ONGOING
     */
    @Test
    public void gameOutcomeCanReturnOngoing() {
        Dummy dummy = new Dummy(3, GameOutcome.ONGOING);
        assertEquals(GameOutcome.ONGOING, dummy.callEvaluate(null));
    }
}