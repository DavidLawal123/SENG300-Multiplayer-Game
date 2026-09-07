package ca.ucalgary.seng300.gamelogic.context;

import ca.ucalgary.seng300.Player;

public class ClientMoveRequestFactory {
    public ClientMoveRequestFactory(){}

    public ClientMoveRequest buildRequest(int row, int col, Player player){
        return new ClientMoveRequest(row, col, player);
    }
}
