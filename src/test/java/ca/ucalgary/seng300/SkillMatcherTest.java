package ca.ucalgary.seng300;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * This test class verifies the functionality of the SkillMatcher component.
 * SkillMatcher is responsible for determining whether players can be matched based on their skill level, game type, and the queue status.
 */
public class SkillMatcherTest {

    /**
     * A helper method used to create user objects for testing as well as reduces duplication and ensures consistency across test cases
     * @param name the username to assign
     * @return a new user instance
     */
    private User createUser(String name) {
        return new User(name, name + "@email.com", "password");
    }

    /**
     * Tests that the constructor initializes correctly with valid parameters.
     */
    @Test
    public void testConstructorValid() {
        SkillMatcher matcher = new SkillMatcher(50, 1);
        assertNotNull(matcher);
    }

    /**
     * Tests that negative tolerance values are rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegValues() {
        new SkillMatcher(-1, 1);
    }

    /**
     * Tests compatibility when both players have identical conditions. Same game type and both are
     * in queue and are compatible.
     */
    @Test
    public void testIsCompatibleSameSkill() {
        SkillMatcher matcher = new SkillMatcher();

        User u1 = createUser("A");
        User u2 = createUser("B");

        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.TIC_TAC_TOE);

        assertTrue(matcher.isCompatible(t1, t2));
    }

    /**
     * Tests that players with different game types cannot be matched.
     */
    @Test
    public void testIsNotCompatibleDifferentGameType() {
        SkillMatcher matcher = new SkillMatcher();

        User u1 = createUser("A");
        User u2 = createUser("B");

        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.CONNECT_FOUR);

        assertFalse(matcher.isCompatible(t1, t2));
    }

    /**
     * Tests that players who are no longer in queue cannot be matched.
     */
    @Test
    public void testIsNotCompatibleIfNotQueued() {
        SkillMatcher matcher = new SkillMatcher();

        User u1 = createUser("A");
        User u2 = createUser("B");

        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.TIC_TAC_TOE);

        //stimulated player already matched
        t2.markMatched(); // not queued anymore

        assertFalse(matcher.isCompatible(t1, t2));
    }

    /**
     * Tests that matchmaking tolerance increases over time.
     * @throws InterruptedException
     */
    @Test
    public void testToleranceIncreasesOverTime() throws InterruptedException {

        SkillMatcher matcher = new SkillMatcher(0, 100);
        User u = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(u, GameType.TIC_TAC_TOE);

        int initialTolerance = matcher.currentTolerance(ticket);
        //simulate time passing in queue
        Thread.sleep(2000); //waiting 2 secs

        int laterTolerance = matcher.currentTolerance(ticket);
        //tolerance should increases over time
        assertTrue(laterTolerance > initialTolerance);
    }

    /**
     * Tests selecting a valid match group from a list of tickets.
     */
    @Test
    public void testSelectMatchGroupValidPair() {
        SkillMatcher matcher = new SkillMatcher();

        User u1 = createUser("A");
        User u2 = createUser("B");

        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.TIC_TAC_TOE);

        List<MatchmakingTicket> tickets = List.of(t1, t2);

        Optional<List<MatchmakingTicket>> result = matcher.selectMatchGroup(tickets, 2);

        //A valid group should be found
        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertTrue(result.get().contains(t1));
        assertTrue(result.get().contains(t2));
    }

    /**
     * Tests that no match is returned when players are incompatible.
     */
    @Test
    public void testSelectMatchGroupNoMatch() {
        SkillMatcher matcher = new SkillMatcher(0, 0); //no tolerance

        User u1 = createUser("A");
        User u2 = createUser("B");

        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.TIC_TAC_TOE);

        //make one user invalid for matching
        t2.markMatched();

        List<MatchmakingTicket> tickets = List.of(t1, t2);
        Optional<List<MatchmakingTicket>> result = matcher.selectMatchGroup(tickets, 2);

        //no valid match should be found
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that the matcher selects the best possible pair which would be the lowest skill difference.
     */
    @Test
    public void testSelectBestMatchLowestDifference() {
        SkillMatcher matcher = new SkillMatcher();

        User u1 = createUser("A");
        User u2 = createUser("B");
        User u3 = createUser("C");


        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.TIC_TAC_TOE);
        MatchmakingTicket t3 = new MatchmakingTicket(u3, GameType.TIC_TAC_TOE);

        List<MatchmakingTicket> tickets = List.of(t1, t2, t3);

        Optional<List<MatchmakingTicket>> result = matcher.selectMatchGroup(tickets, 2);

        //should still find a valid pair even with extra players
        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
    }

    /**
     * Tests that invalid player counts throw an exception.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testSelectMatchGroupInvalidPlayerCount() {
        SkillMatcher matcher = new SkillMatcher();

        List<MatchmakingTicket> tickets = new ArrayList<>();

        //invalid group size ( e.g 3 when only pairs allowed)
        matcher.selectMatchGroup(tickets,3);
    }

    /**
     * Tests selecting a match group directly from a matchmaking queue.
     */
    @Test
    public void testSelectMatchGroupFromQueue() {
        SkillMatcher matcher = new SkillMatcher();
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        User u1 = createUser("A");
        User u2 = createUser("B");

        MatchmakingTicket t1 = new MatchmakingTicket(u1, GameType.TIC_TAC_TOE);
        MatchmakingTicket t2 = new MatchmakingTicket(u2, GameType.TIC_TAC_TOE);
        //add tickets to queue
        queue.enqueue(t1);
        queue.enqueue(t2);

        Optional<List<MatchmakingTicket>> result = matcher.selectMatchGroup(queue, 2);

        //should successfully match players from queue
        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
    }
}