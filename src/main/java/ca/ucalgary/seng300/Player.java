package ca.ucalgary.seng300;

import java.util.UUID;

/**
 * An interface for read-only access to core "player" properties
 * that can be safely passed around without the concern of
 * mutation.
 */
public interface Player {
    public UUID getUUID();
    public String getUsername();
}

