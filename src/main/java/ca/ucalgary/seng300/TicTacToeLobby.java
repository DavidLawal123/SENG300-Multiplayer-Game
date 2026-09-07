package ca.ucalgary.seng300;

public final class TicTacToeLobby extends Lobby {
    private static final int TICTACTOE_CAPACITY = 2;

    public TicTacToeLobby(User owner) {
        super(owner, TICTACTOE_CAPACITY);
    }
}