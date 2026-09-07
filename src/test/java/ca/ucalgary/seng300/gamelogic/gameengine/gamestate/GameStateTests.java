package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class GameStateTests {

    private Player player1;
    private Player player2;
    private ArrayList<ArrayList<Cell>> board;
    private GameState gs;

    @Before
    public void settingUp() {
        player1 = makingPlayer("alice", "alice@example.com");
        player2 = makingPlayer("bob", "bob@example.com");

        board = makeEmptyBoard(3, 3);
        gs = new GameState(board, player1, player2, player1);
    }


    private Player makingPlayer(String username, String email) {
        User user = new User(username, email, "pass123");
        UUID uuid = UUID.randomUUID();

        return new Player() {
            @Override
            public UUID getUUID() {
                return uuid;
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }
        };
    }


    private ArrayList<ArrayList<Cell>> makeEmptyBoard(int rows, int cols) {
        ArrayList<ArrayList<Cell>> nb = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(new Cell(i, j));
            }
            nb.add(row);
        }

        return nb;
    }

    // Constructor tests

    @Test
    public void initialValuesSetsCorrectly() {
        assertEquals(3, gs.getNumRows());
        assertEquals(3, gs.getNumCols());
        assertEquals(player1, gs.getP1());
        assertEquals(player2, gs.getP2());
        assertEquals(player1, gs.getPlayerTurnState());
        assertEquals(GameOutcome.ONGOING, gs.getGameOutcome());
        assertNull(gs.getWinner());
        assertNull(gs.getLoser());
    }

    @Test(expected = NullPointerException.class)
    public void whenBoardIsNull() {
        new GameState(null, player1, player2, player1);
    }

    @Test(expected = NullPointerException.class)
    public void whenPlayer1IsNull() {
        new GameState(board, null, player2, player1);
    }

    @Test(expected = NullPointerException.class)
    public void whenPlayer2IsNull() {
        new GameState(board, player1, null, player1);
    }

    @Test(expected = NullPointerException.class)
    public void whenTurnStateIsNull() {
        new GameState(board, player1, player2, null);
    }

    // Size tests

    @Test
    public void getNumRowsIsCorrect() {
        assertEquals(3, gs.getNumRows());
    }

    @Test
    public void getNumColsIsCorrect() {
        assertEquals(3, gs.getNumCols());
    }

    @Test
    public void getEmptyBoardSize() {
        GameState emptyState = new GameState(new ArrayList<>(), player1, player2, player1);
        assertEquals(0, emptyState.getNumCols());
        assertEquals(0, emptyState.getNumRows());
    }

    //Bounds tests

    @Test
    public void inBoundsIsTrue() {
        assertTrue(gs.inBounds(0, 0));
        assertTrue(gs.inBounds(2, 2));
        assertTrue(gs.inBounds(1, 1));
    }

    @Test
    public void inBoundsForNegativeRow() {
        assertFalse(gs.inBounds(-1, 0));
    }

    @Test
    public void inBoundsForNegativeCol() {
        assertFalse(gs.inBounds(0, -1));
    }

    @Test
    public void inBoundsForRowTooLarge() {
        assertFalse(gs.inBounds(3, 0));
    }

    @Test
    public void inBoundsForColTooLarge() {
        assertFalse(gs.inBounds(0, 3));
    }

    // Cell tests

    @Test
    public void getCellWorksCorrect() {
        Cell cell = gs.getCell(1, 1);
        assertNotNull(cell);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCellForNegativeRow() {
        gs.getCell(-1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCellForNegativeCol() {
        gs.getCell(0, -1);
    }

    @Test
            (expected = IndexOutOfBoundsException.class)
    public void getCellForRowTooLarge() {
        gs.getCell(3, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCellForColTooLarge() {
        gs.getCell(0, 3);
    }

    // Occupancy tests

    @Test
    public void getOccupancyIsEmpty() {
        Optional<Player> occupancy = gs.getOccupancy(0, 0);
        assertFalse(occupancy.isPresent());
    }

    @Test
    public void setOccupancyWorksCorrectly() {
        gs.setOccupancy(1, 1, player1);

        Optional<Player> occupancy = gs.getOccupancy(1, 1);
        assertTrue(occupancy.isPresent());
        assertEquals(player1, occupancy.get());
    }

    @Test
    public void setOccupancyForOtherPlayer() {
        gs.setOccupancy(2, 2, player2);

        Optional<Player> occupancy = gs.getOccupancy(2, 2);
        assertTrue(occupancy.isPresent());
        assertEquals(player2, occupancy.get());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setOccupancyForOutOfBoundsRow() {
        gs.setOccupancy(5, 0, player1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setOccupancyForOutOfBoundsColumn() {
        gs.setOccupancy(0, 5, player1);
    }

    @Test
    public void getOccupancyEmptyOutOfBoundsRow() {
        Optional<Player> occupancy = gs.getOccupancy(10, 0);
        assertFalse(occupancy.isPresent());
    }

    @Test
    public void getOccupancyEmptyOutOfBoundsColumn() {
        Optional<Player> occupancy = gs.getOccupancy(0, 10);
        assertFalse(occupancy.isPresent());
    }

    @Test
    public void getOccupancyForNegativeRow() {
        Optional<Player> occupancy = gs.getOccupancy(-1, 0);
        assertFalse(occupancy.isPresent());
    }

    @Test
    public void getOccupancyForNegativeColumn() {
        Optional<Player> occupancy = gs.getOccupancy(0, -1);
        assertFalse(occupancy.isPresent());
    }

    // Full board tests

    @Test
    public void isFullIsEmpty() {
        assertFalse(gs.isFull());
    }

    @Test
    public void isFullTrue() {
        for (int r = 0; r < gs.getNumRows(); r++) {
            for (int c = 0; c < gs.getNumCols(); c++) {
                gs.setOccupancy(r, c, player1);
            }
        }

        assertTrue(gs.isFull());
    }

    @Test
    public void isFullIsPartiallyFilled() {
        gs.setOccupancy(0, 0, player1);
        gs.setOccupancy(0, 1, player2);

        assertFalse(gs.isFull());
    }

    @Test
    public void isFullEmptyBoard() {
        GameState gameState = new GameState(new ArrayList<>(), player1, player2, player1);
        assertTrue(gameState.isFull());
    }

    // Turn and Outcome tests

    @Test
    public void setPlayerTurnStateWorksCorrectly() {
        gs.setPlayerTurnState(player2);
        assertEquals(player2, gs.getPlayerTurnState());
    }

    @Test(expected = NullPointerException.class)
    public void setPlayerTurnStateNull() {
        gs.setPlayerTurnState(null);
    }

    @Test
    public void setGameOutcomeWorksCorrectly() {
        gs.setGameOutcome(GameOutcome.WIN);
        assertEquals(GameOutcome.WIN, gs.getGameOutcome());
    }

    @Test(expected = NullPointerException.class)
    public void setGameOutcomeNull() {
        gs.setGameOutcome(null);
    }

    // Winner/loser tests

    @Test
    public void setWinnerWorksCorrectly() {
        gs.setWinner(player1);
        assertEquals(player1, gs.getWinner());
    }

    @Test
    public void setWinnerAllowsNull() {
        gs.setWinner(player1);
        gs.setWinner(null);
        assertNull(gs.getWinner());
    }

    @Test
    public void setLoserWorksCorrectly() {
        gs.setLoser(player2);
        assertEquals(player2, gs.getLoser());
    }

    @Test
    public void setLoserAllowsNull() {
        gs.setLoser(player2);
        gs.setLoser(null);
        assertNull(gs.getLoser());
    }

    @Test
    public void winnerLoserBothSetCorrectly() {
        gs.setWinner(player1);
        gs.setLoser(player2);

        assertEquals(player1, gs.getWinner());
        assertEquals(player2, gs.getLoser());
    }

    // Integration Test
    @Test
    public void boardUpdatingCorrectly() {
        gs.setOccupancy(2, 1, player2);

        Cell cell = gs.getCell(2, 1);
        Optional<Player> occupancy = gs.getOccupancy(2, 1);

        assertNotNull(cell);
        assertTrue(occupancy.isPresent());
        assertEquals(player2, occupancy.get());
    }
}