package ca.ucalgary.seng300;

/**
 * High-level connection / activity state for a user.
 * Used by the platform core and UI to drive state transitions.
 */
public enum UserStatus {
    /** User is not connected to the platform. */
    OFFLINE,

    /** User is authenticated and on the platform, but not in queue or a lobby. */
    ONLINE,

    /** User is currently in the matchmaking queue. */
    IN_QUEUE,

    /** User is actively playing a game. */
    IN_GAME,

    /** User is inside a lobby but the match has not started yet. */
    IN_LOBBY
}

