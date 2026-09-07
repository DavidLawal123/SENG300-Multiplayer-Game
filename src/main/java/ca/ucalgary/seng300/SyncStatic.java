package ca.ucalgary.seng300;

import java.io.IOException;

/**
 * An interface which should be applied to data structures
 * which should theoretically be synced to the server.
 * For example, the database and matchmaker should be `Sync`,
 * to allow for networking.
 *
 * This is the static version of the normal `Sync`
 * interface designed to force implementation of a static
 * synchronization method.
 */
public interface SyncStatic {
    /**
     * Synchronize the local database with a remote centralized server
     * (or perhaps a proxy for a more sophisticated implementation).
     * This method will upload local user data to the server database, which
     * will accept changes on a first-come, first-serve basis. If a local change
     * with the server, it is rejected.
     *
     * @returns A boolean indicating whether the local changes relative
     * to the server database were accepted or rejected.
     * @throws An IOException if there was an error connecting to the
     * server.
     */
    public static boolean synchronizeWithServer() throws IOException {
        // Stub.
        return false;
    }
}


