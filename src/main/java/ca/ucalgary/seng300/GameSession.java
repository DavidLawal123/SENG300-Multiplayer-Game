package ca.ucalgary.seng300;

import java.util.*;

/* The class which represents an actual session for a game, keeping
 * track of turns, game-state, and all those wonderful things.
 *
 * This class should be inherited for each of the games that will 
 * be created. 
 *
 * For example, one might wish to create a Chess or Checkers class 
 * which extends Game.
 */
public abstract class GameSession {
    /** The lobby (`Lobby`) that owns this game session. */
    private Lobby owningLobby = null;
    private int playerCount;

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setOwningLobby(Lobby lobby) {
        this.owningLobby = lobby;
    }
}

