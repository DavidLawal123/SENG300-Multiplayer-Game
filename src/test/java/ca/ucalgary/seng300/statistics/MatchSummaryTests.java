package ca.ucalgary.seng300.statistics;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class MatchSummaryTests {

    /**
     * Constructor stores values.
     */
    @Test
    public void constructorWorks() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        MatchSummary m = new MatchSummary(GameType.TIC_TAC_TOE, GameOutcome.WIN, p1, p2);

        assertEquals(GameOutcome.WIN, m.getResult());
        assertEquals(p1, m.getPlayerID());
        assertEquals(p2, m.getOpponentID());
    }

    /**
     * Reverse constructor flips result.
     */
    @Test
    public void reverseConstructorFlipsResult() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        MatchSummary summary = new MatchSummary(GameType.TIC_TAC_TOE, GameOutcome.WIN, p1, p2);
        MatchSummary reversed = new MatchSummary(summary);

        assertEquals(GameOutcome.LOSS, reversed.getResult());
    }

    /**
     Draw stays draw.
     */
    @Test
    public void reverseConstructorDraw() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        MatchSummary summary = new MatchSummary(GameType.TIC_TAC_TOE, GameOutcome.DRAW, p1, p2);
        MatchSummary reversed = new MatchSummary(summary);

        assertEquals(GameOutcome.DRAW, reversed.getResult());
    }
}
