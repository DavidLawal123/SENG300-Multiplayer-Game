package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class GameState implements GameStateView {
    private final ArrayList<ArrayList<Cell>> board;
    private final Player p1;
    private final Player p2;

    private Player playerTurnState;
    private GameOutcome gameOutcome;
    private Player winner;
    private Player loser;

    protected GameState(final ArrayList<ArrayList<Cell>> board,
                        final Player p1,
                        final Player p2,
                        final Player playerTurnState) {
        this.board = Objects.requireNonNull(board);
        this.p1 = Objects.requireNonNull(p1);
        this.p2 = Objects.requireNonNull(p2);
        this.playerTurnState = Objects.requireNonNull(playerTurnState);

        this.gameOutcome = GameOutcome.ONGOING;
        this.winner = null;
        this.loser = null;
    }

    public int getNumRows() {
        return board.size();
    }

    public int getNumCols() {
        return board.isEmpty() ? 0 : board.get(0).size();
    }

    public Cell getCell(int row, int col) {
        if (!inBounds(row, col)) {
            throw new IndexOutOfBoundsException("Cell out of bounds: (" + row + "," + col + ")");
        }
        return board.get(row).get(col);
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < getNumRows() && col >= 0 && col < getNumCols();
    }

    public void setOccupancy(int row, int col, Player player) {
        getCell(row, col).setOccupancy(player);
    }

    @Override
    public Optional<Player> getOccupancy(int row, int col) {
        if (!inBounds(row, col)) {
            return Optional.empty();
        }
        return getCell(row, col).getOccupancy();
    }

    public boolean isFull() {
        for (int r = 0; r < getNumRows(); r++) {
            for (int c = 0; c < getNumCols(); c++) {
                if (getCell(r, c).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setPlayerTurnState(Player playerTurnState) {
        this.playerTurnState = Objects.requireNonNull(playerTurnState);
    }

    public void setGameOutcome(GameOutcome gameOutcome) {
        this.gameOutcome = Objects.requireNonNull(gameOutcome);
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setLoser(Player loser) {
        this.loser = loser;
    }

    @Override
    public Player getP1() {
        return p1;
    }

    @Override
    public Player getP2() {
        return p2;
    }

    @Override
    public Player getPlayerTurnState() {
        return playerTurnState;
    }

    @Override
    public GameOutcome getGameOutcome() {
        return gameOutcome;
    }

    @Override
    public Player getWinner() {
        return winner;
    }

    @Override
    public Player getLoser() {
        return loser;
    }
}