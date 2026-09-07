package ca.ucalgary.seng300.statistics;

import ca.ucalgary.seng300.Database;
import ca.ucalgary.seng300.User;

/**
 * Acts as the entry point for recording completed match results.
 * <p>
 * Bridges the in-memory statistics system managed by StatisticsManager
 * with the persistent Database, ensuring that match outcomes are
 * reflected both in the live leaderboard rankings and in the stored player data.
 */

public class StatisticsUpdater {

    /**
     * Records the result of a completed match.
     * <p>
     * Saves the result of a completed match for both players to the Database.
     * @param match The {@code MatchSummary} describing the completed match
     * @return true if the match was recorded and persisted successfully,
     * false if any step of the process fails
     */
    public static boolean recordMatch(MatchSummary match) {
        try {

            try {
                Database.readFromFile();
            } catch (Exception e) {
                System.out.println("StatisticsUpdater: Failed to read database — " + e.getMessage());
                return false;
            }

            User player = Database.getUserFromUUID(match.getPlayerID());
            User opponent = Database.getUserFromUUID(match.getOpponentID());

            if (player == null || opponent == null) {
                System.out.println("StatisticsUpdater: Could not find one or both players in database.");
                return false;
            }

            player.recordMatch(match);
            opponent.recordMatch(new MatchSummary(match));

            // Persist all changes to the CSV file
            Database.writeToFile();
            return true;

        } catch (Exception e) {
            System.out.println("StatisticsUpdater: Failed to record match — " + e.getMessage());
            return false;
        }
    }
}