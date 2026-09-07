package ca.ucalgary.seng300;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.statistics.MatchSummary;
import ca.ucalgary.seng300.statistics.PlayerStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single player account in the platform.
 * <p>
 * A User has a stable UUID, a username, a current matchmaking
 * level, and basic win/loss/draw statistics. Instances are created
 * either as new users in memory or by deserializing a previously
 * stored representation.
 * <p>
 * This class is immutable with respect to identity fields
 * uuid and creationDate and mutable with respect
 * to profile and statistics (username, status, level, wins, losses,
 * draws).
 */
public final class User implements Player {
    public static final int STARTINGLEVEL = 0;
    public static final int DEFAULTPFP = 1;

    /**
     * Time at which this User instance was created in memory.
     * <p>
     * Note: this is not the original account creation time.
     * When a user is deserialized, only the logical fields are
     * restored; creationDate is set to the time of deserialization.
     */
    private final LocalDateTime creationDate;
    private final UUID uuid;

    private String username;
    private UserStatus status;

    private String email;
    private String password;

    private final HashMap<GameType, PlayerStats> playerStats = new  HashMap<>();

    private int pfp;

    /**
     * Creates a brand-new user with a fresh UUID and default values.
     *
     * @param username non-null display name for the user
     * @throws NullPointerException     if username is null
     * @throws IllegalArgumentException if username contains a comma
     */
    public User(String username, String email, String password) {
        Objects.requireNonNull(username, "username must not be null");
        validateInput(username);
        Objects.requireNonNull(email, "email must not be null");
        validateInput(email);
        Objects.requireNonNull(password, "password must not be null");
        validateInput(password);

        this.creationDate = LocalDateTime.now();
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;

        this.status = UserStatus.ONLINE;

        for (GameType gameType : GameType.values()) {
            playerStats.put(gameType, new PlayerStats(uuid, gameType, STARTINGLEVEL));
        }

        this.pfp = DEFAULTPFP;
    }

    /**
     * Internal constructor used by deserialization to restore all
     * salient fields.
     *
     * @param uuid           unique id of the user
     * @param username       display name
     * @param email          user email
     * @param password       user password
     * @param tttLevel       current tictactoe matchmaking level
     * @param tttTotalWins   number of tictactoe wins
     * @param tttTotalLosses number of tictactoe losses
     * @param tttTotalDraws  number of tictactoe draws
     * @param tttLevel       current tictactoe matchmaking level
     * @param c4TotalWins    number of connect 4 wins
     * @param c4TotalLosses  number of connect 4 losses
     * @param c4TotalDraws   number of connect 4 draws
     * @param c4Level        current connect 4 matchmaking level
     * @param status         current UserStatus
     * @param pfp            current profile picture
     * @param creationDate   date of user creation (can be set to
     *                       LocalDateTime.now())
     */
    private User(
            UUID uuid,
            String username,
            String email,
            String password,
            int tttLevel,
            int tttTotalWins,
            int tttTotalLosses,
            int tttTotalDraws,
            int c4Level,
            int c4TotalWins,
            int c4TotalLosses,
            int c4TotalDraws,
            UserStatus status,
            int pfp,
            LocalDateTime creationDate) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.password = password;

        playerStats.put(GameType.TIC_TAC_TOE, new PlayerStats(uuid, GameType.TIC_TAC_TOE, tttTotalWins, tttTotalLosses, tttTotalDraws, tttLevel));
        playerStats.put(GameType.CONNECT_FOUR, new PlayerStats(uuid, GameType.CONNECT_FOUR, c4TotalWins, c4TotalLosses, c4TotalDraws, c4Level));

        this.status = status;
        this.creationDate = creationDate;
        this.pfp = pfp;
    }

    /**
     * Validates that the input string does not contain a comma character.
     * <p>
     * This validation is necessary because the serialization format uses
     * commas as delimiters, so any input containing a comma would corrupt
     * the serialized data.
     *
     * @param input the string to validate
     * @throws IllegalArgumentException if input contains a comma
     */
    private void validateInput(String input) {
        if (input.contains(",")) {
            throw new IllegalArgumentException(
                    "Input cannot contain comma character: '" + input + "'");
        }
    }

    /**
     * Tests if the provided password matches the user's stored password.
     * <p>
     * 
     * @param password the password to test
     * @return true if password matches, false otherwise
     */
    public boolean testPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Resurrect a user from a serialized representation.
     * This string format for this representation is as follows:
     * "uuid,username,wins,losses,draws,level,status,pfp"
     *
     * @param data serialized user string
     * @return The newly resurrected user (creation date is <b>not</b>
     *         preserved).
     * @throws Exception An exception may be thrown if parsing fails at some
     *                   step of deserialization.
     */
    public static User deserializeFrom(String data) throws Exception {
        Objects.requireNonNull(data, "data must not be null");

        // We use ',' as our delimiter.
        String[] parts = data.trim().split(",");

        // Assert the data has as many parts as we expect.
        if (parts.length != 15) {
            throw new IllegalArgumentException(
                    "User deserialization error with data '" + data + "'. Expected 15 segments, got:" + parts.length
                            + ".");
        }

        UUID uuid = UUID.fromString(parts[0]);
        String username = parts[1];
        String email = parts[2];
        String password = parts[3];
        int tttTotalWins = Integer.parseUnsignedInt(parts[4]);
        int tttTotalLosses = Integer.parseUnsignedInt(parts[5]);
        int tttTotalDraws = Integer.parseUnsignedInt(parts[6]);
        int tttLevel = Integer.parseInt(parts[7]);
        int c4TotalWins = Integer.parseUnsignedInt(parts[8]);
        int c4TotalLosses = Integer.parseUnsignedInt(parts[9]);
        int c4TotalDraws = Integer.parseUnsignedInt(parts[10]);
        int c4Level = Integer.parseInt(parts[11]);
        UserStatus status = UserStatus.valueOf(parts[12]);
        int pfp = Integer.parseUnsignedInt(parts[13]);
        LocalDateTime creationDate = LocalDateTime.parse(parts[14]);

        // Construct a new user with our deserialized data.
        return new User(uuid,
                username,
                email,
                password,
                tttLevel,
                tttTotalWins,
                tttTotalLosses,
                tttTotalDraws,
                c4Level,
                c4TotalWins,
                c4TotalLosses,
                c4TotalDraws,
                status,
                pfp,
                creationDate);
    }

    /**
     * Returns the in-memory creation time for this User
     * instance.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    /**
     * Returns the globally unique identifier for this user.
     *
     * @return user UUID
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Returns the user's display name.
     *
     * @return username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the user's current high-level status
     * (online, offline, in queue, in lobby, in game, etc.)
     *
     * @return current UserStatus
     */
    public UserStatus getStatus() {
        return this.status;
    }

    /**
     * Get the user's level for the provided type of game.
     *
     * @return Their current level, but specific to the game given.
     * @throws IllegalArgumentException Thrown if an unsupported game type is given.
     */
    public int getLevel(GameType gameType) {
        switch (gameType) {
            case TIC_TAC_TOE -> { return this.playerStats.get(GameType.TIC_TAC_TOE).getRankScore(); }
            case CONNECT_FOUR -> { return this.playerStats.get(GameType.CONNECT_FOUR).getRankScore(); }
            default ->
                throw new IllegalArgumentException(
                    "Unsupported game type: " + gameType);
        }
    }

    /**
     * Returns the user's email address.
     *
     * @return email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the PlayerStats object for the specified game
     * @param gameType The {@code GameType} to return stats for
     * @return The PlayerStats object for this user
     */
    public PlayerStats getPlayerStats(GameType gameType) {
        PlayerStats gameStats = this.playerStats.get(gameType);
        if (gameStats == null) {
            throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }

        return gameStats;
    }

    /**
     * Returns the user's profile picture code.
     *
     * @return profile picture code
     */
    public int getProfilePicture() {
        return pfp;
    }

    /**
     * Serializes this user into a single line of text.
     * <p>
     * The format is:
     * uuid,username,wins,losses,draws,level,status,pfp
     * <p>
     * This is the inverse of deserializeFrom(String).
     *
     * @return serialized representation of this user
     */
    public String serialize() {
        PlayerStats tictactoeStats = this.playerStats.get(GameType.TIC_TAC_TOE);
        PlayerStats connect4Stats = this.playerStats.get(GameType.CONNECT_FOUR);
        // uuid,username,email,password,wins,losses,draws,level,status,pfp,creationDate
        return uuid + "," +
                username + "," +
                email + "," +
                password + "," +
                tictactoeStats.getWins() + "," +
                tictactoeStats.getLosses() + "," +
                tictactoeStats.getDraws() + "," +
                tictactoeStats.getRankScore() + "," +
                connect4Stats.getWins() + "," +
                connect4Stats.getLosses() + "," +
                connect4Stats.getDraws() + "," +
                connect4Stats.getRankScore() + "," +
                status.name() + "," +
                pfp + "," +
                creationDate.toString();
    }

    /**
     * Updates the user's status.
     *
     * @param status new non-null UserStatus
     */
    public void setStatus(UserStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Updates the user's email address.
     *
     * @param email new non-null email address
     * @throws NullPointerException     if email is null
     * @throws IllegalArgumentException if email contains a comma
     */
    public void setEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");
        validateInput(email);
        this.email = email;
    }

    /**
     * Updates the user's password.
     *
     * @param password new non-null password
     * @throws NullPointerException     if password is null
     * @throws IllegalArgumentException if password contains a comma
     */
    public void setPassword(String password) {
        Objects.requireNonNull(password, "password must not be null");
        validateInput(password);
        this.password = password;
    }

    /**
     * Updates the user's username.
     *
     * @param username new non-null username
     * @throws NullPointerException     if username is null
     * @throws IllegalArgumentException if username contains a comma
     */
    public void setUsername(String username) {
        Objects.requireNonNull(username, "username must not be null");
        validateInput(username);
        this.username = username;
    }

    /**
     * Updates the user's profile picture
     * @param pfp new profile picture index
     */
    public void setPfp(int pfp) {
        this.pfp = pfp;
    }

    /**
     * Records the result of a match in the user's corresponding PlayerStats field
     * @param match A {@code MatchSummary} representing a completed match
     * @throws IllegalArgumentException IllegalArgumentException if {@code game} is not tracked by this manager or if the GameOutcome is ongoing.
     */
    public void recordMatch (MatchSummary match) {
        if (match.getResult() == GameOutcome.ONGOING) {
            throw new IllegalArgumentException("Match is not completed.");
        }

        switch (match.getGame()) {
            case TIC_TAC_TOE -> playerStats.get(GameType.TIC_TAC_TOE).recordMatch(match);
            case CONNECT_FOUR -> playerStats.get(GameType.CONNECT_FOUR).recordMatch(match);
            default -> throw new IllegalArgumentException("Unsupported game type: " + match.getGame());
        }
    }
}
