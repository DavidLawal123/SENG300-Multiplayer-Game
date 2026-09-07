package ca.ucalgary.seng300;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a game lobby that manages players before a game session starts.
 *
 * Handles:
 * - player management (add/remove users)
 * - lobby state (capacity, locked status)
 * - event publishing for lobby-related actions
 * - linking to a GameSession when the game begins
 *
 * Each lobby maintains its own EventBus to notify listeners of state changes.
 */
public abstract class Lobby {
    private final LocalDateTime creationDate;
    private final UUID uuid;

    private User owner;
    private final List<User> users;   // Includes owner.
    private final int capacity;       // >= 1

    private boolean locked;
    private GameSession gameSession;  // Set when game actually starts.
    private final EventBus eventBus;  // Created at initialization.

    /**
     * Initializes a new lobby with the given owner and capacity.
     *
     * The lobby starts with:
     * - a unique ID and creation timestamp
     * - the owner as the first user in the lobby
     * - an internal EventBus for lobby-specific events
     *
     * @param owner the user creating the lobby
     * @param capacity maximum number of players allowed
     * @throws IllegalArgumentException if capacity < 1
     */
    public Lobby(User owner, int capacity) {
        Objects.requireNonNull(owner, "The owning user must not be null.");

        if (capacity < 1) {
            throw new IllegalArgumentException(
                "Lobby must have a capacity >= 1.");
        }

        this.creationDate = LocalDateTime.now();
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        this.capacity = capacity;

        this.users = new ArrayList<>();
        // The list of users must never be empty. 
        // We add the owner to begin with.
        this.users.add(owner);

        this.eventBus = new EventBus();
    }

    /**
     * Associates a GameSession with this lobby once the game begins.
     *
     * Also sets this lobby as the owner of the game session.
     *
     * @param gameSession the game session to attach
     */
    public void setGameSession(GameSession gameSession) {
        this.gameSession = Objects.requireNonNull(gameSession);
        this.gameSession.setOwningLobby(this);
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Adds a player to the lobby after validating eligibility.
     *
     * Performs checks for:
     * - capacity limits
     * - duplicate users
     * - user availability
     * - preventing owner re-addition
     *
     * Updates user status and publishes relevant events.
     *
     * @param user the user to add to the lobby
     * @throws IllegalArgumentException if validation fails
     */
    public void addPlayer(User user) {
        Objects.requireNonNull(user, "User must not be null.");

        // Validate lobby constraints before adding player
        if (users.size() == capacity) {
            throw new IllegalArgumentException(
                "Lobby is already at capacity. Cannot add a new user.");
        } else if (users.contains(user)) {
            throw new IllegalArgumentException(
                "Lobby already has the user with ID "
                + user.getUUID().toString() 
                + ". Cannot add this user.");
        } else if (user.getStatus() == UserStatus.IN_GAME) {
            throw new IllegalArgumentException(
                "The user with ID "
                + user.getUUID().toString() 
                + " is not available (in-game).");
        } else if (user == this.owner) {
            throw new IllegalArgumentException(
                "The user with ID "
                + user.getUUID().toString() 
                + " is the owner of this lobby! He or she cannot be removed.");
        }

        users.add(user);

        // Update user status.
        user.setStatus(UserStatus.IN_LOBBY);

        // If lobby reaches capacity, notify listeners
        if (users.size() == capacity) {
            eventBus.publishEvent(Event.LOBBY_IS_FULL);
        }

        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);
    }

    /**
     * Removes a non-owner player from the lobby.
     *
     * Updates user status and publishes exit events.
     * Owner removal is not permitted through this method.
     *
     * @param user the player to remove
     * @throws IllegalArgumentException if user is not in lobby or is owner
     * @throws IllegalStateException if user state is inconsistent
     */
    public void removePlayer(User user) {
        Objects.requireNonNull(user);

        // Sanity check.
        if (!users.contains(user)) {
            throw new IllegalArgumentException(
                    "Lobby does not contain user with ID "
                            + user.getUUID() + ".");
        }

        // do not allow removing the owner via this method.
        if (user == this.owner) {
            throw new IllegalArgumentException(
                    "Cannot remove the owner of the lobby via removePlayer.");
        }

        // Remove user from lobby
        users.remove(user);

        // Reset user status since they are no longer in the lobby
        if (user.getStatus() == UserStatus.IN_LOBBY) {
            user.setStatus(UserStatus.ONLINE);
        } else {
            // What on earth happened? This is an internal error.
            throw new IllegalStateException(
                "User " 
                + user.getUUID() 
                + " should have been marked as IN_LOBBY. What happened?"
            );
        }

        // Notify listeners that a user has exited the lobby
        eventBus.publishEvent(Event.USER_DID_EXIT_LOBBY);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public UUID getUUID() {
        return uuid;
    }

    public User getOwner() {
        return owner;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return users.size() >= capacity;
    }

    public boolean getLocked() {
        return locked;
    }

    /**
     * Sets whether the lobby is locked from further player joins.
     *
     * @param locked true if the lobby should be locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Returns a copy of the current users in the lobby.
     *
     * A defensive copy is returned to prevent external modification
     * of the internal user list.
     *
     * @return list of users in the lobby
     */
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Lobby{");
        builder.append("uuid=").append(uuid);
        builder.append(", owner=").append(owner != null ? owner.getUsername() : "null");
        builder.append(", capacity=").append(capacity);
        builder.append(", locked=").append(locked);
        builder.append(", users=[");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            builder.append(user.getUsername())
                    .append(" (")
                    .append(user.getUUID())
                    .append(")");

            if (i < users.size() - 1) {
                builder.append(", ");
            }
        }

        builder.append("]}");
        return builder.toString();
    }
}
