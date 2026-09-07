package ca.ucalgary.seng300;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Database implements SyncStatic {
    private static LocalDateTime lastModifiedDate = null;
    private final static ArrayList<User> users = new ArrayList<>();

    /**
     * Returns the the LocalDateTime when the data csv file was last modified.
     * (Not when the database objects were modified.)
     */
    public static LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    /*
     * Adds a new user to the local database.
     */
    public static void addUser(User user) {
        Database.users.add(user);
    }

    /*
     * Returns the list of users.
     */
    public static ArrayList<User> getUsers() {
        return new ArrayList<>(Database.users);
    }

    /*
     * Returns the the user with the specified username. Returns null if not found.
     */
    public static User getUserFromUsername(String username) throws Exception {
       for (User user : users) {
         if (user.getUsername().equals(username)){
            return user;
         }
       }
       return null;
    }

    /*
     * Returns the user with the specified UUID. Returns null if not found.
     */
    public static User getUserFromUUID(UUID uuid) {
        for (User user : users) {
            if (user.getUUID().equals(uuid)){
                return user;
            }
        }
        return null;
    }

    /*
     * Returns the list of users with the provided UserStatus
     */
    public static ArrayList<User> getUsersWithStatus(UserStatus status) {
        return users.stream()
                .filter(u -> u.getStatus() == status)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /*
     * Returns a string of the database data serialized.
     */
    private static String serialize() {
        return users.stream()
                .map(User::serialize)
                .collect(Collectors.joining("\n"));
    }

    /*
     * Reconstitutes the user list and last modified dates from the data csv
     */
    private static void deserialize(String data) throws Exception {
        ArrayList<User> parsed = new ArrayList<>();
        String[] lines = data.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty())
                continue; // skips blank lines

            if (line.startsWith("#lastModified=")) {
                // parses the last modified time after trimming the marker (#lastModified=)
                lastModifiedDate = LocalDateTime.parse(line.substring("#lastModified=".length()));
                continue;
            }

            parsed.add(User.deserializeFrom(line));
        }

        // updates the users list
        users.clear();
        users.addAll(parsed);
    }

    /*
     * Serializes the user list and writes it to the data csv.
     * Appends the current time as the last modified date to the csv.
     */
    public static void writeToFile() throws Exception {
        // Use networking stub to trigger database synchronization
        // (may change database contents).
        Database.synchronizeWithServer();

        // Path to data.csv relative to Database.java location
        Path path = Paths.get("src", "database", "data.csv");

        // Call serialize and then write contents.
        String userCSV = serialize();
        String footer = "#lastModified=" + LocalDateTime.now().toString();
        String data = userCSV + "\n" + footer;

        Files.writeString(path, data, StandardCharsets.UTF_8);
    }

    /*
     * Updates the userlist and last modified date by reading the csv file.
     * Clears any local data in the database.
     */
    public static void readFromFile() throws Exception {
        // Loads contents from file then deserializes.
        Path path = Paths.get("src", "database", "data.csv");

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Database file not found: " + path.toAbsolutePath());
        }

        String data = Files.readString(path, StandardCharsets.UTF_8);

        deserialize(data);

        // Use networking stub to trigger database synchronization
        // (may change database contents). Essentially, we are
        // refreshing the database, here.
        Database.synchronizeWithServer();
    }

    /*
     * Authenticates username passowrd pair. Returns false if incorrect password or username not found.
     */
    public static Boolean authenticate(String username, String password) throws Exception {
        User user = getUserFromUsername(username);
        if (user == null) {
            return false;
        }
        return user.testPassword(password);
    }

    //
    // Networking Stubs
    //

    /**
     * Synchronize the local database with a remote centralized server
     * (or perhaps a proxy for a more sophisticated implementation).
     * This method will upload local user data to the server database, which
     * will accept changes on a first-come, first-serve basis. If a local change
     * with the server, it is rejected.
     *
     * @returns A boolean indicating whether the local changes relative
     * to the server database were accepted or rejected.
     * @throws An IOException if there was an error connecting to the
     * server.
     */
    public static boolean synchronizeWithServer() throws IOException {
        // Internal functionality stub...

        return true; // Assume success for stub.
    }
}
