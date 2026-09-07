package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameStateFactory {
    public GameStateFactory() {}

    public GameState buildGameState(List<? extends Player> playerList, GameType gameType) {
        Objects.requireNonNull(playerList);
        Objects.requireNonNull(gameType);

        if (playerList.size() != 2) {
            throw new IllegalArgumentException("GameState requires exactly 2 players.");
        }

        int rows;
        int cols;

        switch (gameType) {
            case TIC_TAC_TOE -> { rows = 3; cols = 3; }
            case CONNECT_FOUR -> { rows = 6; cols = 7; }
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }

        ArrayList<ArrayList<Cell>> board = new ArrayList<>(rows);
        for (int r = 0; r < rows; r++) {
            ArrayList<Cell> row = new ArrayList<>(cols);
            for (int c = 0; c < cols; c++) {
                row.add(new Cell(r, c));
            }
            board.add(row);
        }

        Player p1 = playerList.get(0);
        Player p2 = playerList.get(1);

        return new GameState(board, p1, p2, p1);
    }
}