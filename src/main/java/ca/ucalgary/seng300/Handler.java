package ca.ucalgary.seng300;

import java.util.*;

/**
 * Handler represents a single event listener in the EventBus system.
 *
 * Each handler:
 * - wraps a Runnable (closure) that is executed when an event is triggered
 * - is assigned a unique UUID for identification and removal
 *
 * Handlers are registered through EventBus and invoked when their associated
 * event is published.
 */
public final class Handler {

    /**
     * The closure (callback) to execute when the event is triggered.
     */
    private final Runnable closure;

    /**
     * Unique identifier for this handler, used for removal.
     */
    private final UUID uuid;

    /**
     * Creates a new handler with the given closure.
     *
     * @param closure the callback to execute when the event is published
     */
    Handler(Runnable closure) {
        this.closure = Objects.requireNonNull(closure);
        this.uuid = UUID.randomUUID();
    }

    /**
     * Returns the closure associated with this handler.
     *
     * @return the Runnable to execute
     */
    public Runnable getClosure() {
        return this.closure;
    }

    /**
     * Returns the unique identifier of this handler.
     *
     * @return UUID of the handler
     */
    public UUID getUUID() {
        return this.uuid;
    }
}

