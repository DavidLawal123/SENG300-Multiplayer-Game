package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GOEMTests {

    /**
     * The Fake evaluator use for testing
     */
    private static class fakeEvaluator extends GameOutcomeEvaluator {
        private final GameOutcome result;
        private int calls;

        public fakeEvaluator(GameOutcome outcome) {
            super(1);
            this.result = outcome;
            this.calls = 0;
        }

        /**
         *
         * It returns the fixed game outcome and counts how many times was called
         * @param ctx The move context
         * @return game outcome
         */
        @Override
        protected GameOutcome evaluateGameOutcome(MoveContext ctx) {
            calls++;
            return result;
        }

        /**
         * This checks how many times this evaluator was called
         *
         * @return number of calls
         */
        public int getCalls() {
            return calls;
        }
    }

    /**
     * @return dummy MoveContext object
     * @throws Exception if object can not be created
     */
    private MoveContext makeDummyContext() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        return (MoveContext) unsafe.allocateInstance(MoveContext.class);
    }

    /**
     * This Tests if null pipeline throws exception
     */
    @Test(expected = NullPointerException.class)
    public void constructorNullPipelineThrowsException() {
        new GameOutcomeEvaluatorManager(null);
    }

    /**
     * This Tests that if context is null than evaluateOutcome throws exception
     */
    @Test(expected = NullPointerException.class)
    public void evaluateOutcomeNullContextThrowsException() {
        GameOutcomeEvaluatorManager manager =
                new GameOutcomeEvaluatorManager(new ArrayList<GameOutcomeEvaluator>());
        manager.evaluateOutcome(null);
    }

    /**
     * This Tests that empty pipeline gives ONGOING
     */
    @Test
    public void emptyPipelineReturnsOngoing() throws Exception {
        GameOutcomeEvaluatorManager manager =
                new GameOutcomeEvaluatorManager(new ArrayList<GameOutcomeEvaluator>());

        MoveContext dummyContext = makeDummyContext();

        assertEquals(GameOutcome.ONGOING, manager.evaluateOutcome(dummyContext));
    }

    /**
     * This Tests that all evaluators returns ONGOING gives ONGOING result
     */
    @Test
    public void allOngoingReturnsOngoing() throws Exception {
        fakeEvaluator e1 = new fakeEvaluator(GameOutcome.ONGOING);
        fakeEvaluator e2 = new fakeEvaluator(GameOutcome.ONGOING);

        GameOutcomeEvaluatorManager manager =
                new GameOutcomeEvaluatorManager(Arrays.<GameOutcomeEvaluator>asList(e1, e2));

        MoveContext dummyContext = makeDummyContext();

        assertEquals(GameOutcome.ONGOING, manager.evaluateOutcome(dummyContext));
        assertEquals(1, e1.getCalls());
        assertEquals(1, e2.getCalls());
    }

    /**
     * This Tests that pipeline stops when first returns WIN
     */
    @Test
    public void firstWinStopsPipeline() throws Exception {
        fakeEvaluator e1 = new fakeEvaluator(GameOutcome.WIN);
        fakeEvaluator e2 = new fakeEvaluator(GameOutcome.DRAW);

        GameOutcomeEvaluatorManager manager =
                new GameOutcomeEvaluatorManager(Arrays.<GameOutcomeEvaluator>asList(e1, e2));

        MoveContext dummyContext = makeDummyContext();

        assertEquals(GameOutcome.WIN, manager.evaluateOutcome(dummyContext));
        assertEquals(1, e1.getCalls());
        assertEquals(0, e2.getCalls());
    }

    /**
     * This Tests that second evaluator is checked if first gives ONGOING
     */
    @Test
    public void secondEvaluatorUsedIfFirstOngoing() throws Exception {
        fakeEvaluator e1 = new fakeEvaluator(GameOutcome.ONGOING);
        fakeEvaluator e2 = new fakeEvaluator(GameOutcome.DRAW);

        GameOutcomeEvaluatorManager manager =
                new GameOutcomeEvaluatorManager(Arrays.<GameOutcomeEvaluator>asList(e1, e2));

        MoveContext dummyContext = makeDummyContext();

        assertEquals(GameOutcome.DRAW, manager.evaluateOutcome(dummyContext));
        assertEquals(1, e1.getCalls());
        assertEquals(1, e2.getCalls());
    }

    /**
     * This Tests that third evaluator is not called after WIN is found
     */
    @Test
    public void thirdEvaluatorNotCalledAfterWinFound() throws Exception {
        fakeEvaluator e1 = new fakeEvaluator(GameOutcome.ONGOING);
        fakeEvaluator e2 = new fakeEvaluator(GameOutcome.WIN);
        fakeEvaluator e3 = new fakeEvaluator(GameOutcome.DRAW);

        GameOutcomeEvaluatorManager manager =
                new GameOutcomeEvaluatorManager(Arrays.<GameOutcomeEvaluator>asList(e1, e2, e3));

        MoveContext dummyContext = makeDummyContext();

        assertEquals(GameOutcome.WIN, manager.evaluateOutcome(dummyContext));
        assertEquals(1, e1.getCalls());
        assertEquals(1, e2.getCalls());
        assertEquals(0, e3.getCalls());
    }
}