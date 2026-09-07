package ca.ucalgary.seng300;

public enum GameType {
    TIC_TAC_TOE("Tic-Tac-Toe"),
    CONNECT_FOUR("Connect Four");

    private final String displayName;

    private GameType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
