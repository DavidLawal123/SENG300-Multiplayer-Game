package ca.ucalgary.seng300.gamelogic.gamestate;

import ca.ucalgary.seng300.gamelogic.GameOutcome;
import ca.ucalgary.seng300.Player;

import java.util.Optional;

public interface GameStateView {
    public Optional<Player> getOccupancy(int row, int col);
    public Player getP1();
    public Player getP2();
    public Player getPlayerTurnState();
    public GameOutcome getGameOutcome();
    public Player getWinner();
    public Player getLoser();
}
