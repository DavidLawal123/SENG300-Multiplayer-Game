package ca.ucalgary.seng300.gamelogic.gameengine.gameoutcome;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.context.MoveContextFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.Cell;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class GameOutcomeEvaluatorSearchTests {

    private GameState gameState;
    private MoveContextFactory moveContextFactory;
    private Player player1;
    private Player player2;

    /**
     * This sets up game and players state before each tests
     */
    @Before
    public void settingUp() {
        moveContextFactory = new MoveContextFactory();
        player1 = makingPlayer("alice");
        player2 = makingPlayer("bob");

        List<Player> list = new ArrayList<>();
        list.add(player1);
        list.add(player2);

        GameStateFactory gameStateFactory = new GameStateFactory();
        gameState = gameStateFactory.buildGameState(list, GameType.CONNECT_FOUR);
    }

    /**
     * This creates a player with random UUID
     */
    private Player makingPlayer(String username) {
        User u = new User(username, username + "@example.com", "pass123");
        UUID id = UUID.randomUUID();

        return new Player() {
            @Override
            public UUID getUUID() {
                return id;
            }

            @Override
            public String getUsername() {
                return u.getUsername();
            }
        };
    }

    /**
     * This creates a move context for given position and player
     */
    private MoveContext ctx(int row, int col, Player player) {
        return moveContextFactory.buildCTX(row, col, player, gameState);
    }

    /**
     * This places a player at a specific position on the board
     */
    private void place(int row, int col, Player player) {
        Cell cell = gameState.getCell(row, col);
        cell.setOccupancy(player);
    }

    /**
     * This Tests that no win is found when there is no match of neighbors
     */
    @Test
    public void winSearchReturnsEmptyWhenNoMatchingNeighborsExist() {
        place(2, 2, player1);

        Optional<List<Cell>> list =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertFalse(list.isPresent());
    }

    /**
     * This Tests horizontal win to right
     */
    @Test
    public void winSearchFindsHorizontalWinToTheRight() {
        place(2, 2, player1);
        place(2, 3, player1);
        place(2, 4, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This Tests horizontal win using right and left both
     */
    @Test
    public void winSearchFindsHorizontalWinUsingBothDirections() {
        place(2, 1, player1);
        place(2, 2, player1);
        place(2, 3, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This Tests vertical win
     */
    @Test
    public void winSearchFindsVerticalWin() {
        place(1, 2, player1);
        place(2, 2, player1);
        place(3, 2, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This tests diagonal wins - upright and downleft
     */
    @Test
    public void winSearchFindsUpRightDownLeftDiagonalWin() {
        place(3, 1, player1);
        place(2, 2, player1);
        place(1, 3, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This tests diagonal wins - downright and upleft
     */
    @Test
    public void winSearchFindsDownRightUpLeftDiagonalWin() {
        place(1, 1, player1);
        place(2, 2, player1);
        place(3, 3, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This tests that opponent blocks win
     */
    @Test
    public void winSearchReturnsEmptyWhenLineIsBrokenByOpponent() {
        place(2, 2, player1);
        place(2, 3, player2);
        place(2, 4, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertFalse(result.isPresent());
    }

    /**
     * This tests empty cell breaks win
     */
    @Test
    public void winSearchReturnsEmptyWhenLineIsBrokenByEmptyCell() {
        place(2, 2, player1);
        place(2, 4, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertFalse(result.isPresent());
    }

    /**
     * This tests if two in row is not enough for 3 win length
     */
    @Test
    public void winSearchReturnsEmptyWhenOnlyTwoInARowForWinLengthThree() {
        place(2, 2, player1);
        place(2, 3, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 3);

        assertFalse(result.isPresent());
    }

    /**
     * This tests win detection at board edge
     */
    @Test
    public void winSearchWorksAtBoardEdge() {
        place(0, 0, player1);
        place(0, 1, player1);
        place(0, 2, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(0, 0, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This tests diagonal wins at corner
     */
    @Test
    public void winSearchWorksAtCornerDiagonal() {
        place(0, 0, player1);
        place(1, 1, player1);
        place(2, 2, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(0, 0, player1), 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    /**
     * This tests win length 1 return the starting cell
     */
    @Test
    public void winSearchForWinLengthOneReturnsRootImmediately() {
        place(2, 2, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(2, result.get().get(0).getRow());
        assertEquals(2, result.get().get(0).getCol());
    }

    /**
     * This tests finds 4 in row
     */
    @Test
    public void winSearchCanFindFourInRowWhenRequested() {
        place(2, 1, player1);
        place(2, 2, player1);
        place(2, 3, player1);
        place(2, 4, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 4);

        assertTrue(result.isPresent());
        assertEquals(4, result.get().size());
    }

    /**
     * This tests 3 in row is not enough 4 needed for win length 4
     */
    @Test
    public void winSearchReturnsEmptyWhenFourNeededButOnlyThreeExist() {
        place(2, 1, player1);
        place(2, 2, player1);
        place(2, 3, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 4);

        assertFalse(result.isPresent());
    }

    /**
     * This tests results size matches requested win length
     */
    @Test
    public void winSearchReturnsExactlyRequestedWinLength() {
        place(2, 0, player1);
        place(2, 1, player1);
        place(2, 2, player1);
        place(2, 3, player1);
        place(2, 4, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(2, 2, player1), 4);

        assertTrue(result.isPresent());
        assertEquals(4, result.get().size());
    }

    /**
     * This tests returned cells have correct coordinates
     */
    @Test
    public void winSearchReturnsCellsContainingTheCorrectCoordinates() {
        place(4, 1, player1);
        place(4, 2, player1);
        place(4, 3, player1);

        Optional<List<Cell>> result =
                GameOutcomeEvaluator.Search.winSearch(ctx(4, 2, player1), 3);

        assertTrue(result.isPresent());

        boolean has41 = false;
        boolean has42 = false;
        boolean has43 = false;

        for (Cell c : result.get()) {
            if (c.getRow() == 4 && c.getCol() == 1) {
                has41 = true;
            }
            if (c.getRow() == 4 && c.getCol() == 2) {
                has42 = true;
            }
            if (c.getRow() == 4 && c.getCol() == 3) {
                has43 = true;
            }
        }

        assertTrue(has41);
        assertTrue(has42);
        assertTrue(has43);
    }
}