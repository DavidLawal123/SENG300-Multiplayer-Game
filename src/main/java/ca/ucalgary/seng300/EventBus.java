package ca.ucalgary.seng300;

import java.util.*;
import java.util.Map.Entry;

/**
 * EventBus is a simple publish-subscribe system used to notify different parts
 * of the application when specific events occur.
 *
 * Components can:
 * - register handlers (callbacks) for specific events
 * - remove handlers when no longer needed
 * - publish events to trigger all associated handlers
 *
 * This allows loose coupling between systems (e.g., Matchmaker, UI, logging).
 */
// Example usage:
// 
// ```Lobby a = new Lobby(myUser, 3)
// Lobby.getEventBus().registerHandler(() -> {
//     System.out.println("Someone else has joined the lobby!")
//     // ...update ui...
// }, Event.USER_DID_JOIN_LOBBY)```
public final class EventBus {
    /**
     * Maps each event type to a list of handlers that should be executed
     * when that event is published.
     */
    public HashMap<Event, ArrayList<Handler>> handlers = new HashMap<>();

    /**
     * Registers a handler (callback) for a specific event.
     *
     * @param closure the code to run when the event is triggered
     * @param forEventType the event type to subscribe to
     * @return UUID of the handler, used for later removal if needed
     */
    public UUID registerHandler(Runnable closure, Event forEventType) {
        // Ensure handler list exists for this event type
        if (!handlers.containsKey(forEventType)) {
            handlers.put(forEventType, new ArrayList<>());
        }
        
        // Create a new handler entry with a unique UUID.
        Handler handler = new Handler(closure);

        // Add handler to the list for this event
        handlers.get(forEventType).add(handler);

        return handler.getUUID();
    }

    /**
     * Removes a previously registered handler using its UUID.
     *
     * @param uuid the unique identifier of the handler to remove
     * @throws IllegalArgumentException if the handler is not found
     */
    public void removeHandler(UUID uuid) throws IllegalArgumentException {
        // Iterate through all event handler lists
        for (Entry<Event, ArrayList<Handler>> entry: handlers.entrySet()) {
            ArrayList<Handler> sublist = entry.getValue();
            
            // Search for handler with matching UUID
            for (int i = 0; i < sublist.size(); i++) {
                if (sublist.get(i).getUUID().equals(uuid)) {
                    // Found! Remove and return to caller.
                    sublist.remove(i);
                    return;
                }
            }
        }

        // Throw error, if no handler found
        throw new IllegalArgumentException(
            "Failed to find the handler by the UUID " + uuid.toString());
    }

    /**
     * Publishes an event, triggering all registered handlers for that event.
     *
     * @param event the event to publish
     */
    public void publishEvent(Event event) {
        // Check if there are any handlers for this event
        if (handlers.containsKey(event)) {
            // Run each handler designated for the given event.
            for (Handler handler: handlers.get(event)) {
                handler.getClosure().run();
            }
        }
    }
}

