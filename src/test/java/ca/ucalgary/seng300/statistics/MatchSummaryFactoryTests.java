package ca.ucalgary.seng300.statistics;

import ca.ucalgary.seng300.*;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MatchSummaryFactoryTests {

    private Player makePlayer() {
        UUID uuid = UUID.randomUUID();
        return new Player() {
            public UUID getUUID() {
                return uuid;
            }

            public String getUsername() {
                return "x";
            }
        };
    }

    /**
     * Win summary works.
     */
    @Test
    public void buildWinMatchSummaryWorks() {
        MatchSummaryFactory factory = new MatchSummaryFactory();

        MatchSummary summary = factory.buildWinMatchSummary(makePlayer(), makePlayer(), GameType.TIC_TAC_TOE);

        assertEquals(GameOutcome.WIN, summary.getResult());
    }


    /**
     * Draw summary works.
     */
    @Test
    public void buildDrawMatchSummaryWorks() {
        MatchSummaryFactory factory = new MatchSummaryFactory();

        MatchSummary summary = factory.buildDrawMatchSummary(
                Arrays.asList(makePlayer(), makePlayer()),
                GameType.TIC_TAC_TOE
        );

        assertEquals(GameOutcome.DRAW, summary.getResult());
    }
}
