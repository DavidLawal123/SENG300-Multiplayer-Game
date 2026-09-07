package ca.ucalgary.seng300;

import ca.ucalgary.seng300.statistics.PlayerStats;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates skill-based matchmaking rules.
 *
 * Scope (Iteration 1):
 * - Only 2-player games are supported (TicTacToe and Connect Four).
 *
 * Strategy:
 * - Find the best compatible pair (minimum skill difference) within a tolerance.
 * - Tolerance widens as players wait in the queue to avoid long wait times.
 *
 * Testing note:
 * - Public methods use LocalDateTime.now().
 * - Package-private helpers (currentToleranceAt / isCompatibleAt) accept "now" for deterministic tests.
 */
public final class SkillMatcher {
    private final int baseTolerance;
    private final int toleranceExpansionPerSecond;

    public SkillMatcher() {
        this.baseTolerance = 50;
        this.toleranceExpansionPerSecond = 10;
    }

    public SkillMatcher(int baseTolerance, int toleranceExpansionPerSecond) {
        if (baseTolerance < 0 || toleranceExpansionPerSecond < 0) {
            throw new IllegalArgumentException("tolerance values must be non-negative");
        }
        this.baseTolerance = baseTolerance;
        this.toleranceExpansionPerSecond = toleranceExpansionPerSecond;
    }

    /**
     * Returns the current tolerance for a queued ticket.
     *
     * Tolerance widens over time based on how long the ticket has been waiting:
     * baseTolerance + waitedSeconds * toleranceExpansionPerSecond.
     *
     * @param ticket non-null matchmaking ticket
     * @return current tolerance (non-negative), in rank hundredths
     */
    public int currentTolerance(MatchmakingTicket ticket) {
        Objects.requireNonNull(ticket);
        return currentToleranceAt(ticket, LocalDateTime.now());
    }

    /**
     * Checks if two tickets are compatible right now.
     *
     * Compatibility requires:
     * - same game type
     * - both tickets are in QUEUED status
     * - absolute skill difference is within the current tolerance window
     *
     * @param a non-null ticket
     * @param b non-null ticket
     * @return true if a and b can be matched, otherwise false
     */
    public boolean isCompatible(MatchmakingTicket a, MatchmakingTicket b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return isCompatibleAt(a, b, LocalDateTime.now());
    }

    /**
     * Selects a match group from a queue.
     *
     * Current scope (Iteration 1): only 2-player games are supported.
     *
     * @param queue non-null matchmaking queue
     * @param requiredPlayers must be 2 for the current implementation
     * @return Optional containing a list of exactly 2 tickets if a compatible match is found,
     *         otherwise Optional.empty()
     * @throws IllegalArgumentException if requiredPlayers != 2
     */
    public Optional<List<MatchmakingTicket>> selectMatchGroup(MatchmakingQueue queue, int requiredPlayers) {
        Objects.requireNonNull(queue);
        return selectMatchGroup(queue.getWaitingTickets(), requiredPlayers);
    }

    /**
     * Selects a match group from the provided tickets.
     *
     * Current scope (Iteration 1): only 2-player games are supported.
     *
     * Contract:
     * - If a match is found, the returned list will contain exactly 2 tickets.
     * - The pair chosen is the compatible pair with the smallest skill difference.
     *
     * @param tickets list of queued tickets (non-null)
     * @param requiredPlayers must be 2 for the current implementation
     * @return Optional containing a list of exactly 2 tickets if a compatible match is found,
     *         otherwise Optional.empty()
     * @throws IllegalArgumentException if requiredPlayers != 2
     */
    public Optional<List<MatchmakingTicket>> selectMatchGroup(List<MatchmakingTicket> tickets, int requiredPlayers) {
        Objects.requireNonNull(tickets);

        // Check all the tickets are also non-null (O(n)).
        for (MatchmakingTicket ticket: tickets) {
            Objects.requireNonNull(ticket);
        }

        if (requiredPlayers != 2) {
            throw new IllegalArgumentException(
                "SkillMatcher currently supports only 2-player matching. requested=" + requiredPlayers
            );
        }

        LocalDateTime now = LocalDateTime.now();

        MatchmakingTicket bestA = null;
        MatchmakingTicket bestB = null;
        int bestDiff = Integer.MAX_VALUE;

        // Find best two-player combination with lowest skill difference.
        // This complexity of this double for loop is ϴ((n choose 2)).
        for (int i = 0; i < tickets.size(); i++) {
            MatchmakingTicket a = tickets.get(i);

            for (int j = i + 1; j < tickets.size(); j++) {
                MatchmakingTicket b = tickets.get(j);

                if (!isCompatibleAt(a, b, now)) {
                    continue;
                }

                int diff = Math.abs(a.getSkillSnapshot() - b.getSkillSnapshot());
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestA = a;
                    bestB = b;
                }
            }
        }

        if (bestA == null || bestB == null) {
            return Optional.empty();
        }

        ArrayList<MatchmakingTicket> result = new ArrayList<>(2);
        result.add(bestA);
        result.add(bestB);

        // Stable ordering: earlier queued ticket first.
        result.sort(Comparator.comparing(MatchmakingTicket::getQueuedAt));
        return Optional.of(result);
    }

    // -------------------------
    // Helpers for deterministic tests
    // -------------------------

    int currentToleranceAt(MatchmakingTicket ticket, LocalDateTime now) {
        Objects.requireNonNull(ticket);
        Objects.requireNonNull(now);

        long waitedSeconds = Math.max(0L, Duration.between(ticket.getQueuedAt(), now).getSeconds());
        return Math.toIntExact(baseTolerance + (waitedSeconds * toleranceExpansionPerSecond));
    }

    boolean isCompatibleAt(MatchmakingTicket a, MatchmakingTicket b, LocalDateTime now) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        Objects.requireNonNull(now);

        if (a.getGameType() != b.getGameType()) {
            return false;
        }

        if (a.getStatus() != MatchmakingTicket.QueueStatus.QUEUED
            || b.getStatus() != MatchmakingTicket.QueueStatus.QUEUED) {
            return false;
        }

        int tolerance = Math.min(currentToleranceAt(a, now), currentToleranceAt(b, now));
        int diff = Math.abs(a.getSkillSnapshot() - b.getSkillSnapshot());
        return diff <= tolerance;
    }
}
