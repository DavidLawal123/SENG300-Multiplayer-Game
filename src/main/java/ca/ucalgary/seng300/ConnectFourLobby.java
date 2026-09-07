package ca.ucalgary.seng300;

public final class ConnectFourLobby extends Lobby {
    private static final int CONNECTFOUR_CAPACITY = 2; // or 4 if you ever support teams

    public ConnectFourLobby(User owner) {
        super(owner, CONNECTFOUR_CAPACITY);
    }
}
