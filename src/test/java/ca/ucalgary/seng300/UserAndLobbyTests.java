package ca.ucalgary.seng300;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.statistics.MatchSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Dummy class for testing lobby class as it is abstract
class TestLobby extends Lobby {
    public TestLobby(User owner, int capacity) {
        super(owner, capacity);
    }
}
//To test GameSession
class TestGameSession extends GameSession{}

public class UserAndLobbyTests {

    // USER CLASS TEST CASES

    /**
     * This test checks if a user is created correctly with valid inputs
     */
    @Test
    public void userCreation() {
        User user = new User("alice", "alice@example.com", "pass123");

        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(UserStatus.ONLINE, user.getStatus());
        assertNotNull(user.getUUID());
        assertNotNull(user.getCreationDate());
    }

    /**
     * Tests if null username throws exception
     */
    @Test
    public void constructorNullUsername() {
        assertThrows(NullPointerException.class, () -> {
            new User(null,"mail","123");
        });
    }

    /**
     * Tests if comma in a username throws exception
     */
    @Test
    public void constructorInvalidUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("bad,name","mail","123");
        });
    }

    /**
     * Tests if null email throws exception
     */
    @Test
    public void constructorNullEmail() {
        assertThrows(NullPointerException.class, () -> {
            new User("ivan",null,"ivanPass");
        });
    }

    /**
     * Tests if null password throws exception
     */
    @Test
    public void constructorNullPassword() {
        assertThrows(NullPointerException.class, () -> {
            new User("julia","julia@example.com",null);
        });
    }

    //PASSWORD TESTS

    /**
     * This checks password is correct
     */
    @Test
    public void passwordCorrect() {
        User user = new User("charlie", "charlie@example.com", "querty123");

        assertTrue(user.testPassword("querty123"));
    }

    /**
     * This checks password is incorrect
     */
    @Test
    public void passwordIncorrect() {
        User user = new User("frank", "frank@example.com", "frankPass");

        assertFalse(user.testPassword("123"));
    }

    /**
     * This checks password is null
     */
    @Test
    public void passwordNull() {
        User user = new User("grace", "grace@example.com", "gracePass");

        assertFalse(user.testPassword(null));
    }

    /**
     * Tests the invalid CSV input throws exception
     */
    @Test
    public void deserializeInvalid() {
        assertThrows(Exception.class, () -> {
            User.deserializeFrom("bad,data");
        });
    }

    /**
     * Tests null data
     */
    @Test
    public void deserializeNull() {
        assertThrows(NullPointerException.class, () -> {
            User.deserializeFrom(null);
        });
    }

    /**
     * This test checks updating username, password and email
     */
    @Test
    public void settersTest() {
        User user = new User("bob","c@example.com", "secure123");

        user.setUsername("newUsername");
        user.setEmail("new@example.com");
        user.setPassword("newPassword");

        assertEquals("newUsername", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertTrue(user.testPassword("newPassword"));
    }

    /**
     * This test checks that user is created correctly with valid inputs
     */
    @Test
    public void setValidUsername() {
        User user = new User("alice", "alice@example.com", "pass123");

        user.setUsername("newUsername");
        assertEquals("newUsername", user.getUsername());
    }

    /**
     * This test checks that null username throws exception
     */
    @Test
    public void setUsernameNull() {
        User user = new User("ivan", "ivan@example.com", "ivanPass");

        assertThrows(NullPointerException.class, () -> {
            user.setUsername(null);
        });
    }

    /**
     * This test checks that null email throws exception
     */
    @Test
    public void setEmailNull() {
        User user = new User("ivan", "ivan@example.com", "ivanPass");

        assertThrows(NullPointerException.class, () -> {
            user.setEmail(null);
        });
    }

    /**
     * This test checks that null password throws exception
     */
    @Test
    public void setPasswordNull() {
        User user = new User("ivan", "ivan@example.com", "ivanPass");

        assertThrows(NullPointerException.class, () -> {
            user.setPassword(null);
        });
    }

    /**
     * This test checks that username with comma throws exception
     */
    @Test
    public void setUsernameComma() {
        User user = new User("grace", "grace@example.com", "gracePass");

        IllegalArgumentException expectation = assertThrows(IllegalArgumentException.class, () -> {
            user.setUsername("bad,username");
        });

        assertTrue(expectation.getMessage().contains("comma"));
    }

    /**
     * This test checks that email with comma throws exception
     */
    @Test
    public void setEmailComma() {
        User user = new User("grace", "grace@example.com", "gracePass");

        IllegalArgumentException expectation = assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("bad,email");
        });

        assertTrue(expectation.getMessage().contains("comma"));
    }

    /**
     * This test checks that password with comma throws exception
     */
    @Test
    public void setPasswordComma() {
        User user = new User("grace", "grace@example.com", "gracePass");

        IllegalArgumentException expectation = assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword("bad,password");
        });

        assertTrue(expectation.getMessage().contains("comma"));
    }

    /**
     *  This test checks that the valid player stats are returned
     */
    @Test
    public void playerStatsValid() {
        User user = new User("frank", "frank@example.com",  "frankPass");

        assertNotNull(user.getPlayerStats(GameType.TIC_TAC_TOE));
    }

    /**
     * This test checks that invalid game type throws exception
     */
    @Test
    public void playerStatsInvalid() {
        User user = new User("edgar", "edgar@example.com", "edgarPass");

        assertThrows(IllegalArgumentException.class, () -> {
            user.getPlayerStats(null);
        });
    }

    /**
     * This test checks that default level is correct
     */
    @Test
    public void levelTest() {
        User user = new User("heidi", "heidi@example.com",  "heidiPass");

        assertEquals(0, user.getLevel(GameType.TIC_TAC_TOE));
    }

    /**
     * This test checks that setting null status throws exception
     */
    @Test
    public void statusNull() {
        User user = new User("bob", "bob@example.com", "secure456");

        assertThrows(NullPointerException.class, () -> {
            user.setStatus(null);
        });
    }

    /**
     * This test checks that connect 4 level is correct
     */
    @Test
    public void connectFourLevel() {
        User user = new User("heidi", "heidi@example.com",  "heidiPass");

        assertEquals(0, user.getLevel(GameType.CONNECT_FOUR));
    }

    /**
     * This test checks that the users data is serialized properly
     */
    @Test
    public void serializeTest() {
        User user = new User("charlie", "charlie@example.com", "querty123");

        String data = user.serialize();

        assertTrue(data.contains("charlie"));
        assertTrue(data.contains("charlie@example.com"));
        assertTrue(data.contains("ONLINE"));
    }

    /**
     * This test checks that user is correctly created from serialized data
     */
    @Test
    public void deserializeTest() throws Exception {
        String userdata = "f1e2b0a-9c3a-4b6a-8e3f-12d4a6b7c8d9,alice,alice@example.com,pass123,12,3,1,1,12,3,1,5,ONLINE,1,2026-03-15T10:15:30";

        User user = User.deserializeFrom(userdata);

        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
    }

    /**
     * This test checks that same usernames are working correctly
     */
    @Test
    public void setUsernameSame() {
        User user = new User("bob", "bob@example.com", "secure456");

        user.setUsername("bob");
        assertEquals("bob", user.getUsername());
    }

    /**
     * This test checks that each user have a unique UUID
     */
    @Test
    public void uniqueUUID() {
        User user1 = new User("bob", "bob@example.com", "secure456");
        User user2 = new User("bob", "bob@example.com", "secure456");

        assertNotEquals(user1.getUUID(), user2.getUUID());
    }

    /**
     * This test checks the default profile picture value
     */
    @Test
    public void defaultProfilePicture() {
        User user = new User("bob", "bob@example.com", "secure456");

        assertEquals(User.DEFAULTPFP, user.getProfilePicture());
    }

    /**
     * This test checks that player stats for connect 4 are valid
     */
    @Test
    public void playerStatConnect4Valid() {
        User user = new User("bob", "bob@example.com", "secure456");

        assertNotNull(user.getPlayerStats(GameType.CONNECT_FOUR));
    }

    /**
     * This test checks that null match throwing exception
     */
    @Test
    public void recordMatchNull() {
        User user = new User("charlie", "charlie@example.com", "querty123");

        assertThrows(NullPointerException.class, () -> {
            user.recordMatch(null);
        });
    }

    /**
     * This test checks if ongoing match throws exception
     */
    @Test
    public void recordMatchOngoingException() {
        User user = new User("charlie", "charlie@example.com", "querty123");
        User opponent = new User("alice", "alice@example.com", "pass123");

        MatchSummary matchSummary = new MatchSummary(
                GameType.TIC_TAC_TOE,
                GameOutcome.ONGOING,
                user.getUUID(),
                opponent.getUUID()
        );
        assertThrows(IllegalArgumentException.class, () -> {
            user.recordMatch(matchSummary);
        });
    }

    //LOBBY CLASS TEST CASES

    /**
     * This test checks if lobby is created properly
     */
    @Test
    public void validLobbyCreation() {
        User owner = new User("alice", "alice@example.com", "pass123");
        Lobby lobby = new TestLobby(owner, 3);

        assertEquals(owner, lobby.getOwner());
        assertEquals(3, lobby.getCapacity());
        assertEquals(1, lobby.getUsers().size());
    }

    /**
     * This test checks if event bus is not null
     */
    @Test
    public void lobbyEventbussNotNull() {
        Lobby lobby = new TestLobby(new User("charlie", "charlie@example.com", "querty123"),2);

        assertNotNull(lobby.getEventBus());
    }

    /**
     * This test checks if lobby UUID is created
     */
    @Test
    public void lobbyUUIDnotNull() {
        User owner = new User("alice", "alice@example.com", "pass123");
        Lobby lobby = new TestLobby(owner, 2);

        assertNotNull(lobby.getUUID());
    }

    /**
     * This test checks if lobby creation date is set
     */
    @Test
    public void lobbyCreationDateNotNull() {
        User owner = new User("alice", "alice@example.com", "pass123");
        Lobby lobby = new TestLobby(owner, 2);

        assertNotNull(lobby.getCreationDate());
    }

    /**
     * This test checks if zero capacity throws exception
     */
    @Test
    public void invalidCapacityZero() {
        User owner = new User("bob", "bob@example.com", "secure456");

        assertThrows(IllegalArgumentException.class, () -> {
            new TestLobby(owner, 0);
        });
    }

    /**
     * This test checks if null owner throws exception
     */
    @Test
    public void ownerNull() {
        assertThrows(NullPointerException.class, () -> {
            new TestLobby(null, 3);
        });
    }

    /**
     * This test checks if player is added successfully
     */
    @Test
    public void addPlayerSuccess() {
        Lobby lobby = new TestLobby(new User("charlie", "charlie@example.com", "querty123"),2);
        User user = new User("bob", "bob@example.com", "secure456");

        lobby.addPlayer(user);
        assertTrue(lobby.getUsers().contains(user));
    }

    /**
     * This test checks if player status changes to IN_LOBBY
     */
    @Test
    public void playerChangesStatusToInLobby() {
        Lobby lobby = new TestLobby(new User("charlie", "charlie@example.com", "querty123"),2);
        User user = new User("bob", "bob@example.com", "secure456");

        lobby.addPlayer(user);

        assertEquals(UserStatus.IN_LOBBY, user.getStatus());
    }

    /**
     * This test checks if adding null player throws exception
     */
    @Test
    public void addNullUser() {
        Lobby lobby = new TestLobby(new User("charlie", "charlie@example.com", "querty123"),2);

        assertThrows(NullPointerException.class, () -> {
            lobby.addPlayer(null);
        });
    }

    /**
     * This test checks if adding duplicate user throws exception
     */
    @Test
    public void addDuplicateUser() {
        User user = new User("grace", "grace@example.com", "gracePass");
        Lobby lobby = new TestLobby(user, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            lobby.addPlayer(user);
        });
    }

    /**
     * This test checks if adding player when lobby is full throws exception
     */
    @Test
    public void addWhenFull() {
        Lobby lobby = new TestLobby(new User("edgar", "edgar@example.com", "edgarPass"),1);

        assertThrows(IllegalArgumentException.class, () -> {
            lobby.addPlayer(new User("charlie", "charlie@example.com", "querty123"));
        });
    }

    /**
     * This test checks if adding player in game throws exception
     */
    @Test
    public void addUserInGame() {
        Lobby lobby = new TestLobby(new User("edgar", "edgar@example.com", "edgarPass"),2);
        User user = new User("heidi", "heidi@example.com",  "heidiPass");

        user.setStatus(UserStatus.IN_GAME);

        assertThrows(IllegalArgumentException.class, () -> {
            lobby.addPlayer(user);
        });
    }

    /**
     * This test checks if owner cannot be added again
     */
    @Test
    public void addOwnerAgain() {
        User user = new User("edgar", "edgar@example.com", "edgarPass");
        Lobby lobby = new TestLobby(user,2);

        assertThrows(IllegalArgumentException.class, () -> {
            lobby.addPlayer(user);
        });
    }

    /**
     * This test checks if lobby becomes full
     */
    @Test
    public void lobbyBecomesFull() {
        Lobby lobby = new TestLobby(new User("edgar", "edgar@example.com", "edgarPass"),2);
        User user = new User("heidi", "heidi@example.com",  "heidiPass");

        lobby.addPlayer(user);
        assertTrue(lobby.isFull());

    }

    /**
     * This test checks if player is removed successfully
     */
    @Test
    public void removeTestPlayerSuccess() {
        User owner = new User("ivan", "ivan@example.com", "ivanPass");
        Lobby lobby = new TestLobby(owner, 2);
        User user = new User("bob","bob@example.com","secure456");

        lobby.addPlayer(user);
        lobby.removePlayer(user);

        assertFalse(lobby.getUsers().contains(user));
    }

    /**
     * This test checks if player status changes to ONLINE after removal
     */
    @Test
    public void removePlayerChangesStatustoOnline() {
        User owner = new User("ivan", "ivan@example.com", "ivanPass");
        Lobby lobby = new TestLobby(owner, 2);
        User user = new User("bob","bob@example.com","secure456");

        lobby.addPlayer(user);
        lobby.removePlayer(user);

        assertEquals(UserStatus.ONLINE, user.getStatus());
    }

    /**
     * This test checks if removing null user throws exception
     */
    @Test
    public void removeNullUser() {
        Lobby lobby = new TestLobby(new User("alice", "alice@example.com", "pass123"),2);

        assertThrows(NullPointerException.class, () ->{
            lobby.removePlayer(null);
        });
    }

    /**
     * This test checks if removing null user not in lobby throws exception
     */
    @Test
    public void removeUserNotInLobby() {
        Lobby lobby = new TestLobby(new User("bob", "bob@example.com","secure456"),2);
        User user = new User("alice", "alice@example.com", "pass123");

        assertThrows(IllegalArgumentException.class, () -> {
            lobby.removePlayer(user);
        });
    }

    /**
     * This test checks if owner cannot be removed
     */
    @Test
    public void removeOwner() {
        User owner = new User("ivan", "ivan@example.com", "ivanPass");
        Lobby lobby = new TestLobby(owner, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            lobby.removePlayer(owner);
        });
    }

    /**
     * This test checks if wrong user status throws exception on removal
     */
    @Test
    public void removeWrongUserStatus(){
        User owner = new User("ivan", "ivan@example.com", "ivanPass");
        Lobby lobby = new TestLobby(owner, 2);
        User user = new User("bob","bob@example.com","secure456");

        lobby.addPlayer(user);
        user.setStatus(UserStatus.IN_GAME);

        assertThrows(IllegalStateException.class, () -> {
            lobby.removePlayer(user);
        });
    }

    /**
     * This test checks if game session is correct
     */
    @Test
    public void setGameSession() {
        Lobby lobby = new TestLobby(new User("bob","bob@example.com","secure456"),2);
        GameSession gameSession = new TestGameSession();

        lobby.setGameSession(gameSession);

        assertEquals(gameSession, lobby.getGameSession());
    }

    /**
     * This test checks if setting null game session throws exception
     */
    @Test
    public void setGameSessionNull() {
        Lobby lobby = new TestLobby(new User("bob","bob@example.com","secure456"),2);

        assertThrows(NullPointerException.class, () ->{
            lobby.setGameSession(null);
        });
    }

    /**
     * This test checks if getUsers returns a copy
     */
    @Test
    public void getUsersReturnsCopy(){
        Lobby lobby = new TestLobby(new User("bob","bob@example.com","secure456"),2);

        List<User> users = lobby.getUsers();
        users.clear();

        assertEquals(1, lobby.getUsers().size());
    }

    /**
     * This test checks if lobby is locked
     */
    @Test
    public void setLocked() {
        Lobby lobby = new TestLobby(new User("bob","bob@example.com","secure456"),2);

        lobby.setLocked(true);

        assertTrue(lobby.getLocked());
    }

    /**
     * This test checks if lobby is not full
     */
    @Test
    public void isFullFalse() {
        Lobby lobby = new TestLobby(new User("bob","bob@example.com","secure456"),2);

        assertFalse(lobby.isFull());
    }

    /**
     * This test checks if lobby is full
     */
    @Test
    public void isFullTrue() {
        Lobby lobby = new TestLobby(new User("bob","bob@example.com","secure456"),1);

        assertTrue(lobby.isFull());
    }

    // TIC TAC TOE LOBBY TESTS

    /**
     * This test checks if TicTacToe Lobby is successfully created
     */
    @Test
    public void ticTacToeLobbyCreation() {
        User owner = new User("charlie", "charlie@example.com", "querty123");
        TicTacToeLobby tttlobby = new TicTacToeLobby(owner);

        assertEquals(owner, tttlobby.getOwner());
        assertEquals(2, tttlobby.getCapacity());
        assertEquals(1, tttlobby.getUsers().size());
        assertTrue(tttlobby.getUsers().contains(owner));
        assertFalse(tttlobby.isFull());
    }

    /**
     * This test checks if lobby becomes full after adding player
     */
    @Test
    public void ticTacToeLobbyBecomesFullAfterAddingOnePlayer() {
        User owner = new User("charlie", "charlie@example.com", "querty123");
        TicTacToeLobby tttlobby = new TicTacToeLobby(owner);
        User player = new User("alice", "alice@example.com", "pass123");

        tttlobby.addPlayer(player);

        assertTrue(tttlobby.isFull());
        assertEquals(2, tttlobby.getUsers().size());
        assertTrue(tttlobby.getUsers().contains(player));
    }

    /**
     * This test checks if null owner throws exception
     */
    @Test
    public void ticTacToelobbyOwnerNull() {
        assertThrows(NullPointerException.class, () -> {
            new TicTacToeLobby(null);
        });
    }

    /**
     * This test checks if third player cannot be added
     */
    @Test
    public void ticTacToeLobbyCannotAddThirdPlayer() {
        User owner = new User("charlie", "charlie@example.com", "querty123");
        TicTacToeLobby tttlobby = new TicTacToeLobby(owner);
        User p1 = new User("alice", "alice@example.com", "pass123");
        User p2 = new User("bob", "bob@example.com", "secure456");

        tttlobby.addPlayer(p1);

        assertThrows(IllegalArgumentException.class, () -> {
            tttlobby.addPlayer(p2);
        });
    }

    // CONNECT FOUR LOBBY TESTS

    /**
     * This test checks if Connect4 Lobby is successfully created
     */
    @Test
    public void connect4LobbyCreation() {
        User owner = new User("charlie", "charlie@example.com", "querty123");
        ConnectFourLobby c4lobby = new ConnectFourLobby(owner);

        assertEquals(owner, c4lobby.getOwner());
        assertEquals(2, c4lobby.getCapacity());
        assertEquals(1, c4lobby.getUsers().size());
        assertTrue(c4lobby.getUsers().contains(owner));
        assertFalse(c4lobby.isFull());
    }

    /**
     * This test checks if C4 lobby becomes full after adding player
     */
    @Test
    public void connect4LobbyBecomesFullAfterAddingOnePlayer() {
        User owner = new User("charlie", "charlie@example.com", "querty123");
        ConnectFourLobby c4lobby = new ConnectFourLobby(owner);
        User player = new User("alice", "alice@example.com", "pass123");

        c4lobby.addPlayer(player);

        assertTrue(c4lobby.isFull());
        assertEquals(2, c4lobby.getUsers().size());
        assertTrue(c4lobby.getUsers().contains(player));
    }

    /**
     * This test checks if null owner throws exception
     */
    @Test
    public void connect4lobbyOwnerNull() {
        assertThrows(NullPointerException.class, () -> {
            new ConnectFourLobby(null);
        });
    }

    /**
     * This test checks if third player cannot be added
     */
    @Test
    public void connect4LobbyCannotAddThirdPlayer() {
        User owner = new User("charlie", "charlie@example.com", "querty123");
        ConnectFourLobby c4lobby = new ConnectFourLobby(owner);
        User p1 = new User("alice", "alice@example.com", "pass123");
        User p2 = new User("bob", "bob@example.com", "secure456");

        c4lobby.addPlayer(p1);

        assertThrows(IllegalArgumentException.class, () -> {
            c4lobby.addPlayer(p2);
        });
    }

}