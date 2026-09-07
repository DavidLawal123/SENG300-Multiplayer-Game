package ca.ucalgary.seng300.statistics;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.gamelogic.GameOutcome;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Records an immutable summary of a completed game match.
 */
public class MatchSummary {
    /** The outcome of the match (WIN, LOSS, DRAW). */
    private final GameOutcome result;

    /** The game type this object is summarizing */
    private final GameType game;

    /** The unique identifier of the local player. */
    private final UUID playerID;
    /** The unique identifier of the opponent. */
    private final UUID opponentID;

    /** The time of the match completion in {@code HH:mm} format. */
    private final String time;
    /** The date of the match completion in {@code YYYY-MM-DD} format. */
    private final String date;

    /**
     * Constructs a new {@link MatchSummary} with the results of a completed game and automatically timestamps it with the current system time.
     * @param result The {@link GameOutcome} indicating the match outcome.
     * @param playerID The unique identifier of the player.
     * @param opponentID The unique identifier of the opponent.
     */
    public MatchSummary (GameType gameType, GameOutcome result, UUID playerID, UUID opponentID) {
        Objects.requireNonNull(gameType);
        Objects.requireNonNull(result);
        Objects.requireNonNull(playerID);
        Objects.requireNonNull(opponentID);

        this.game = gameType;
        this.result = result;
        this.playerID = playerID;
        this.opponentID = opponentID;

        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();

        this.time = time.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.date = date.toString();
    }

    /**
     * Takes a {@code MatchSummary} as a parameter and builds an opposite version from the opponent's perspective
     * @param matchSummary The {@code MatchSummary} to reverse
     */
    public MatchSummary (MatchSummary matchSummary) {
        this.game = matchSummary.game;
        this.playerID = matchSummary.opponentID;
        this.opponentID = matchSummary.playerID;
        this.time = matchSummary.time;
        this.date = matchSummary.date;

        switch (matchSummary.result) {
            case WIN -> { this.result = GameOutcome.LOSS; }
            case LOSS -> { this.result = GameOutcome.WIN; }
            default -> { this.result = GameOutcome.DRAW; }
        }
    }

    public GameOutcome getResult() {
        return result;
    }

    public GameType getGame() {
        return game;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public UUID getOpponentID() {
        return opponentID;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
