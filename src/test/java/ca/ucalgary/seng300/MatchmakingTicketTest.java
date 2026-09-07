package ca.ucalgary.seng300;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test class verifies the behavior of MatchmakingTicket.
 * A ticket represents a players request to be matched for a game, and tracks their status, game type, and anything queue related.
 */
public class MatchmakingTicketTest {

    /**
     * Helper method to create a user object for testing.
     * @param name the base identifier for the user
     * @return a new user instance
     */
    private User createUser(String name) {
        return new User(name, name + "@email.com", "password");

    }

    /**
     * Tests that the constructor initializes all fields correctly.
     */
    @Test
    public void testConstructorValid() {
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        assertNotNull(ticket.getUUID());
        assertNotNull(ticket.getQueuedAt());
        assertEquals(user, ticket.getPlayer());
        assertEquals(GameType.TIC_TAC_TOE, ticket.getGameType());
        assertEquals(MatchmakingTicket.QueueStatus.QUEUED, ticket.getStatus());

    }

    /**
     * Tests that a null user is not allowed.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructorNullUser() {
        new MatchmakingTicket(null, GameType.TIC_TAC_TOE);
    }

    /**
     * Tests that a null game type is not allowed.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructorNullGameType() {
        User user = createUser("A");
        new MatchmakingTicket(user, null);
    }

    /**
     * Tests transitioning a ticket to matched state.
     * This is what simulates a successful match.
     */
    @Test
    public void testMarkMatched() {
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        ticket.markMatched();
        assertEquals(MatchmakingTicket.QueueStatus.MATCHED, ticket.getStatus());
    }

    /**
     * Tests cancelling a ticket.
     * The Ticket should transition to cancelled state.
     */
    @Test
    public void testCancel() {
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        ticket.cancel();
        assertEquals(MatchmakingTicket.QueueStatus.CANCELLED, ticket.getStatus());
    }

    /**
     * Tests that creating a ticket automatically places the user in queue.
     * Ensures user state is updated consistently with matchmaking.
     */
    @Test
    public void testConstructorSetsUserToInQueue() {
        User user = createUser("A");
        assertEquals(UserStatus.ONLINE, user.getStatus());
        new MatchmakingTicket(user, GameType.TIC_TAC_TOE);
        assertEquals(UserStatus.IN_QUEUE, user.getStatus());
    }

    /**
     * Tests that cancelling a ticket returns the user to online status.
     * This reflects that they are no longer waiting for a match.
     */
    @Test
    public void testCancelResetsUserToOnline() {
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        assertEquals(UserStatus.IN_QUEUE, user.getStatus());
        ticket.cancel();

        assertEquals(UserStatus.ONLINE, user.getStatus());
    }

    /**
     * Tests that cancelling does not override a users non-queue state.
     * For example, if a user is already in a game, cancelling should not incorrectly reset their status.
     */
    @Test
    public void testCancelDoesNotOverrideNonQueueStatus() {
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        user.setStatus(UserStatus.IN_GAME);
        ticket.cancel();

        assertEquals(UserStatus.IN_GAME, user.getStatus());
    }

    /**
     * Tests that the skill level is captured at the time of ticket creation.
     * This ensures matchmaking uses a consistent snapshot, even if the users skill changes later.
     */
    @Test
    public void testSkillSnapshotIsCaptured(){
        User user = createUser("A");

        int level = user.getLevel(GameType.TIC_TAC_TOE);
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);
        assertEquals(level, ticket.getSkillSnapshot());
    }
}