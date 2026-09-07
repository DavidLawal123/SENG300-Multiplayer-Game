package ca.ucalgary.seng300;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * NOTE: Since Database uses a fixed data.csv file, tests overwrite it using data.template.csv before each test
 * and restore it after.
 * This prevents permanent changes and keeps the tests independent.
 */
public class DatabaseTest {
    private static final Path data_path = Paths.get("src" , "database" , "data.csv");
    private static final Path template_path = Paths.get("src" , "database" , "data.template.csv");

    private String originalData;
    private boolean originalDataExisted;

    /**
     * Reset data.csv from template before every test.
     */
    @Before
    public void before() throws Exception{
        originalDataExisted = Files.exists(data_path);

        if(originalDataExisted){
            originalData = Files.readString(data_path, StandardCharsets.UTF_8);
        }else{
            originalData = null;
        }
        Files.copy(template_path, data_path, StandardCopyOption.REPLACE_EXISTING);
        Database.readFromFile();
    }

    /**
     * Restore original data.csv after every test
     */
    @After
    public void after() throws Exception{
        if(originalDataExisted){
            Files.writeString(data_path, originalData, StandardCharsets.UTF_8);
        }else{
            Files.deleteIfExists(data_path);
        }
    }

    private void loadEmptyDatabase() throws Exception{
        Files.writeString(data_path, "#lastModified=2026-03-22T12:00:00", StandardCharsets.UTF_8);
        Database.readFromFile();
    }

    /**
     * Test last modified date from template file
     */
    @Test
    public void testGetLastModifiedDate(){
        assertEquals(LocalDateTime.parse("2026-03-22T12:00:00"), Database.getLastModifiedDate());
    }

    /**
     * Test adding one user
     */
    @Test
    public void testAddUser(){
        int before = Database.getUsers().size();

        User user = new User("newUser" , "newUser@gmail.com", "pass");
        Database.addUser(user);

        assertTrue(Database.getUsers().contains(user));
        assertEquals(before + 1, Database.getUsers().size());
    }

    /**
     * Test adding two users
     */
    @Test
    public void testAddMultipleUsers(){
        int before = Database.getUsers().size();

        User user1 = new User("userA" , "userA@gmail.com" , "pass");
        User user2 = new User("userB" , "userB@gmail.com" , "pass");

        Database.addUser(user1);
        Database.addUser(user2);

        assertEquals(before + 2, Database.getUsers().size());
    }

    /**
     * Test both users were added
     */
    @Test
    public void testAddMultipleUsers1(){
        User user1 = new User("userA" , "userA@gmail.com" , "pass");
        User user2 = new User("userB" , "userB@gmail.com" , "pass");

        Database.addUser(user1);
        Database.addUser(user2);

        assertTrue(Database.getUsers().contains(user1));
        assertTrue(Database.getUsers().contains(user2));
    }

    /**
     * Test getUser size after adding three users
     */
    @Test
    public void testGetUser(){
        int before = Database.getUsers().size();

        User user1 = new User("u1" , "u1@gmail.com" , "pass");
        User user2 = new User("u2" , "u2@gmail.com" , "pass");
        User user3 = new User("u3" , "u3@gmail.com" , "pass");

        Database.addUser(user1);
        Database.addUser(user2);
        Database.addUser(user3);

        assertEquals(before + 3, Database.getUsers().size());
    }

    /**
     * Test getUsers return a copy
     */
    @Test
    public void testGetUserReturnCopy(){
        int before = Database.getUsers().size();

        ArrayList<User> list = Database.getUsers();
        list.clear();

        assertEquals(before, Database.getUsers().size());
    }

    /**
     * Test finding user by username when found
     */
    @Test
    public void testGetUserFromUsernameFound() throws Exception{
        User user = new User("scott", "scott@gmail.com" , "pass");
        Database.addUser(user);

        assertEquals(user, Database.getUserFromUsername("scott"));
    }

    /**
     * Test finding user by username with null input
     */
    @Test
    public void testGetUserFromUsernameNull() throws Exception{
        assertNull(Database.getUserFromUsername(null));
    }

    /**
     * Test finding user by UUID when found
     */
    @Test
    public void testGetUserFromUUIDFound(){
        User user = new User("a" , "a@gmail.com", "aaaa");
        Database.addUser(user);

        assertEquals(user, Database.getUserFromUUID(user.getUUID()));
    }

    /**
     * Test finding user by UUID when not found
     */
    @Test
    public void testGetUserFromUUIDNotFound(){
        assertNull(Database.getUserFromUUID(UUID.randomUUID()));
    }

    /**
     * Test getting users with online and offline status
     */
    @Test
    public void testGetUserWithStatus(){
        int onlineBefore = Database.getUsersWithStatus(UserStatus.ONLINE).size();
        int offlineBefore = Database.getUsersWithStatus(UserStatus.OFFLINE).size();

        User user1 = new User("e" , "e@gmail.com" , "abcd");
        User user2 = new User("f" , "f@gmail.com" , "defg");

        user1.setStatus(UserStatus.ONLINE);
        user2.setStatus(UserStatus.OFFLINE);

        Database.addUser(user1);
        Database.addUser(user2);

        assertEquals(onlineBefore + 1, Database.getUsersWithStatus(UserStatus.ONLINE).size());
        assertEquals(offlineBefore + 1, Database.getUsersWithStatus(UserStatus.OFFLINE).size());
    }

    @Test
    public void testGetUserWithStatus1(){
        int onlineBefore = Database.getUsersWithStatus(UserStatus.ONLINE).size();
        int offlineBefore = Database.getUsersWithStatus(UserStatus.OFFLINE).size();
        int inLobbyBefore = Database.getUsersWithStatus(UserStatus.IN_LOBBY).size();
        int inQueueBefore = Database.getUsersWithStatus(UserStatus.IN_QUEUE).size();

        User user1 = new User("e1" , "e1@gmail.com" , "abcd");
        User user2 = new User("f1" , "f1@gmail.com" , "defg");
        User user3 = new User("g1" , "g1@gmail.com" , "hijk");
        User user4 = new User("h1" , "h1@gmail.com" , "lmno");

        user1.setStatus(UserStatus.ONLINE);
        user2.setStatus(UserStatus.OFFLINE);
        user3.setStatus(UserStatus.ONLINE);
        user4.setStatus(UserStatus.IN_LOBBY);

        Database.addUser(user1);
        Database.addUser(user2);
        Database.addUser(user3);
        Database.addUser(user4);

        assertEquals(onlineBefore + 2, Database.getUsersWithStatus(UserStatus.ONLINE).size());
        assertEquals(offlineBefore + 1, Database.getUsersWithStatus(UserStatus.OFFLINE).size());
        assertEquals(inLobbyBefore + 1, Database.getUsersWithStatus(UserStatus.IN_LOBBY).size());
        assertEquals(inQueueBefore, Database.getUsersWithStatus(UserStatus.IN_QUEUE).size());
    }

    /**
     * Test writing to file.
     * File is restored after test, so no permanent change happens.
     */
    @Test
    public void testWriteToFile() throws Exception{
        User user = new User("writeUser" , "write@gmail.com" , "aabb");
        Database.addUser(user);

        Database.writeToFile();

        String contents = Files.readString(data_path, StandardCharsets.UTF_8);

        assertTrue(Files.exists(data_path));
        assertTrue(contents.contains("writeUser"));
        assertTrue(contents.contains("#lastModified="));
    }

    /**
     * Test reading from template file
     */
    @Test
    public void testReadFromFile() throws Exception{
        Database.readFromFile();

        assertNotNull(Database.getUsers());
        assertEquals(10, Database.getUsers().size());
    }

    /**
     * Test authentication success
     */
    @Test
    public void testAuthenticationValid() throws Exception{
        User user = new User("x", "x@gmail.com" , "abab");
        Database.addUser(user);

        assertTrue(Database.authenticate("x" , "abab"));
    }
    /**
     * Test authentication wrong password
     */
    @Test
    public void testAuthenticateWrongPassword() throws Exception{
        User user = new User("y" , "y@gmail.com" , "xxyy");
        Database.addUser(user);

        assertFalse(Database.authenticate("y" , "xyxy"));
    }

    /**
     * Test authentication when user is not found
     */
    @Test
    public void testAuthenticationUserNotFound() throws Exception{
        assertFalse(Database.authenticate("unknown" , "aaaa"));
    }

    /**
     * Test authentication on empty database
     */
    @Test
    public void testAuthenticateEmptyDatabase() throws Exception{
        loadEmptyDatabase();
        assertFalse(Database.authenticate("z" , "zzzz"));
    }

}