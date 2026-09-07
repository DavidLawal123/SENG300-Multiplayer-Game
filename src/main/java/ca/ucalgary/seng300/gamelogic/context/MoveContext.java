package ca.ucalgary.seng300.gamelogic.context;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;

public class MoveContext {
    private final int row;
    private final int col;
    private final Player player;
    private final GameState gs;

    protected MoveContext(final int row, final int col, final Player player, final GameState gs) {
        this.row = row;
        this.col = col;
        this.player = player;
        this.gs = gs;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Player getPlayer() {
        return player;
    }

    public GameState getGameState() {
        return gs;
    }
}
