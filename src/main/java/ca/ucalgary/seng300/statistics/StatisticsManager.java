package ca.ucalgary.seng300.statistics;

import ca.ucalgary.seng300.Database;
import ca.ucalgary.seng300.GameType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manages and stores player statistics across different games.
 * <br><br/>
 * Allows for the tracking of individual player performance,
 * including rank score and match histories, organized to allow stats to be accessed per-game.
 */
public class StatisticsManager {
    /** Mapping of games to lists of player statistics associated with that game **/
    private final HashMap<GameType, ArrayList<PlayerStats>> gameStats = new HashMap<>();

    /**
     * Initializes a new {@link StatisticsManager} for all games.
     */
    public StatisticsManager () {
        try {
            Database.readFromFile();
        } catch (Exception e) {
            System.out.println("Error creating Statistics Manager: Could not load user data.");
        }

        for  (GameType game : GameType.values()) {
            loadGameStats(game);
        }
    }

    /**
     * Initializes a new {@link StatisticsManager} with a predefined set of games.
     * @param games An array of {@link GameType} to be tracked by the manager.
     */
    public StatisticsManager (GameType[] games) {
        try {
            Database.readFromFile();
        } catch (Exception e) {
            System.out.println("Error creating Statistics Manager: Could not load user data.");
        }

        for  (GameType game : games) {
            loadGameStats(game);
        }
    }

    /**
     * Reloads statistics information from Database for loaded games
     */
    public void reload () {
        try {
            Database.readFromFile();
        } catch (Exception e) {
            System.out.println("Error creating Statistics Manager: Could not load user data.");
        }

        for (GameType game : gameStats.keySet()) {
            loadGameStats(game);
        }
    }

    /**
     * Loads a list of {@code PlayerStats} for a specified game from the {@code Database}
     * @param gameType The {@code GameType} category to load stats
     */
    public void loadGameStats (GameType gameType) {
        ArrayList<PlayerStats> stats = Database
                .getUsers()
                .stream()
                .map(user -> user.getPlayerStats(gameType))
                .collect(Collectors.toCollection(ArrayList::new));

        gameStats.put(gameType, stats);

        sortLeaderboard(gameType);
    }

    /**
     * Sorts the {@link PlayerStats} list of a specified game
     * @param game The {@link GameType} category to sort
     * @return {@code true} if the sort is successful, {@code false} if it fails
     */
    public boolean sortLeaderboard (GameType game) {
        ArrayList<PlayerStats> leaderboard = gameStats.get(game);
        if (leaderboard == null) {
            return false;
        }

        Collections.sort(leaderboard);
        return true;
    }

    /**
     * Retrieves the list of player stats for a specified game
     * @param game The {@code GameType} category to return stats for
     * @return an {@code ArrayList} of {@code PlayerStats} objects for the specified game
     */
    public ArrayList<PlayerStats> getRawGameStats (GameType game) {
        return new ArrayList<>(gameStats.get(game));
    }

    /**
     * Retrieves an ArrayList of field values from the PlayerStats objects in the leaderboard
     * <br></br>
     * Written as PlayerStats::(getter for field), where (getter for field) is a getter method from the {@code PlayerStats} class,
     * such as PlayerStats::getUserID returns an ArrayList of userIDs in the order of the leaderboard
     * @param game The {@code GameType} category to search
     * @param extractor A specified method from {@code PlayerStats} to get a value from, PlayerStats::_______
     * @return An {@code ArrayList} containing values of a specified field from each {@code PlayerStats} object
     */
    public <R> ArrayList<R> getLeaderboard (GameType game, Function<PlayerStats, R> extractor) {
        ArrayList<PlayerStats> statsList = gameStats.get(game);
        if (statsList == null) {
            return null;
        }

        return statsList.stream()
                .map(extractor)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves the statistics object for a specific player in a specific game or creates and registers a new entry if none exists yet.
     * @param userID The unique identifier of the player.
     * @param game The {@link GameType} category to check.
     * @return The {@link PlayerStats} object associated with the user ID.
     * @throws IllegalArgumentException if {@code game} is not tracked by this manager or if no user exists with the requested UUID.
     */
    public PlayerStats getPlayerStats (UUID userID, GameType game) {
        Objects.requireNonNull(userID, "userID");
        Objects.requireNonNull(game, "game");
        ArrayList<PlayerStats> players = gameStats.get(game);
        if (players == null) {
            throw new IllegalArgumentException(game.toString() + " does not exist");
        }

        PlayerStats playerStats = players.stream()
                .filter(stats -> userID.equals(stats.userID))
                .findFirst()
                .orElse(null);

        if (playerStats == null) {
            throw new IllegalArgumentException("User with UUID:  " + userID + " does not exist");
        }

        return playerStats;
    }
}
