package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class GameSessionFactoryTests {

    private Player makingPlayer(String s) {
        UUID uuid = UUID.randomUUID();
        return new Player() {
            public UUID getUUID() { return uuid; }
            public String getUsername() { return s; }
        };
    }


    /**
     * This Tests that Factory builds a session.
     */
    @Test
    public void buildGameSessionWorks() {
        GameSessionFactory sessionFactory = new GameSessionFactory();

        GameSession gameSession = sessionFactory.buildGameSession(
                GameType.TIC_TAC_TOE,
                Arrays.asList(makingPlayer("a"), makingPlayer("b"))
        );

        assertNotNull(gameSession);
    }

    /**
     * This Tests that null game type throws exception.
     */
    @Test(expected = NullPointerException.class)
    public void nullGameTypeThrows() {
        new GameSessionFactory().buildGameSession(null, new ArrayList<>());}

    /**
     *This Tests that null players throws exception.
     */
    @Test(expected = NullPointerException.class)
    public void nullPlayersThrows() {
        new GameSessionFactory().buildGameSession(GameType.TIC_TAC_TOE, null);}
}