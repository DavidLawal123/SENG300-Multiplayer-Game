package ca.ucalgary.seng300.gamelogic;

import ca.ucalgary.seng300.GameType;

import java.util.UUID;

public class GameInfo {
    private UUID uuid = UUID.randomUUID();
    private String description;
    private int numPlayersRequired;
    private GameType gameType;

    public GameInfo(String description, int numPlayersRequired, GameType gameType) {
        this.description = description;
        this.numPlayersRequired = numPlayersRequired;
        this.gameType = gameType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public int getNumPlayersRequired() {
        return numPlayersRequired;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumPlayersRequired(int numPlayersRequired) {
        this.numPlayersRequired = numPlayersRequired;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
