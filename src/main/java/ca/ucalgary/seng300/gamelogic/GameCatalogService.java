package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.GameType;
import ca.ucalgary.seng300.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameCatalogService {
    private final List<GameInfo> gamesList;
    private final GameSessionFactory gameSessionFactory;

    public GameCatalogService() {
        this.gameSessionFactory = new GameSessionFactory();

        // Simple default catalog for Iteration 1.
        List<GameInfo> tmp = new ArrayList<>();
        tmp.add(new GameInfo("Tic-Tac-Toe (2 players)", 2, GameType.TIC_TAC_TOE));
        tmp.add(new GameInfo("Connect Four (2 players)", 2, GameType.CONNECT_FOUR));
        this.gamesList = tmp;
    }

    public GameSession startMatch(GameType gameType, List<? extends Player> players) {
        Objects.requireNonNull(gameType);
        Objects.requireNonNull(players);

        if (players.size() != 2) {
            throw new IllegalArgumentException("startMatch requires exactly 2 players.");
        }

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        List<Player> playerList = List.of(p1,p2);

        return gameSessionFactory.buildGameSession(gameType, playerList);
    }

    public List<GameInfo> getGamesList() {
        return new ArrayList<>(gamesList);
    }

    public GameInfo getGameInfo(UUID uuid) {
        Objects.requireNonNull(uuid);

        for (GameInfo gi : gamesList) {
            if (gi.getUuid().equals(uuid)) {
                return gi;
            }
        }
        throw new IllegalArgumentException("No GameInfo found for uuid: " + uuid);
    }
}