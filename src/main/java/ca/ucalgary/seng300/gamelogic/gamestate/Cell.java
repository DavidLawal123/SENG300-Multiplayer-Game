package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.Player;

import java.util.Optional;
import java.util.UUID;

public class Cell {
    private Player occupancy; // null means empty
    private final UUID uuid;
    private final int row;
    private final int col;

    protected Cell(final int row, final int col) {
        this.row = row;
        this.col = col;
        this.uuid = UUID.randomUUID();
        this.occupancy = null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Optional<Player> getOccupancy() {
        return Optional.ofNullable(occupancy);
    }

    public void setOccupancy(Player player) {
        // player can be null to clear the cell
        this.occupancy = player;
    }

    public boolean isEmpty() {
        return occupancy == null;
    }
}