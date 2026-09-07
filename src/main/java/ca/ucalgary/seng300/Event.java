package ca.ucalgary.seng300;

public enum Event {
    // Published by the `Database` event bus.
    DATABASE_DID_CHANGE,

    // Published by the `Matchmaker` event bus.
    USER_DID_CREATE_LOBBY,
    LOBBY_CREATED_FROM_QUEUE,
    USER_DID_EXIT_LOBBY,
    USER_DID_JOIN_LOBBY,
    USER_DID_QUEUE,
    USER_DID_CANCEL_QUEUE,
    LOBBY_IS_FULL,
    LOBBY_DESTROYED,
    MATCHMAKING_TICK // Internal to `Matchmaker`.
}
