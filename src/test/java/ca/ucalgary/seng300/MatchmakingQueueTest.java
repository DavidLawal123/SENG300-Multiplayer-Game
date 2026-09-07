package ca.ucalgary.seng300;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.UUID;

/**
 * This test class verifies the behavior of the MatchmakingQueue.
 * The queue is responsible for storing and managing players waiting to be matched for a specific game type.
 */
public class MatchmakingQueueTest {

    /**
     * Helper method that creates users for testings.
     * @param name the username that is used to generate user details
     * @return a new user instance with a default email and password
     */
    private User createUser(String name){
        return new User(name, name+ "@email.com","password");
    }

    /**
     * Tests that the constructor correctly initializes the queue.
     * The game type should be stored and the queue should start empty.
     */
    @Test
    public void testConstructorValid() {
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        assertEquals(GameType.TIC_TAC_TOE, queue.getGameType());
        assertTrue(queue.getWaitingTickets().isEmpty());
    }

    /**
     * Tests that passing a null game type throws an error.
     */
    @Test (expected = NullPointerException.class)
    public void testConstructorNull(){
        new MatchmakingQueue(null);
    }

    /**
     * Tests adding a valid ticket to the queue.
     * The ticket should appear in the waiting list.
     */
    @Test
    public void testEnqueueValid() {
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        queue.enqueue(ticket);
        assertEquals(1, queue.getWaitingTickets().size());
        assertTrue(queue.getWaitingTickets().contains(ticket));
    }

    /**
     * Tests that adding a null ticket is not allowed.
     */
    @Test (expected = NullPointerException.class)
    public void testEnqueueNull(){
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        queue.enqueue(null);
    }

    /**
     * Tests that tickets with the wrong game type are rejected.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testEnqueueWrongGameType() {
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.CONNECT_FOUR);

        queue.enqueue(ticket);
    }

    /**
     * Tests that duplicate tickets cannot be added to the queue.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testEnqueueDuplicate(){
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        User user = createUser("A");

        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        queue.enqueue(ticket);
        queue.enqueue(ticket); //here is the duplicate
    }

    /**
     * Tests removing a ticket that does not exist.
     * Should throw an exception.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNonExisting(){
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        queue.remove(UUID.randomUUID());
    }

    /**
     * Tests that removing with a null UUID throws an error.
     */
    @Test (expected = NullPointerException.class)
    public void testRemoveNull() {
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        queue.remove(null);
    }

    /**
     * Tests removing a valid ticket from the queue.
     * The queue should become empty and the ticket status should update to cancelled.
     */
    @Test
    public void testRemoveValid(){
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);
        queue.enqueue(ticket);

        queue.remove(ticket.getUUID());

        assertTrue(queue.getWaitingTickets().isEmpty());
        assertEquals(MatchmakingTicket.QueueStatus.CANCELLED, ticket.getStatus());
    }

    /**
     * Tests that the returned waiting tickets list is immutable.
     * External code should not be able to modify the queue directly.
     */
    @Test
    public void testGetWaitingTickets() {
        MatchmakingQueue queue = new MatchmakingQueue(GameType.TIC_TAC_TOE);
        User user = createUser("A");
        MatchmakingTicket ticket = new MatchmakingTicket(user, GameType.TIC_TAC_TOE);

        queue.enqueue(ticket);

        try {
            queue.getWaitingTickets().add(ticket);
            fail("List should be immutable");
        } catch (UnsupportedOperationException e){
            //expected behavior
        }
    }
}