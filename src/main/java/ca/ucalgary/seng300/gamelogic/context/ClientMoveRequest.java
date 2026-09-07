package ca.ucalgary.seng300.gamelogic.context;

import ca.ucalgary.seng300.Player;

public class ClientMoveRequest {
    private final int row;
    private final int col;
    private final Player player;

    protected ClientMoveRequest(int row, int col, final Player player) {
        this.row = row;
        this.col = col;
        this.player = player;
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
}
