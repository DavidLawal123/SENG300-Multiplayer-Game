package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class CellTests {

    private Cell cell;
    private Player player1;
    private Player player2;

    @Before
    public void settingUp() {
        cell = new Cell(1, 2);
        player1 = makingPlayer("alice", "alice@example.com");
        player2 = makingPlayer("bob", "bob@example.com");
    }

    private Player makingPlayer(String username, String email) {
        User u = new User(username, email, "pass123");
        UUID uuid = UUID.randomUUID();

        return new Player() {
            @Override
            public UUID getUUID() {
                return uuid;
            }

            @Override
            public String getUsername() {
                return u.getUsername();
            }
        };
    }
    @Test
    public void constructorWorking() {
        assertNotNull(cell);
    }

    @Test
    public void rowSetCorrect() {
        assertEquals(1, cell.getRow());
    }

    @Test
    public void columnsSetCorrect() {
        assertEquals(2, cell.getCol());
    }

    @Test
    public void uuidIsCorrectlyCreated() {
        assertNotNull(cell.getUuid());
    }

    @Test
    public void uuidIsStayingSame() {
        UUID one = cell.getUuid();
        UUID two = cell.getUuid();

        assertEquals(one, two);
    }

    @Test
    public void occupancyStartingEmpty() {
        Optional<Player> optional = cell.getOccupancy();
        assertFalse(optional.isPresent());
    }

    @Test
    public void isEmptyTrue() {
        assertTrue(cell.isEmpty());
    }

    @Test
    public void setOccupancyWorking() {
        cell.setOccupancy(player1);

        Optional<Player> optional = cell.getOccupancy();
        assertTrue(optional.isPresent());
        assertEquals(player1, optional.get());
    }

    @Test
    public void setOccupancyWorkingforOtherPlayer() {
        cell.setOccupancy(player2);

        Optional<Player> optional = cell.getOccupancy();
        assertTrue(optional.isPresent());
        assertEquals(player2, optional.get());
    }

    @Test
    public void isEmptyFalse() {
        cell.setOccupancy(player1);
        assertFalse(cell.isEmpty());
    }

    @Test
    public void setOccupancyClearCellNull() {
        cell.setOccupancy(player1);
        cell.setOccupancy(null);

        assertFalse(cell.getOccupancy().isPresent());
        assertTrue(cell.isEmpty());
    }

    @Test
    public void getOccupancyclearIsEmpty() {
        cell.setOccupancy(player1);
        cell.setOccupancy(null);

        Optional<Player> optional = cell.getOccupancy();
        assertFalse(optional.isPresent());
    }

    @Test
    public void replacingOccupancyWorks() {
        cell.setOccupancy(player1);
        cell.setOccupancy(player2);

        Optional<Player> optional = cell.getOccupancy();
        assertTrue(optional.isPresent());
        assertEquals(player2, optional.get());
    }

    @Test
    public void rowsAreNotChanging() {
        cell.setOccupancy(player1);
        assertEquals(1, cell.getRow());
    }

    @Test
    public void columnsAreNotChanging() {
        cell.setOccupancy(player1);
        assertEquals(2, cell.getCol());
    }

    @Test
    public void uuidDoesNotChangeAfterChangingOccupancy() {
        UUID original = cell.getUuid();

        cell.setOccupancy(player1);
        cell.setOccupancy(null);
        cell.setOccupancy(player2);

        UUID newOne = cell.getUuid();
        assertEquals(original, newOne);
    }

    @Test
    public void clearingManyTimes() {
        cell.setOccupancy(player1);
        cell.setOccupancy(null);
        cell.setOccupancy(null);

        assertTrue(cell.isEmpty());
    }

    @Test
    public void settingNullonEmptyCell() {
        cell.setOccupancy(null);

        assertTrue(cell.isEmpty());
        assertFalse(cell.getOccupancy().isPresent());
    }

    @Test
    public void getOccupancyResult() {
        cell.setOccupancy(player1);

        Optional<Player> first = cell.getOccupancy();
        Optional<Player> second = cell.getOccupancy();

        assertEquals(first, second);
    }
}