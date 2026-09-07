package ca.ucalgary.seng300;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single user's entry in matchmaking for a specific game type.
 * Stores a snapshot of the user's skill level at queue time so matching is stable.
 */
public final class MatchmakingTicket {

    public enum QueueStatus {
        QUEUED,
        MATCHED,
        CANCELLED
    }

    private final UUID uuid;
    private final LocalDateTime queuedAt;
    private final User player;
    private final GameType gameType;
    private final int skillSnapshot;
    private QueueStatus status;

    public MatchmakingTicket(User player, GameType gameType) {
        this.player = Objects.requireNonNull(player, "player must not be null");
        this.gameType = Objects.requireNonNull(gameType, "gameType must not be null");
        this.uuid = UUID.randomUUID();
        this.queuedAt = LocalDateTime.now();

        this.skillSnapshot = player.getLevel(gameType);

        this.status = QueueStatus.QUEUED;

        // Best-effort status update.
        if (player.getStatus() == UserStatus.ONLINE) {
            player.setStatus(UserStatus.IN_QUEUE);
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public LocalDateTime getQueuedAt() {
        return this.queuedAt;
    }

    public User getPlayer() {
        return this.player;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public int getSkillSnapshot() {
        return this.skillSnapshot;
    }

    public QueueStatus getStatus() {
        return this.status;
    }

    public void markMatched() {
        this.status = QueueStatus.MATCHED;
    }

    public void cancel() {
        this.status = QueueStatus.CANCELLED;

        // Only revert status if the user is still marked as queued.
        if (player.getStatus() == UserStatus.IN_QUEUE) {
            player.setStatus(UserStatus.ONLINE);
        }
    }
}
