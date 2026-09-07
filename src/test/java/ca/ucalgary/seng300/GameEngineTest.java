package ca.ucalgary.seng300;

import ca.ucalgary.seng300.gamelogic.gameengine.GameEngine;
import ca.ucalgary.seng300.gamelogic.gameengine.GameEngineFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.gamelogic.context.MoveContext;
import ca.ucalgary.seng300.gamelogic.context.MoveContextFactory;
import ca.ucalgary.seng300.gamelogic.gamestate.GameState;
import ca.ucalgary.seng300.gamelogic.gamestate.GameStateFactory;
/**
 * This test class verifies the shared logic inside GameEngine.
 * Uses a concrete implementation (TTTGameEngine) to test abstract behavior.
 */
public class GameEngineTest {


    private Player createPlayer(String name) {
        return new Player(){
            private final UUID id = UUID.randomUUID();
            @Override
            public UUID getUUID(){
                return id;
            }
            @Override
            public String getUsername(){
                return name;
            }
        };
    }

    private GameEngine createEngine(GameType type){
        GameEngineFactory factory = new GameEngineFactory();
        return factory.buildGameEngine(type);
    }

    private GameState createGameState(Player p1, Player p2, GameType type){
        GameStateFactory factory = new GameStateFactory();
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        return factory.buildGameState(players, type);
    }

    private MoveContext createContext(GameState state, Player player, int row, int col){
        MoveContextFactory factory = new MoveContextFactory();
        return factory.buildCTX(row, col, player, state);
    }


    @Test
    public void testApplyGameOutcomeTransitionWin(){
        Player p1 = createPlayer("A");
        Player p2 = createPlayer("B");

        GameState gameState = createGameState(p1,p2, GameType.TIC_TAC_TOE);
        MoveContext ctx = createContext(gameState, p1,0,0);

        GameEngine engine = createEngine(GameType.TIC_TAC_TOE);
        engine.applyGameOutcomeTransition(ctx,GameOutcome.WIN);

        assertEquals(GameOutcome.WIN, gameState.getGameOutcome());
        assertEquals(p1, gameState.getWinner());
        assertEquals(p2, gameState.getLoser());
    }

    @Test
    public void testApplyGameOutcomeTransitionDraw(){
        Player p1 = createPlayer("A");
        Player p2 = createPlayer("B");

        GameState gameState = createGameState(p1,p2, GameType.TIC_TAC_TOE);
        MoveContext ctx = createContext(gameState, p1,0,0);

        GameEngine engine = createEngine(GameType.TIC_TAC_TOE);
        engine.applyGameOutcomeTransition(ctx,GameOutcome.DRAW);

        assertEquals(GameOutcome.DRAW, gameState.getGameOutcome());
    }

    @Test
    public void testApplyGameOutcomeTransitioningOngoing(){
        Player p1 = createPlayer("A");
        Player p2 = createPlayer("B");

        GameState gameState = createGameState(p1, p2, GameType.TIC_TAC_TOE);
        MoveContext ctx = createContext(gameState,p1, 0, 0);

        GameEngine engine = createEngine(GameType.TIC_TAC_TOE);
        engine.applyGameOutcomeTransition(ctx, GameOutcome.ONGOING);
        assertEquals(GameOutcome.ONGOING, gameState.getGameOutcome());
    }

    @Test
    public void testApplyPlayerTurnStateTransition(){
        Player p1 = createPlayer("A");
        Player p2 = createPlayer("B");

        GameState gameState = createGameState(p1,p2, GameType.TIC_TAC_TOE);
        MoveContext ctx = createContext(gameState, p1,0,0);
        GameEngine engine = createEngine(GameType.TIC_TAC_TOE);

        engine.applyPlayerTurnStateTransition(ctx, p2);
        assertEquals(p2, gameState.getPlayerTurnState());
    }

    @Test
    public void testApplyMoveTTT(){
        Player p1 = createPlayer("A");
        Player p2 = createPlayer("B");

        GameState gameState = createGameState(p1,p2, GameType.TIC_TAC_TOE);
        MoveContext ctx = createContext(gameState,p1,1,1);

        GameEngine engine = createEngine(GameType.TIC_TAC_TOE);
        engine.applyMove(ctx);
        assertTrue(gameState.getOccupancy(1,1).isPresent());
        assertEquals(p1, gameState.getOccupancy(1,1).get());
    }

    @Test
    public void testApplyMoveConnectFour(){
        Player p1 = createPlayer("A");
        Player p2 = createPlayer("B");

        GameState gameState = createGameState(p1,p2,GameType.CONNECT_FOUR);
        int row = 0;
        int col = 0;
        MoveContext ctx = createContext(gameState,p1,row,col);
        GameEngine engine = createEngine(GameType.CONNECT_FOUR);
        engine.applyMove(ctx);
        assertTrue(gameState.getOccupancy(row,col).isPresent());
        assertEquals(p1,gameState.getOccupancy(row,col).get());
    }
}
