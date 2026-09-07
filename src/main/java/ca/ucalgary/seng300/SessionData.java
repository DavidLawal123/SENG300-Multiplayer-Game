package ca.ucalgary.seng300;

import java.util.UUID;

public final class SessionData {

    private static UUID currentLoggedIn;

    public static void storeCurrentPlayer(UUID uuid) {
        currentLoggedIn = uuid;
    }

    public static UUID getCurrentLoggedInUUID() {
        return currentLoggedIn;
    }

}
