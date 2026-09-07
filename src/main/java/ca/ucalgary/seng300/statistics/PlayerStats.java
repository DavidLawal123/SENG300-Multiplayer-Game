package ca.ucalgary.seng300.statistics;

import java.util.Objects;
import java.util.UUID;

import ca.ucalgary.seng300.GameType;

/**
 * Tracks a player's performance metrics and ranking for a specific game.
 * <br><br/>
 * Maintains running totals of wins, losses, draws and calculates the player's rank score and win percentage.
 */
public class PlayerStats implements Comparable<PlayerStats> {
    /** Rank change per win/loss. */
    public static final int RANK_SCORE_STEP = 10;

    /** The unique identifier of the player. */
    public final UUID userID;
    /** The specific game type the stats are for. */
    public final GameType game;

    private int wins = 0;
    private int losses = 0;
    private int draws = 0;
    private int gamesPlayed = 0;
    private int rankScore = 0;

    public PlayerStats (UUID userID, GameType game, int startingRank) {
        this.userID = userID;
        this.game = game;
        rankScore = startingRank;
    }

    /**
     * Constructor to build PlayerStats object from existing serialized data.
     * @param userID The unique identifier of the user
     * @param game The {@code GameType} category of the stats
     * @param wins The number of wins recorded
     * @param losses The number of losses recorded
     * @param draws The number of draws recorded
     * @param rankScore The matchmaking rank score of the user
     */
    public PlayerStats (UUID userID, GameType game, int wins, int losses, int draws, int rankScore) {
        this.userID = userID;
        this.game = game;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.rankScore = rankScore;
        this.gamesPlayed = wins + losses + draws;
    }

    /**
     * Updates the player's totals and rank score based on the completed match.
     * @param match The {@link MatchSummary} containing the match result.
     */
    public void recordMatch (MatchSummary match) {
        switch (match.getResult()) {
            case WIN -> {
                wins++;
                rankScore += RANK_SCORE_STEP;
            }
            case LOSS -> {
                losses++;
                rankScore -= RANK_SCORE_STEP;

                if (rankScore < 0) rankScore = 0;
            }
            case DRAW -> draws++;
        }

        gamesPlayed++;
    }

    /** @return Total number of wins. */
    public int getWins () { return wins; }

    /** @return Total number of losses. */
    public int getLosses () { return losses; }

    /** @return Total number of draws. */
    public int getDraws () { return draws; }

    /** @return Total number of matches recorded. */
    public int getGamesPlayed () { return gamesPlayed; }

    /**
     * Calculates the percentage of games won.
     * @return The win rate as a percentage (0.0 to 100.0). Returns {@code 0} if no games are recorded.
     */
    public float getWinRate () {
        if (gamesPlayed == 0) {
            return 0;
        }

        return (float) wins / gamesPlayed * 100;
    }

    /** @return The current competitive rank score. */
    public int getRankScore () { return rankScore; }

    /** @return The unique identifier of the user */
    public UUID getUserID () { return userID; }

    /**
     * Compares this player to another object based on the unique {@code userID} and {@code game} types.
     * @param other   the reference object with which to compare.
     * @return {@code true} if the {@code userID} and {@code game} types match, {@code false} otherwise.
     */
    @Override
    public boolean equals (Object other) {
        if (other instanceof PlayerStats playerStats) {
            return userID.equals(playerStats.userID) && game == playerStats.game;
        }
        return false;
    }

    @Override
    public int hashCode () {
        return Objects.hash(userID, game);
    }

    /**
     * @return A formatted summary of the player's statistics for this game.
     */
    @Override
    public String toString () {
        String output = "";
        output += userID.toString() + "\n";
        output += "Game: " + game + "\n";
        output += "Wins: " + wins + "\n";
        output += "Draws: " + draws + "\n";
        output += "Losses: " + losses + "\n";
        output += "Win Rate: " + String.format("%.2f", getWinRate()) + "%\n";
        output += "Rank Score: " + rankScore + "\n";
        return output;
    }

    /**
     * Compares this player's rank score with another person's score.
     * Results in descending order, higher rank score first to be used in leaderboards.
     * @param other The other PlayerStats object to compare
     * @return A negative integer if this player has a higher score,
     * zero if the scores are equal, or a positive integer if this player has a lower score.
     */
    @Override
    public int compareTo (PlayerStats other) {
        int result = Integer.compare(other.getRankScore(), this.getRankScore());

        if (result != 0) return result;

        return Float.compare(other.getWinRate(), this.getWinRate());
    }
}
