package ca.ucalgary.seng300;

import java.util.*;
import  org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test class verifies the behavior of the Matchmaker system.
 * Each test focuses on a specific feature such as queuing,lobby creation and edge cases(like invalid inputs, empty states, etc)
 */
public class MatchmakerTest {

    /**
     * A helper method created to quickly create users for testing and avoids repeating constructor code in every test.
     * @param name the username to assign to the test user
     * @return a new user instance with a generated email and default password
     */
    private User createUser(String name){
        return new User(name, name+"@email.com","password");
    }

    /**
     * Tests that the Matchmaker constructor initializes without crashing.
     */
    @Test
    public void testConstructorInitializesCorrectly(){
        Matchmaker matchmaker = new Matchmaker();
        //If constructor fails, this would be null or throw
        assertNotNull(matchmaker);
    }

    /**
     * Tests the normal case where a user successfully enters the matchmaking queue.
     */
    @Test
    public void testQueueForMatchSuccess(){
        Matchmaker matchmaker = new Matchmaker();
        User user = createUser("A");

        //User joins the queue
        MatchmakingTicket ticket = matchmaker.queueForMatch(user, GameType.TIC_TAC_TOE);

        //Ensures ticket is created
        assertNotNull(ticket);
        //Ensures correct user is stored in the ticket
        assertEquals(user, ticket.getPlayer());
        //Ensure correct game type is stored
        assertEquals(GameType.TIC_TAC_TOE, ticket.getGameType());
        //Ensure user status updates properly
        assertEquals(UserStatus.IN_QUEUE, user.getStatus());
    }

    /**
     * Tests that passing a nul GameType throws an exception.
     */
    @Test(expected = NullPointerException.class)
    public void testQueueForMatchNullGameType(){
        Matchmaker matchmaker = new Matchmaker();
        User user = createUser("A");
        //should throw because game type is required
        matchmaker.queueForMatch(user, null);
    }

    /**
     * Tests that passing a null User throws an exception.
     */
    @Test (expected = NullPointerException.class)
    public void testQueueMatchForNullUser(){
        Matchmaker matchmaker = new Matchmaker();

        //Should throw because user cannot be null
        matchmaker.queueForMatch(null, GameType.TIC_TAC_TOE);
    }

    /**
     * Tests that a user that is not in the online state cannot join the queue.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testQueueForMatchUserNotOnline(){
        Matchmaker matchmaker = new Matchmaker();
        User user = createUser("A");

        //sets invalid state
        user.setStatus(UserStatus.IN_GAME);
        //should throw because only online users can queue
        matchmaker.queueForMatch(user, GameType.TIC_TAC_TOE);
    }

    /**
     * Tests successful cancellation of a queued ticket.
     */
    @Test
    public void testCancelQueueSuccess(){
        Matchmaker matchmaker = new Matchmaker();
        User user = createUser("A");

        //add user to queue
        MatchmakingTicket ticket = matchmaker.queueForMatch(user, GameType.TIC_TAC_TOE);
        //cancel using ticket UUID
        boolean result = matchmaker.cancelQueue(ticket.getUUID());
        //should return true if successful cancel
        assertTrue(result);
        //ticket status should update
        assertEquals(MatchmakingTicket.QueueStatus.CANCELLED, ticket.getStatus());
    }

    /**
     * Tests cancelling a ticket that does not exist.
     */
    @Test
    public void testCancelQueueNotFound(){
        Matchmaker matchmaker = new Matchmaker();
        //Random UUID not in system
        boolean result = matchmaker.cancelQueue(UUID.randomUUID());
        //Should return false when there is nothing to cancel
        assertFalse(result);
    }

    /**
     * Tests creating a new lobby when none exists.
     */
    @Test
    public void testCreateNewLobby(){
        Matchmaker matchmaker = new Matchmaker();
        User user = createUser("A");
        //First user should create a lobby
        Lobby lobby = matchmaker.joinOrCreateLobby(user, GameType.TIC_TAC_TOE);
        //Lobby should exist
        assertNotNull(lobby);
        //Only one user inside
        assertEquals(1, lobby.getUsers().size());
        //the first user becomes the owner
        assertEquals(user, lobby.getOwner());
        //status updates to lobby state
        assertEquals(UserStatus.IN_LOBBY, user.getStatus());
    }

    /**
     * Tests that a second user joins an existing lobby instead of creating a new one.
     */
    @Test
    public void testJoinExistingLobby(){
        Matchmaker matchmaker = new Matchmaker();

        User u1 = createUser("A");
        User u2 = createUser("B");

        Lobby lobby1 = matchmaker.joinOrCreateLobby(u1, GameType.TIC_TAC_TOE);
        Lobby lobby2 = matchmaker.joinOrCreateLobby(u2, GameType.TIC_TAC_TOE);

        //both users should be in the same lobby
        assertEquals(lobby1, lobby2);
        //lobby should now have 2 users
        assertEquals(2, lobby1.getUsers().size());
    }

    /**
     * Tests that a lobby becomes locked once it is full.
     */
    @Test
    public void testLobbyBecomesLockedWhenFull(){
        Matchmaker matchmaker = new Matchmaker();

        User u1 = createUser("A");
        User u2 = createUser("B");

        Lobby lobby = matchmaker.joinOrCreateLobby(u1, GameType.TIC_TAC_TOE);
        matchmaker.joinOrCreateLobby(u2, GameType.TIC_TAC_TOE);

        //lobby should be full
        assertTrue(lobby.isFull());
        //and locked to prevent more joins
        assertTrue(lobby.getLocked());
    }

    /**
     * Tests finding available(open)lobbies.
     */
    @Test
    public void testFindOpenLobbies(){
        Matchmaker matchmaker = new Matchmaker();

        User u1 = createUser("A");

        matchmaker.joinOrCreateLobby(u1, GameType.TIC_TAC_TOE);

        List<Lobby> lobbies = matchmaker.findOpenLobbies(GameType.TIC_TAC_TOE);

        //should find one open lobby
        assertEquals(1, lobbies.size());
    }

    /**
     * Tests finding open lobbies when none exist.
     */
    @Test
    public void testFindOpenLobbiesEmpty() {
        Matchmaker matchmaker = new Matchmaker();

        List<Lobby> lobbies = matchmaker.findOpenLobbies(GameType.TIC_TAC_TOE);

        //should return empty list
        assertTrue(lobbies.isEmpty());
    }

    /**
     * Tests a user leaving a lobby.
     */
    @Test
    public void testLeaveLobby(){
        Matchmaker matchmaker = new Matchmaker();

        User u1 = createUser("A");
        User u2 = createUser("B");

        Lobby lobby = matchmaker.joinOrCreateLobby(u1, GameType.TIC_TAC_TOE);
        matchmaker.joinOrCreateLobby(u2, GameType.TIC_TAC_TOE);

        //Remove second user
        matchmaker.leaveLobby(u2,lobby);
        //only one user should remain
        assertEquals(1, lobby.getUsers().size());
        //User status should reset
        assertEquals(UserStatus.ONLINE, u2.getStatus());
    }

    /**
     * Tests that leaving a lobby not managed by Matchmaker throws an error.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testLeaveLobbyInvalidLobby(){
        Matchmaker matchmaker = new Matchmaker();

        User u1 = createUser("A");
        //creates a lobby not tracked by matchmaker
        Lobby fakeLobby = new TicTacToeLobby(u1);
        //should throw because matchmaker doesn't manage this lobby
        matchmaker.leaveLobby(u1, fakeLobby);
    }

}