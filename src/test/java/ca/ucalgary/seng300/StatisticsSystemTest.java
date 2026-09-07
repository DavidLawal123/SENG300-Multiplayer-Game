package ca.ucalgary.seng300;

import ca.ucalgary.seng300.Database;
import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.User;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.statistics.MatchSummary;
import ca.ucalgary.seng300.statistics.PlayerStats;
import ca.ucalgary.seng300.statistics.StatisticsManager;
import ca.ucalgary.seng300.statistics.StatisticsUpdater;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class StatisticsSystemTest {
    private static final Path data_path = Paths.get("src" , "database", "data.csv");
    private static final Path template_path = Paths.get("src" , "database" , "data.template.csv");

    private String originalData;
    private boolean originalDataExisted;

    private GameType gameOne;
    private GameType gameTwo;

    /**
     * NOTE:
     * Database uses a fixed data.csv file.
     * Before each test, data.csv file reset using data.template.csv.
     * After each test, the original file content is restored.
     * This would prevent permanent CSV changes
     */
    @Before
    public void before() throws Exception{
        originalDataExisted = Files.exists(data_path);

        if(originalDataExisted){
            originalData = Files.readString(data_path, StandardCharsets.UTF_8);
        }else{
            originalData = null;
        }

        Files.copy(template_path, data_path, StandardCopyOption.REPLACE_EXISTING);
        Database.readFromFile();

        gameOne = GameType.TIC_TAC_TOE;
        gameTwo = GameType.CONNECT_FOUR;
    }

    /**
     * Restore the original data.csv after each test.
     */
    @After
    public void after() throws Exception{
        if(originalDataExisted){
            Files.writeString(data_path, originalData, StandardCharsets.UTF_8);
        }else{
            Files.deleteIfExists(data_path);
        }
    }

    //StatisticsManager Tests

    /**
     * Test constructor loads supported games.
     */
    @Test
    public void testDefaultConstructorLoadsSupportedGames(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{GameType.TIC_TAC_TOE, GameType.CONNECT_FOUR});

        assertNotNull(manager.getRawGameStats(GameType.TIC_TAC_TOE));
        assertNotNull(manager.getRawGameStats(GameType.CONNECT_FOUR));
    }

    /**
     * Test constructor with one selected game only loads that game
     */
    @Test
    public void testSelectedConstructorLoadsChosenGame(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        assertNotNull(manager.getRawGameStats(gameOne));
        assertNull(manager.getLeaderboard(gameTwo, PlayerStats::getUserID));
    }

    /**
     * Test reload keeps loaded game available
     */
    @Test
    public void testReload(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        manager.reload();

        assertNotNull(manager.getRawGameStats(gameOne));
    }

    /**
     * Test loadGameStats loads stats for a valid game
     */
    @Test
    public void testLoadGameStats(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        manager.loadGameStats(gameOne);

        assertNotNull(manager.getRawGameStats(gameOne));
    }

    /**
     * Test loaded stats count matches database user count
     */
    @Test
    public void testLoadGameStatsCountMatchesUsers(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        assertEquals(Database.getUsers().size(), manager.getRawGameStats(gameOne).size());
    }

    /**
     * Test sortLeaderboard returns true for loaded game
     */
    @Test
    public void testSortLeaderboardLoadedGame(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        assertTrue(manager.sortLeaderboard(gameOne));
    }

    /**
     * Test sortLeaderboard returns false for unloaded game
     */
    @Test
    public void testSortLeaderboardUnloadedGame(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        assertFalse(manager.sortLeaderboard(gameTwo));
    }

    /**
     * Test getRawGameStats returns stats for the loaded game
     */
    @Test
    public void testGetRawGameStats(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        ArrayList<PlayerStats> stats = manager.getRawGameStats(gameOne);

        assertNotNull(stats);
        assertEquals(Database.getUsers().size(), stats.size());
    }

    /**
     * Test getRawGameStats returns a copy of the list
     */
    @Test
    public void testGetRawGamesStatsReturnsCopy(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        int before = manager.getRawGameStats(gameOne).size();
        ArrayList<PlayerStats> stats = manager.getRawGameStats(gameOne);
        stats.clear();

        assertEquals(before, manager.getRawGameStats(gameOne).size());
    }

    /**
     * Test getRawGameStats throws exception for unloaded game
     */
    @Test(expected = NullPointerException.class)
    public void testGetRawGamesStatsUnloadedGame(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});
        manager.getRawGameStats(gameTwo);
    }

    /**
     * Test getLeaderboard returns extracted user IDs
     */
    @Test
    public void testGetLeaderboardUserIDs(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        ArrayList<UUID> ids = manager.getLeaderboard(gameOne, PlayerStats::getUserID);

        assertNotNull(ids);
        assertEquals(Database.getUsers().size(), ids.size());
    }

    /**
     * Test getLeaderboard returns extracted rank scores
     */
    @Test
    public void testGetLeaderboardRankScore(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        ArrayList<Integer> ranks = manager.getLeaderboard(gameOne, PlayerStats::getRankScore);

        assertNotNull(ranks);
        assertEquals(Database.getUsers().size(), ranks.size());
    }

    /**
     * Test getLeaderboard returns null for unloaded game
     */
    @Test
    public void testGetLeaderboardUnloadedGame(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        assertNull(manager.getLeaderboard(gameTwo, PlayerStats::getUserID));
    }

    /**
     * Test getPlayerStats return valid stats for a known player
     */
    @Test
    public void testPlayerStats(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});
        User user = Database.getUsers().getFirst();

        PlayerStats stats = manager.getPlayerStats(user.getUUID(), gameOne);

        assertNotNull(stats);
        assertEquals(user.getUUID(), stats.getUserID());
        assertEquals(gameOne, stats.game);
    }

    /**
     * Test getPlayerStats with unloaded game
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayerStatsUnloadedGame(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});
        User user = Database.getUsers().getFirst();

        manager.getPlayerStats(user.getUUID(), gameTwo);
    }

    /**
     * Test getPlayerStats with unknown UUID
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayerStatsUnknownUUID(){
        StatisticsManager manager = new StatisticsManager(new GameType[]{gameOne});

        manager.getPlayerStats(UUID.randomUUID(), gameOne);
    }


    //Statistics Updater Tests

    /**
     * Test recordMatch returns true for valid match
     */
    @Test
    public void testRecordMatchValid(){
        ArrayList<User> users = Database.getUsers();
        User player = users.get(0);
        User opponent = users.get(1);

        MatchSummary summary = new MatchSummary(
                gameOne,
                GameOutcome.WIN,
                player.getUUID(),
                opponent.getUUID()
        );
        assertTrue(StatisticsUpdater.recordMatch(summary));
    }

    /**
     * Test recordMatch returns false when opponent is not found
     */
    @Test
    public void testRecordMatchPlayerNotFound(){
        ArrayList<User> users = Database.getUsers();
        User opponent = users.getFirst();

        MatchSummary summary = new MatchSummary(
                gameOne,
                GameOutcome.WIN,
                UUID.randomUUID(),
                opponent.getUUID()
        );
        assertFalse(StatisticsUpdater.recordMatch(summary));
    }

    /**
     * Test recordMatch returns false when opponent is not found
     */
    @Test
    public void testRecordMatchOpponentNotFound(){
        ArrayList<User> users = Database.getUsers();
        User player = users.getFirst();

        MatchSummary summary = new MatchSummary(
                gameOne,
                GameOutcome.WIN,
                player.getUUID(),
                UUID.randomUUID()
        );
        assertFalse(StatisticsUpdater.recordMatch(summary));
    }

    /**
     * Test valid match updates player win and opponent loses
     */
    @Test
    public void testRecordMatchUpdatesStats() throws Exception{
        ArrayList<User> users = Database.getUsers();
        User player = users.get(0);
        User opponent = users.get(1);

        int playerWinsBefore = player.getPlayerStats(gameOne).getWins();
        int opponentLossesBefore = opponent.getPlayerStats(gameOne).getLosses();

        MatchSummary summary = new MatchSummary(
                gameOne,
                GameOutcome.WIN,
                player.getUUID(),
                opponent.getUUID()
        );
        assertTrue(StatisticsUpdater.recordMatch(summary));

        Database.readFromFile();

        User updatedPlayer = Database.getUserFromUUID(player.getUUID());
        User updatedOpponent = Database.getUserFromUUID(opponent.getUUID());

        assertEquals(playerWinsBefore + 1, updatedPlayer.getPlayerStats(gameOne).getWins());
        assertEquals(opponentLossesBefore + 1, updatedOpponent.getPlayerStats(gameOne).getLosses());
    }

    /**
     * Test draw match updates both players' draw count
     */
    @Test
    public void testRecordMatchDraw() throws Exception{
        ArrayList<User> users = Database.getUsers();
        User player = users.get(0);
        User opponent = users.get(1);

        int playerDrawsBefore = player.getPlayerStats(gameOne).getDraws();
        int opponentDrawsBefore = opponent.getPlayerStats(gameOne).getDraws();

        MatchSummary summary = new MatchSummary(
                gameOne,
                GameOutcome.DRAW,
                player.getUUID(),
                opponent.getUUID()
        );
        assertTrue(StatisticsUpdater.recordMatch(summary));

        Database.readFromFile();

        User updatedPlayer = Database.getUserFromUUID(player.getUUID());
        User updatedOpponent = Database.getUserFromUUID(opponent.getUUID());

        assertEquals(playerDrawsBefore + 1, updatedPlayer.getPlayerStats(gameOne).getDraws());
        assertEquals(opponentDrawsBefore + 1, updatedOpponent.getPlayerStats(gameOne).getDraws());
    }

    /**
     * Test template last modified value is loaded correctly after reset
     */
    @Test
    public void testTemplateLastModifiedValueLoaded(){
        assertEquals(LocalDateTime.parse("2026-03-22T12:00:00"), Database.getLastModifiedDate());
    }
}